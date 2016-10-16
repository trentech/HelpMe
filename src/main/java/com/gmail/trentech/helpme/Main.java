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

		Help help = new Help("clear", "clear", "Clears items from player inventory.", false)
				.setPermission("minecraft.command.clear")
				.setUsage("clear [player] [item] [data] [maxCount] [dataTag]")
				.setExample("/clear MonroeTT minecraft:redstone");
		
		writeJson(gson, help);

		help = new Help("difficulty", "difficulty", "Sets the difficulty level (peaceful, easy, etc.).", false)
				.setPermission("minecraft.command.difficulty")
				.setUsage("difficulty <difficulty>")
				.setExample("/difficulty hard");
		
		writeJson(gson, help);
		
		help = new Help("gamemode", "gamemode", "Sets a player's game mode.", false)
				.setPermission("minecraft.command.gamemode")
				.setUsage("gamemode <mode> [player]")
				.setExample("/gamemode SURVIVAL");
		
		writeJson(gson, help);
		
		help = new Help("gamerule", "gamerule", "Sets or queries a game rule value.", false)
				.setPermission("minecraft.command.gamerule")
				.setUsage("gamerule <rule name> [value]")
				.setExample("/gamerule doDaylightCycle false");
		
		writeJson(gson, help);
		
		help = new Help("give", "give", "Gives an item to a player.", false)
				.setPermission("minecraft.command.give")
				.setUsage("/give <player> <item> [amount] [data] [dataTag]")
				.setExample("/give MonroeTT minecraft:diamond 64");
		
		writeJson(gson, help);
		
		help = new Help("kill", "kill", "Kills entities (players, mobs, items, etc.).", false)
				.setPermission("minecraft.command.kill")
				.setUsage("kill [player|entity]")
				.setExample("kill MonroeTT");
		
		writeJson(gson, help);
		
		help = new Help("kick", "kick", "Kicks a player off a server.", false)
				.setPermission("minecraft.command.kick")
				.setUsage("/kick <player> [reason ...]")
				.setExample("kick MonroeTT stop being a jerk");
		
		writeJson(gson, help);

		try (Stream<Path> paths = Files.walk(Main.instance().getPath())) {
			paths.forEach(path -> {
				File file = path.toFile();
				
				if(file.getName().endsWith(".json")) {
					try {
						gson.fromJson(new FileReader(file), Help.class).save();
					} catch (JsonSyntaxException | JsonIOException | FileNotFoundException e) {
						e.printStackTrace();
					}
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		
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
						gson.fromJson(new FileReader(file), Help.class).save();
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