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
			if (config.getNode("colors", "pagination", "title").isVirtual()) {
				config.getNode("colors", "pagination", "title").setValue(TextColors.GREEN.getName());
			}
			if (config.getNode("colors", "pagination", "padding").isVirtual()) {
				config.getNode("colors", "pagination", "padding").setValue(TextColors.DARK_GREEN.getName());
			}
			if (config.getNode("colors", "list").isVirtual()) {
				config.getNode("colors", "list").setValue(TextColors.GREEN.getName());
			}
			if (config.getNode("colors", "content").isVirtual()) {
				config.getNode("colors", "content").setValue(TextColors.WHITE.getName());
			}
			if (config.getNode("colors", "headers").isVirtual()) {
				config.getNode("colors", "headers").setValue(TextColors.GREEN.getName());
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
