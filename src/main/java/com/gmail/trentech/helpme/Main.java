package com.gmail.trentech.helpme;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

import com.gmail.trentech.helpme.commands.CMDHelp;
import com.gmail.trentech.helpme.utils.Resource;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.inject.Inject;

import me.flibio.updatifier.Updatifier;

@Updatifier(repoName = Resource.NAME, repoOwner = Resource.AUTHOR, version = Resource.VERSION)
@Plugin(id = Resource.ID, name = Resource.NAME, version = Resource.VERSION, description = Resource.DESCRIPTION, authors = Resource.AUTHOR, url = Resource.URL, dependencies = { @Dependency(id = "Updatifier", optional = true) })
public class Main {

	@Inject
	@ConfigDir(sharedRoot = false)
	private Path path;

	@Inject
	private Logger log;

	private static PluginContainer plugin;
	private static Main instance;

	@Listener
	public void onPreInitializationEvent(GamePreInitializationEvent event) {
		plugin = Sponge.getPluginManager().getPlugin(Resource.ID).get();
		instance = this;

		try {
			Files.createDirectories(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Listener
	public void onInitialization(GameInitializationEvent event) {
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
			Help help = new Help("clear", "clear", "Clears items from player inventory.").setPermission("minecraft.command.clear").setUsage("/clear [player] [item] [data] [maxCount] [dataTag]").setExample("/clear MonroeTT minecraft:redstone");

			writeJson(gson, help);

			Help.register(help);
		}

		if (!Help.get("difficulty").isPresent()) {
			Help help = new Help("difficulty", "difficulty", "Sets the difficulty level (peaceful, easy, etc.).").setPermission("minecraft.command.difficulty").setUsage("/difficulty <difficulty>").setExample("/difficulty hard");

			writeJson(gson, help);

			Help.register(help);
		}

		if (!Help.get("gamemode").isPresent()) {
			Help help = new Help("gamemode", "gamemode", "Sets a player's game mode.").setPermission("minecraft.command.gamemode").setUsage("/gamemode <mode> [player]").setExample("/gamemode SURVIVAL");

			writeJson(gson, help);

			Help.register(help);
		}

		if (!Help.get("gamerule").isPresent()) {
			Help help = new Help("gamerule", "gamerule", "Sets or queries a game rule value.").setPermission("minecraft.command.gamerule").setUsage("/gamerule <rule name> [value]").setExample("/gamerule doDaylightCycle false");

			writeJson(gson, help);

			Help.register(help);
		}

		if (!Help.get("give").isPresent()) {
			Help help = new Help("give", "give", "Gives an item to a player.").setPermission("minecraft.command.give").setUsage("/give <player> <item> [amount] [data] [dataTag]").setExample("/give MonroeTT minecraft:diamond 64");

			writeJson(gson, help);

			Help.register(help);
		}

		if (!Help.get("kill").isPresent()) {
			Help help = new Help("kill", "kill", "Kills entities (players, mobs, items, etc.).").setPermission("minecraft.command.kill").setUsage("/kill [player|entity]").setExample("/kill MonroeTT");

			writeJson(gson, help);

			Help.register(help);
		}

		if (!Help.get("kick").isPresent()) {
			Help help = new Help("kick", "kick", "Kicks a player off a server.").setPermission("minecraft.command.kick").setUsage("/kick <player> [reason ...]").setExample("/kick MonroeTT stop being a jerk");

			writeJson(gson, help);

			Help.register(help);
		}

		if (!Help.get("list").isPresent()) {
			Help help = new Help("list", "list", "Lists players on the server.").setPermission("minecraft.command.list").setUsage("/list [uuids]");

			writeJson(gson, help);

			Help.register(help);
		}

		if (!Help.get("pardon").isPresent()) {
			Help help = new Help("pardon", "pardon", "Removes entries from the banlist.").setPermission("minecraft.command.pardon").setUsage("/pardon <name>").setExample("/pardon MonroeTT");

			writeJson(gson, help);

			Help.register(help);
		}
		
		if(!Help.get("say").isPresent()) {
			Help help = new Help("say", "say", "Sends a message in the chat to other players.").setPermission("minecraft.command.say").setUsage("/say <message ...>").setExample("/say Hello world!");
			
			writeJson(gson, help);
			
			Help.register(help);
		}

		if(!Help.get("sponge").isPresent()) {
			Help spongeReload = new Help("sponge reload", "reload", "Asks plugins to perform their own reload procedures.").setPermission("sponge.command.reload").setUsage("/sponge reload");
			Help spongeAudit = new Help("sponge audit", "audit", "Forces loading of unloaded classes to enable mixin debugging.").setPermission("sponge.command.audit").setUsage("/sponge audit");
			Help spongeTimingsCost = new Help("sponge timings cost", "cost", "Gets the cost of using timings.").setPermission("sponge.command.timings").setUsage("/sponge timings cost");
			Help spongeTimingsVerboff = new Help("sponge timings verboff", "verboff", "Disables timings monitoring at the verbose level. Note that high-frequency timings will not be available.").setPermission("sponge.command.timings").setUsage("/sponge timings verboff");	
			Help spongeTimingsVerbon = new Help("sponge timings verbon", "verbon", "Enables timings monitoring at the verbose level.").setPermission("sponge.command.timings").setUsage("/sponge timings verbon");	
			Help spongeTimingsReport = new Help("sponge timings report", "report", "Generates the timings report on your server performance at http://timings.aikar.co").setPermission("sponge.command.timings").setUsage("/sponge timings report");
			Help spongeTimingsReset = new Help("sponge timings reset", "reset", "Resets all timing data and begins recording timing data after the time this command was done.").setPermission("sponge.command.timings").setUsage("/sponge timings reset");
			Help spongeTimingsOff = new Help("sponge timings off", "off", "Disables timings. Note that most timings commands will not function and timings will not be recorded if timings are disabled.").setPermission("sponge.command.timings").setUsage("/sponge timings off");
			Help spongeTimingsOn = new Help("sponge timings on", "on", "Enables timings. Note that this will also reset timings data.").setPermission("sponge.command.timings").setUsage("/sponge timings on");
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
		
		if (!Help.get("weather").isPresent()) {
			Help help = new Help("weather", "weather", "Sets the weather.").setPermission("minecraft.command.weather").setUsage("/weather <clear|rain|thunder> [duration]").setExample("/weather clear 9000");

			writeJson(gson, help);

			Help.register(help);
		}

		if (!Help.get("teleport").isPresent()) {
			Help help = new Help("teleport", "teleport", "Teleports entities (players, mobs, items, etc.).").setPermission("minecraft.command.teleport").setUsage("/teleport <target entity> <x> <y> <z> [<y-rot> <x-rot>]").setExample("/teleport MonroeTT -150 76 456");

			writeJson(gson, help);

			Help.register(help);
		}
		
		Sponge.getEventManager().registerListeners(this, new EventListener());
		Sponge.getCommandManager().register(this, CMDHelp.cmdHelp, "helpme", "hm");
	}
	
	@Listener
	public void onReloadEvent(GameReloadEvent event) {
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
	}
	
	public Logger getLog() {
		return log;
	}

	public Path getPath() {
		return path;
	}

	private void writeJson(Gson gson, Help help) {
		File file = new File(getPath().toFile(), help.getRawCommand() + ".json");
		
		if(!file.exists()) {
			try (Writer writer = new FileWriter(file)) {
			    gson.toJson(help, writer);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static PluginContainer getPlugin() {
		return plugin;
	}

	public static Main instance() {
		return instance;
	}

}