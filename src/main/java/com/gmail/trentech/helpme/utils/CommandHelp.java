package com.gmail.trentech.helpme.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import com.gmail.trentech.helpme.Main;
import com.gmail.trentech.helpme.help.Argument;
import com.gmail.trentech.helpme.help.Help;
import com.gmail.trentech.helpme.help.Usage;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class CommandHelp {

	public static void init() {
		Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().setFieldNamingPolicy(FieldNamingPolicy.IDENTITY).create();

		try (Stream<Path> paths = Files.walk(Main.instance().getPath())) {
			paths.forEach(path -> {
				File file = path.toFile();
				
				if(file.getName().endsWith(".json")) {
					try {
						Help.register(gson.fromJson(new FileReader(file), Help.class));
					} catch (JsonSyntaxException | JsonIOException | FileNotFoundException e) {
						e.printStackTrace();
					}
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (!Help.get("clear").isPresent()) {
			Usage usage = new Usage(Argument.of("[player]", "Must be a player name or a target selector. If not specified, defaults to the player who executed the command. Not optional in command blocks."))
					.addArgument(Argument.of("[item]", "Specifies the id of the item to be cleared. If not specified, all items in the player's inventory are cleared."))
					.addArgument(Argument.of("[data]", "Specifies the data value of the item to be cleared. If not specified, or if -1, all items that match item are cleared, regardless of their data."))
					.addArgument(Argument.of("[maxCount]", "Specifies the maximum number of items to be cleared. If not specified, or if -1, all items that match item and data are cleared. If 0, does not clear items, but returns successfully if there were items that could have been cleared"))
					.addArgument(Argument.of("[dataTag]", "Specifies the data tags of the item to be cleared (only items matching the specified data tags will be cleared. Data tags not specified will not be considered). Must be a compound NBT tag (for example, {display:{Name:Fred}})."));
			
			Help help = new Help("clear", "clear", "Clears items from player inventory.")
					.setPermission("minecraft.command.clear")
					.setUsage(usage)
					.addExample("/clear MonroeTT minecraft:redstone");

			writeJson(gson, help);

			Help.register(help);
		}

		if (!Help.get("difficulty").isPresent()) {
			Usage usage = new Usage(Argument.of("<difficulty>", "Specifies the new difficulty level. Must be one of the following: peaceful(0), easy(1), normal(2), hard(3)"));

			Help help = new Help("difficulty", "difficulty", "Sets the difficulty level (peaceful, easy, etc.).")
					.setPermission("minecraft.command.difficulty")
					.setUsage(usage)
					.addExample("/difficulty hard");

			writeJson(gson, help);

			Help.register(help);
		}

		if (!Help.get("gamemode").isPresent()) {
			Usage usage = new Usage(Argument.of("<mode>", "Must be one of the following: survival(0), creative(1), adventure(2), spectator(3)"))
					.addArgument(Argument.of("[player]", "If specified, must be either a player's username or a target selector. If unspecified, defaults to the player using the command."));
			
			Help help = new Help("gamemode", "gamemode", "Sets a player's game mode.")
					.setPermission("minecraft.command.gamemode")
					.setUsage(usage)
					.addExample("/gamemode SURVIVAL");

			writeJson(gson, help);

			Help.register(help);
		}

		if (!Help.get("gamerule").isPresent()) {
			Usage usage = new Usage(Argument.of("<rule name>","Specifies the game rule to set or query. May be any value, but only certain predefined game rules will affect gameplay."))
					.addArgument(Argument.of("[value]","Specifies the value to set the game rule to. May be any value, though only true or false specified for predefined game rules will actually affect gameplay, except in the case of randomTickSpeed and spawnRadius, where any integer 0 or greater will affect gameplay"));
			
			Help help = new Help("gamerule", "gamerule", "Sets or queries a game rule value.")
					.setPermission("minecraft.command.gamerule")
					.setUsage(usage)
					.addExample("/gamerule doDaylightCycle false");

			writeJson(gson, help);

			Help.register(help);
		}

		if (!Help.get("give").isPresent()) {
			Usage usage = new Usage(Argument.of("<player>", "Specifies the target to give item(s) to. Must be a player name or target selector."))
					.addArgument(Argument.of("<item>", "Specifies the item to give. Must be a valid item id (for example, minecraft:iron_shovel), or block id for which items exist. Numerical ids are unsupported."))
					.addArgument(Argument.of("[amount]", "Specifies the number of items to give. Must be between 1 and 64 (inclusive), but can be 64 even when that's more than one stack. If not specified, defaults to 1."))
					.addArgument(Argument.of("[data]", "Specifies the item data of the given item(s). Must be an integer between -2,147,483,648 and 2,147,483,647 (inclusive, without the commas), but values which are invalid for the specified item id revert to 0. If not specified, defaults to 0."))
					.addArgument(Argument.of("[dataTag]", "Specifies the data tag of the given item(s). Must be a compound NBT tag (for example, {display:{Name:Fred}})."));
			
			Help help = new Help("give", "give", "Gives an item to a player.")
					.setPermission("minecraft.command.give")
					.setUsage(usage)
					.addExample("/give MonroeTT minecraft:diamond 64");

			writeJson(gson, help);

			Help.register(help);
		}

		if (!Help.get("kill").isPresent()) {
			Usage usage = new Usage(Argument.of("[player|entity]", "Specifies the target(s) to be killed (including 'non-living' entities like items, vehicles, etc.). Must be a player name or a target selector. If not specified, defaults to the command's user. Not optional in command blocks"));

			Help help = new Help("kill", "kill", "Kills entities (players, mobs, items, etc.).")
					.setPermission("minecraft.command.kill")
					.setUsage(usage)
					.addExample("/kill MonroeTT");

			writeJson(gson, help);

			Help.register(help);
		}

		if (!Help.get("kick").isPresent()) {
			Usage usage = new Usage(Argument.of("<player>"))
					.addArgument(Argument.of("[reason ...]"));
			
			Help help = new Help("kick", "kick", "Kicks a player off a server.")
					.setPermission("minecraft.command.kick")
					.setUsage(usage)
					.addExample("/kick MonroeTT stop being a jerk");

			writeJson(gson, help);

			Help.register(help);
		}

		if (!Help.get("list").isPresent()) {
			Usage usage = new Usage(Argument.of("[uuids]", "If 'uuids' is specified, player UUIDs will be shown alongside names"));

			Help help = new Help("list", "list", "Lists players on the server.")
					.setPermission("minecraft.command.list")
					.setUsage(usage);

			writeJson(gson, help);

			Help.register(help);
		}

		if (!Help.get("pardon").isPresent()) {
			Usage usage = new Usage(Argument.of("<name>", "Specifies the name to remove from the banlist."));

			Help help = new Help("pardon", "pardon", "Removes entries from the banlist.")
					.setPermission("minecraft.command.pardon")
					.setUsage(usage)
					.addExample("/pardon MonroeTT");

			writeJson(gson, help);

			Help.register(help);
		}
		
		if (!Help.get("pardon-ip").isPresent()) {
			Usage usage = new Usage(Argument.of("<address>", "Specifies the IP address to remove from the banlist. Must be a valid IP address."));

			Help help = new Help("pardon-ip", "pardon-ip", "Removes entries from the banlist.")
					.setPermission("minecraft.command.pardon")
					.setUsage(usage)
					.addExample("/pardon-ip 64.94.205.56");

			writeJson(gson, help);

			Help.register(help);
		}
		
		if(!Help.get("say").isPresent()) {
			Usage usage = new Usage(Argument.of("<message …>"));

			Help help = new Help("say", "say", "Sends a message in the chat to other players.")
					.setPermission("minecraft.command.say")
					.setUsage(usage)
					.addExample("/say Hello world!");
			
			writeJson(gson, help);
			
			Help.register(help);
		}

		if (!Help.get("weather").isPresent()) {
			Usage usage = new Usage(Argument.of("<clear|rain|thunder>", "clear: Set the weather to clear, rain: Set the weather to rain (or snow in cold biomes), thunder: Set the weather to a thunderstorm (or a thunder snowstorm in cold biomes)"))
					.addArgument(Argument.of("[duration]", "Specifies the time in seconds for the specified weather to last. Must be between 1 and 1,000,000 (inclusive, without the commas). If duration is omitted, the duration will be set between 6,000 and 18,000 ticks."));
			
			Help help = new Help("weather", "weather", "Sets the weather.")
					.setPermission("minecraft.command.weather")
					.setUsage(usage)
					.addExample("/weather clear 9000");

			writeJson(gson, help);

			Help.register(help);
		}

		if (!Help.get("teleport").isPresent()) {
			Usage usage = new Usage(Argument.of("<target entity>", "Specifies the entity(s) to be teleported. Must be either a player name or a target selector."))
					.addArgument(Argument.of("<x> <y> <z>", "Specifies the coordinates to teleport the target(s) to. x and z must fall within the range -30,000,000 to 30,000,000 (exclusive, without the commas), and y must be within the range -4096 to 4096 inclusive. May use tilde notation to specify a position relative to the position of the executor of the command (use /tp or /execute to teleport using coordinates relative to the target instead)."))
					.addArgument(Argument.of("[<y-rot>", "Specifies the horizontal rotation (-180.0 for due north, -90.0 for due east, 0.0 for due south, 90.0 for due west, to 179.9 for just west of due north, before wrapping back around to -180.0). Tilde notation can be used to specify a rotation relative to the target's previous rotation."))
					.addArgument(Argument.of("<x-rot>]", "Specifies the vertical rotation (-90.0 for straight up to 90.0 for straight down). Tilde notation can be used to specify a rotation relative to the target's previous rotation."));
			
			Help help = new Help("teleport", "teleport", "Teleports entities (players, mobs, items, etc.).")
					.setPermission("minecraft.command.teleport")
					.setUsage(usage)
					.addExample("/teleport MonroeTT -150 76 456");

			writeJson(gson, help);

			Help.register(help);
		}
		
		if(!Help.get("sponge").isPresent()) {
			
			Help spongeReload = new Help("sponge reload", "reload", "Asks plugins to perform their own reload procedures.").setPermission("sponge.command.reload");
			Help spongeAudit = new Help("sponge audit", "audit", "Forces loading of unloaded classes to enable mixin debugging.").setPermission("sponge.command.audit");
			Help spongeTimingsCost = new Help("sponge timings cost", "cost", "Gets the cost of using timings.").setPermission("sponge.command.timings");
			Help spongeTimingsVerboff = new Help("sponge timings verboff", "verboff", "Disables timings monitoring at the verbose level. Note that high-frequency timings will not be available.").setPermission("sponge.command.timings");	
			Help spongeTimingsVerbon = new Help("sponge timings verbon", "verbon", "Enables timings monitoring at the verbose level.").setPermission("sponge.command.timings");	
			Help spongeTimingsReport = new Help("sponge timings report", "report", "Generates the timings report on your server performance at http://timings.aikar.co").setPermission("sponge.command.timings");
			Help spongeTimingsReset = new Help("sponge timings reset", "reset", "Resets all timing data and begins recording timing data after the time this command was done.").setPermission("sponge.command.timings");
			Help spongeTimingsOff = new Help("sponge timings off", "off", "Disables timings. Note that most timings commands will not function and timings will not be recorded if timings are disabled.").setPermission("sponge.command.timings");
			Help spongeTimingsOn = new Help("sponge timings on", "on", "Enables timings. Note that this will also reset timings data.").setPermission("sponge.command.timings");
			Help spongeTimings = new Help("sponge timings", "timings", "The main command for the timings module.").setPermission("sponge.command.timings")
					.addChild(spongeTimingsCost)
					.addChild(spongeTimingsVerboff)
					.addChild(spongeTimingsVerbon)
					.addChild(spongeTimingsReport)
					.addChild(spongeTimingsReset)
					.addChild(spongeTimingsOff)
					.addChild(spongeTimingsOn);

			Help sponge = new Help("sponge", "sponge", "The main command for Sponge.").setPermission("sponge.command").addChild(spongeTimings).addChild(spongeAudit).addChild(spongeReload);		
					
			writeJson(gson, sponge);
			
			Help.register(sponge);
		}
	}
	

	private static void writeJson(Gson gson, Help help) {
		File file = new File(Main.instance().getPath().toFile(), "commands/" + help.getRawCommand() + ".json");
		
		if(!file.exists()) {
			try (Writer writer = new FileWriter(file)) {
			    gson.toJson(help, writer);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
