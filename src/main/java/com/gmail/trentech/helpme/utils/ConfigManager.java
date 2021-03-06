package com.gmail.trentech.helpme.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;

import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.helpme.Main;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

public class ConfigManager {

	private Path path;
	private CommentedConfigurationNode config;
	private ConfigurationLoader<CommentedConfigurationNode> loader;
	
	private static ConcurrentHashMap<String, ConfigManager> configManagers = new ConcurrentHashMap<>();

	private ConfigManager(String configName) {
		try {
			path = Main.instance().getPath().resolve(configName + ".conf");
			
			if (!Files.exists(path)) {		
				Files.createFile(path);
				Main.instance().getLog().info("Creating new " + path.getFileName() + " file...");
			}		
		} catch (IOException e) {
			e.printStackTrace();
		}

		load();
	}
	
	public static ConfigManager get(String configName) {
		return configManagers.get(configName);
	}
	
	public static ConfigManager get() {
		return configManagers.get("config");
	}

	public static ConfigManager init() {
		return init("config");
	}

	public static ConfigManager init(String configName) {
		ConfigManager configManager = new ConfigManager(configName);
		CommentedConfigurationNode config = configManager.getConfig();
		
		if (configName.equalsIgnoreCase("config")) {
			if (config.getNode("theme", "pagination", "title").isVirtual()) {
				config.getNode("theme", "pagination", "title", "color").setValue(TextColors.GREEN.getName().toUpperCase()).setComment("One of the following: AQUA,BLACK,BLUE,DARK_AQUA,DARK_BLUE,DARK_GRAY,DARK_GREEN,DARK_PURPLE,DARK_RED,GOLD,GRAY,GREEN,LIGHT_PURPLE,RED,WHITE,YELLOW");
				config.getNode("theme", "pagination", "title", "style").setValue("UNDERLINE").setComment("One or more(Comma seperated) of the following: BOLD,ITALIC,UNDERLINE,STRIKETHROUGH,OBFUSCATED");
			}
			if (config.getNode("theme", "pagination", "padding").isVirtual()) {
				config.getNode("theme", "pagination", "padding", "color").setValue(TextColors.DARK_GREEN.getName()).setComment("One of the following: AQUA,BLACK,BLUE,DARK_AQUA,DARK_BLUE,DARK_GRAY,DARK_GREEN,DARK_PURPLE,DARK_RED,GOLD,GRAY,GREEN,LIGHT_PURPLE,RED,WHITE,YELLOW");;
				config.getNode("theme", "pagination", "padding", "style").setValue("STRIKETHROUGH,BOLD").setComment("One or more(Comma seperated) of the following: BOLD,ITALIC,UNDERLINE,STRIKETHROUGH,OBFUSCATED");
				config.getNode("theme", "pagination", "padding", "text").setValue("=").setComment("Characters the padding will be made of");
			}
			if (config.getNode("theme", "list").isVirtual()) {
				config.getNode("theme", "list", "color").setValue(TextColors.GREEN.getName().toUpperCase()).setComment("One of the following: AQUA,BLACK,BLUE,DARK_AQUA,DARK_BLUE,DARK_GRAY,DARK_GREEN,DARK_PURPLE,DARK_RED,GOLD,GRAY,GREEN,LIGHT_PURPLE,RED,WHITE,YELLOW");;
				config.getNode("theme", "list", "style").setValue("ITALIC").setComment("One or more(Comma seperated) of the following: 'BOLD,ITALIC,UNDERLINE,STRIKETHROUGH,OBFUSCATED'");;
			}
			if (config.getNode("theme", "content").isVirtual()) {
				config.getNode("theme", "content", "color").setValue(TextColors.WHITE.getName().toUpperCase()).setComment("One of the following: AQUA,BLACK,BLUE,DARK_AQUA,DARK_BLUE,DARK_GRAY,DARK_GREEN,DARK_PURPLE,DARK_RED,GOLD,GRAY,GREEN,LIGHT_PURPLE,RED,WHITE,YELLOW");;
				config.getNode("theme", "content", "style").setValue("NONE").setComment("One or more(Comma seperated) of the following: 'BOLD,ITALIC,UNDERLINE,STRIKETHROUGH,OBFUSCATED'");;
			}
			if (config.getNode("theme", "headers").isVirtual()) {
				config.getNode("theme", "headers", "color").setValue(TextColors.GREEN.getName().toUpperCase()).setComment("One of the following: AQUA,BLACK,BLUE,DARK_AQUA,DARK_BLUE,DARK_GRAY,DARK_GREEN,DARK_PURPLE,DARK_RED,GOLD,GRAY,GREEN,LIGHT_PURPLE,RED,WHITE,YELLOW");;
				config.getNode("theme", "headers", "style").setValue("BOLD").setComment("One or more(Comma seperated) of the following: 'BOLD,ITALIC,UNDERLINE,STRIKETHROUGH,OBFUSCATED'");;
			}
		}
		
		configManager.save();
		
		configManagers.put(configName, configManager);
		
		return configManager;
	}
	
	public ConfigurationLoader<CommentedConfigurationNode> getLoader() {
		return loader;
	}

	public CommentedConfigurationNode getConfig() {
		return config;
	}

	public void save() {
		try {
			loader.save(config);
		} catch (IOException e) {
			Main.instance().getLog().error("Failed to save config");
			e.printStackTrace();
		}
	}

	private void load() {
		loader = HoconConfigurationLoader.builder().setPath(path).build();
		try {
			config = loader.load();
		} catch (IOException e) {
			Main.instance().getLog().error("Failed to load config");
			e.printStackTrace();
		}
	}
}
