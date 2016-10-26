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
		new File(Main.instance().getPath().toFile(), "commands").mkdirs();
		
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

		if (!Help.get("effect").isPresent()) {
			Usage usage = new Usage(Argument.of("<player>", "Specifies the targetted player"))
					.addArgument(Argument.of("<effect>", "Specifies the effect to grant. Must be a status effect id (for example, 1 or minecraft:speed). Set to 'clear' to remove all effects"))
					.addArgument(Argument.of("[seconds]", "Specifies the effect's duration in seconds. Must be between 0 and 1,000,000 (inclusive, without the comas). If not specified, defaults to 30 seconds."))
					.addArgument(Argument.of("[amplifier]", "Specifies the number of additional levels to add to the effect. Must be between 0 and 255 (inclusive). If not specified, defaults to 0. "
							+ "Note that the first tier of a status effect (e.g. Regeneration I) is 0, so the second tier, for example Regeneration II, would be specified by an amplifier level of 1."))
					.addArgument(Argument.of("[hideParticles]", "Specifies whether the particles of the status effect should be hidden. Must be either true or false. If not specified, defaults to false"));

			Help help = new Help("effect", "effect", "The effect command manages status effects on players and other entities.")
					.setPermission("minecraft.command.effect")
					.setUsage(usage)
					.addExample("/effect MonroeTT minecraft:speed 60");

			writeJson(gson, help);

			Help.register(help);
		}
		
		if (!Help.get("enchant").isPresent()) {
			Usage usage = new Usage(Argument.of("<player>", "Specifies the targetted player"))
					.addArgument(Argument.of("<enchantment ID>", "Specifies the enchantment to be added to the item held by the target. Must be a valid enchantment ID (for example, 16 or minecraft:sharpness)."))
					.addArgument(Argument.of("[level]", "Specifies the enchantment level. Must be at least 1 and not greater than the maximum level for the specified enchantment. If not specified, defaults to 1."));

			Help help = new Help("enchant", "enchant", "Adds an enchantment to a player's selected item, subject to the same restrictions as an anvil.")
					.setPermission("minecraft.command.enchant")
					.setUsage(usage)
					.addExample("/enchant MonroeTT minecraft:speed 60");

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

		if (!Help.get("particle").isPresent()) {
			Usage usage = new Usage(Argument.of("<name>", "Specifies the particle to create. Must be a particle name (for example, explode)."))
					.addArgument(Argument.of("<x> <y> <z>", "Specifies the position at which to create the particle. All values (including y) must be between -30,000,000 and 30,000,000"
							+ " (inclusive, without the commas). May use tilde notation to specify a position relative to the command's execution."))
					.addArgument(Argument.of("<xd> <yd> <zd>", "Specifies the size of the 3-dimensional cuboid volume to spawn particles in, centered on position x y z, and divided "
							+ "by about 8 (using 1 1 1 specifies a cuboid of about 8×8×8 in size)."))
					.addArgument(Argument.of("<speed>", "Specifies the speed of the particle. Must be at least 0."))
					.addArgument(Argument.of("[count]", "Specifies the number of particle effects to create. Must be at least 0 (which produces one particle)."))
					.addArgument(Argument.of("[mode]", "Specifies the display mode. May be anything but only force will have an effect: to allow the particle(s) to be seen up to "
							+ "256 blocks away and by players that use the minimal particles setting."))
					.addArgument(Argument.of("[player]", "Allows control of which player should view this particle instead of everyone in the viewing range of the particle."))
					.addArgument(Argument.of("[params ...]", "Allows blockdust, iconcrack, blockcrack and fallingdust to be modified to view a specific item or block. Two parameters "
							+ "are accepted for iconcrack."));

			Help help = new Help("particle", "particle", "Creates particles.")
					.setPermission("minecraft.command.particle")
					.setUsage(usage)
					.addExample("/particle explode 150 75 -643 0 0 0 0 MonroeTT");

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
		
		if (!Help.get("playsound").isPresent()) {
			Usage usage = new Usage(Argument.of("<sound>", "Specifies the sound to play. Must be a sound event defined in sounds.json (for example, mob.pig.say)."))
					.addArgument(Argument.of("<source>", "Specifies which category in the music & sound options the sound falls under. Must be master, music, record, weather, block, hostile, neutral, player, ambient, or voice."))
					.addArgument(Argument.of("<player>", "Specifies the sound's target. Must be a player name or a target selector."))
					.addArgument(Argument.of("[x] [y] [z]", "Specifies the position to play the sounds from. May use tilde notation to specify a position relative to the target(s)."))
					.addArgument(Argument.of("[volume]", "Specifies the distance that the sound can be heard. Must be at least 0.0. For values less than 1.0, the sound will be quieter and have"
							+ " a smaller sphere within which it may be heard. For values greater than 1.0, the sound will not actually grow louder, but its audible range (a 16-block radius at 1.0)"
							+ " will be multiplied by volume. There will always be a gradual falloff to silence based on distance from the center of the sphere."))
					.addArgument(Argument.of("[pitch] ", "Specifies the pitch of the sound. Must be between 0.0 and 2.0 (inclusive), and values less than 0.5 are equivalent to 0.5. "
							+ "Values lower than 1.0 lower the pitch and increase the duration; values greater than 1.0 raise the pitch and reduce the duration. The pitch value is a multiplier "
							+ "applied to the frequency, so if a value between 0.5 and 1.0 (inclusive) is doubled, the pitch will be an octave higher. (If you're a musician wishing to convert other "
							+ "intervals to pitch values, see Note block#Usage, but be aware that 1.0 won't be F♯ for all sound effects.) If not specified, defaults to 1.0."))
					
					.addArgument(Argument.of("[minimumVolume]", "Specifies the volume for targets outside the sound's normal audible sphere. If a target is outside the normal sphere, the "
							+ "sound will instead be centered some short distance from the target (less than four blocks away), and minimumVolume will determine its volume. "
							+ "Must be between 0.0 and 1.0 (inclusive)."));

			Help help = new Help("playsound", "playsound", "Plays a sound.")
					.setPermission("minecraft.command.playsound")
					.setUsage(usage)
					.addExample("/playsound mob.pig.say master MonroeTT");

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
		
		if (!Help.get("scoreboard").isPresent()) {

			Help scoreObjList = new Help("scoreboard objectives list", "list", "Lists all existing objectives, with their display name and criteria")
					.setPermission("minecraft.command.scoreboard");
			
			Usage usageScoreObjAdd = new Usage(Argument.of("<name>"))
					.addArgument(Argument.of("<criteria>"))
					.addArgument(Argument.of("[display name...]"));
			
			Help scoreObjAdd = new Help("scoreboard objectives add", "add", "Creates a new objective with the internal name name, specified criteria, and the optional display name. Without a specified display name, it will default to name. See above section for the meaning of these properties. All arguments are case-sensitive.")
					.setUsage(usageScoreObjAdd)
					.setPermission("minecraft.command.scoreboard");
			
			Usage usageScoreObjRemove = new Usage(Argument.of("<name>"));
			
			Help scoreObjRemove = new Help("scoreboard objectives remove", "remove", "Deletes all references to the objective with name in the scoreboard system. Data is deleted from the objectives list, player scores, and if it was on a display list, it will no longer be displayed.")
					.setUsage(usageScoreObjRemove)
					.setPermission("minecraft.command.scoreboard");
			
			Usage usageScoreObjDisplay = new Usage(Argument.of("<slot>"))
					.addArgument(Argument.of("[objective]"));
			
			Help scoreObjDisplay = new Help("scoreboard objectives setdisplay", "setdisplay", "Displays score info for objective in the specified slot. Valid slots are listed and described in Display Slots. Note that the objective parameter is optional: if no objective is provided, this display slot is cleared (returned to its default state).")
					.setUsage(usageScoreObjDisplay)
					.setPermission("minecraft.command.scoreboard");
			
			Help scoreObj = new Help("scoreboard objectives", "objectives", "Objectives sub command")
					.setPermission("minecraft.command.scoreboard")
					.addChild(scoreObjDisplay)
					.addChild(scoreObjRemove)
					.addChild(scoreObjAdd)
					.addChild(scoreObjList);
			
			Usage usageScorePlayerOperation = new Usage(Argument.of("<targetName>"))
					.addArgument(Argument.of("<targetObjective>"))
					.addArgument(Argument.of("<operation>"))
					.addArgument(Argument.of("<selector>"))
					.addArgument(Argument.of("<objective>"));
			
			Help scorePlayerOperation = new Help("scoreboard players operation", "operation", "Applies an arithmetic operation altering targetName's score in targetObjective, using selector's score in objective as input.")
					.setPermission("minecraft.command.scoreboard")
					.setUsage(usageScorePlayerOperation);
			
			Usage usageScorePlayerTest = new Usage(Argument.of("<player>"))
					.addArgument(Argument.of("<objective>"))
					.addArgument(Argument.of("<min>"))
					.addArgument(Argument.of("[max]"));
			
			Help scorePlayerTest = new Help("scoreboard players test", "test", "Outputs whether or not player's score in objective is within the range min to max (inclusive). If not specified or if '*' is used, max defaults to 2,147,483,647. Using a '*' for min means -2,147,483,648. '*' may be used to represent all players tracked by the scoreboard.")
					.setPermission("minecraft.command.scoreboard")
					.setUsage(usageScorePlayerTest);
			
			Usage usageScorePlayerEnable = new Usage(Argument.of("<player>"))
					.addArgument(Argument.of("<trigger>"));
			
			Help scorePlayerEnable = new Help("scoreboard players enable", "enable", "Enables player to use the /trigger command on the specified trigger objective. Until this has been done, player's attempts to /trigger that objective will fail. Once they have used the /trigger command on it, it will be disabled again. '*' may be used to represent all players tracked by the scoreboard.")
					.setPermission("minecraft.command.scoreboard")
					.setUsage(usageScorePlayerEnable);
			
			Usage usageScorePlayerReset = new Usage(Argument.of("<player>"))
					.addArgument(Argument.of("<objective>"));
			
			Help scorePlayerReset = new Help("scoreboard players reset", "reset", "Deletes score or all scores for player. If objective is specified, only that objective is cleared; otherwise this applies to all objectives. Note this does not merely set the score(s) to 0: it removes the player from the scoreboard altogether (or for the given objective). '*' may be used to represent all players tracked by the scoreboard.")
					.setPermission("minecraft.command.scoreboard")
					.setUsage(usageScorePlayerReset);
			
			Usage usageScorePlayerRemove = new Usage(Argument.of("<player>"))
					.addArgument(Argument.of("<objective>"))
					.addArgument(Argument.of("<count>"))
					.addArgument(Argument.of("[dataTag]"));
			
			Help scorePlayerRemove = new Help("scoreboard players remove", "remove", "Decrements the player's score in objective by count. '*' may be used to represent all players tracked by the scoreboard.")
					.setPermission("minecraft.command.scoreboard")
					.setUsage(usageScorePlayerRemove);

			Help scorePlayerAdd = new Help("scoreboard players add", "add", "Increments the player's score in objective by count. '*' may be used to represent all players tracked by the scoreboard.")
					.setPermission("minecraft.command.scoreboard")
					.setUsage(usageScorePlayerRemove);
			
			Usage usageScorePlayerSet = new Usage(Argument.of("<player>"))
					.addArgument(Argument.of("<objective>"))
					.addArgument(Argument.of("<score>"))
					.addArgument(Argument.of("[dataTag]"));
			
			Help scorePlayerSet = new Help("scoreboard players set", "set", "Sets the player's score in objective to score, overwriting the previous score if it exists. '*' may be used in place of player to represent every player tracked by the scoreboard.")
					.setPermission("minecraft.command.scoreboard")
					.setUsage(usageScorePlayerSet);
			
			Usage usageScorePlayerList = new Usage(Argument.of("[playername]"));
			
			Help scorePlayerList = new Help("scoreboard players list", "list", "Displays all players who are tracked, in some way, by the scoreboard system. The optional playername parameter can be used to display all scores of a particular player, and '*' (an asterisk) in place of playername will display all scores for all players tracked by the scoreboard system.")
					.setPermission("minecraft.command.scoreboard")
					.setUsage(usageScorePlayerList);
			
			Help scorePlayerTag = new Help("scoreboard players tag", "tag", "")
					.setPermission("minecraft.command.scoreboard");
			
			Help scorePlayer = new Help("scoreboard players", "players", "Players sub command")
					.setPermission("minecraft.command.scoreboard")
					.addChild(scorePlayerList)
					.addChild(scorePlayerSet)
					.addChild(scorePlayerAdd)
					.addChild(scorePlayerRemove)
					.addChild(scorePlayerReset)
					.addChild(scorePlayerEnable)
					.addChild(scorePlayerTest)
					.addChild(scorePlayerOperation)
					.addChild(scorePlayerTag);

			Help help = new Help("scoreboard", "scoreboard", "Scoreboard base comand.")
					.setPermission("minecraft.command.scoreboard")
					.addChild(scorePlayer)
					.addChild(scoreObj);

			writeJson(gson, help);

			Help.register(help);
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
