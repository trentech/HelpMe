package com.gmail.trentech.helpme;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Consumer;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import com.google.common.base.CharMatcher;

public class Help {

	private final String rawCommand;
	private final String command;
	private final String description;
	private final boolean hasChildren;
	private Optional<String> permission = Optional.empty();
	private Optional<String> usage = Optional.empty();
	private Optional<String> example = Optional.empty();

	private static TreeMap<String, Help> map = new TreeMap<>();

	public Help(String rawCommand, String command, String description, boolean hasChildren) {
		this.rawCommand = rawCommand;
		this.command = command;
		this.description = description;
		this.hasChildren = hasChildren;
	}

	public String getRawCommand() {
		return rawCommand;
	}

	public String getDescription() {
		return description;
	}

	public boolean hasChildren() {
		return hasChildren;
	}
	
	public Optional<String> getPermission() {
		return permission;
	}

	public Help setPermission(String permission) {
		this.permission = Optional.of(permission);
		return this;
	}

	public Optional<String> getUsage() {
		return usage;
	}

	public Help setUsage(String usage) {
		this.usage = Optional.of(usage);
		return this;
	}

	public Optional<String> getExample() {
		return example;
	}

	public Help setExample(String example) {
		this.example = Optional.of(example);
		return this;
	}

	public String getCommand() {
		return command;
	}

	public void save() {
		map.put(getRawCommand(), this);
	}

	public void execute(CommandSource src) {
		List<Text> list = new ArrayList<>();

		list.add(Text.of(TextColors.GREEN, "Description:"));
		list.add(Text.of(TextColors.WHITE, " ", getDescription()));

		Optional<String> permission = getPermission();
		
		if (permission.isPresent()) {
			list.add(Text.of(TextColors.GREEN, "Permission:"));
			list.add(Text.of(TextColors.WHITE, " ", permission.get()));
		}
		
		Optional<String> usage = getUsage();
		
		if (usage.isPresent()) {
			list.add(Text.of(TextColors.GREEN, "Syntax:"));
			list.add(Text.of(TextColors.WHITE, " ", usage.get()));
		}
		
		Optional<String> example = getExample();
		
		if (example.isPresent()) {
			list.add(Text.of(TextColors.GREEN, "Example:"));
			list.add(Text.of(TextColors.WHITE, " ", example.get(), TextColors.DARK_GREEN));
		}

		PaginationList.builder()
				.title(Text.builder().color(TextColors.DARK_GREEN).append(Text.of(TextColors.GREEN, getCommand().toLowerCase())).build())
				.contents(list)
				.sendTo(src);
	}

	public static Optional<Help> get(String rawCommand) {
		if (map.containsKey(rawCommand)) {
			return Optional.of(map.get(rawCommand));
		}

		return Optional.empty();
	}

	public static Consumer<CommandSource> execute(String rawCommand) {
		return (CommandSource src) -> {
			if (map.containsKey(rawCommand)) {
				Help help = map.get(rawCommand);
				help.execute(src);
			}
		};
	}

	public static Consumer<CommandSource> executeList(List<Help> list) {
		return (CommandSource src) -> {
			executeList(src, list);
		};
	}
	
	public static void executeList(CommandSource src, List<Help> list) {
		List<Text> pages = new ArrayList<>();
		
		for (Help help : list) {

			Optional<String> optionalPermission = help.getPermission();
			
			if(optionalPermission.isPresent()) {
				if (src.hasPermission(optionalPermission.get())) {
					if(help.hasChildren()) {
						pages.add(Text.builder().color(TextColors.GREEN).onHover(TextActions.showText(Text.of("Click command for list of sub commands "))).onClick(TextActions.executeCallback(Help.executeList(Help.getChildren(help.getRawCommand())))).append(Text.of("/" + help.getRawCommand())).build());
					} else {
						pages.add(Text.builder().color(TextColors.GREEN).onHover(TextActions.showText(Text.of("Click command for more information "))).onClick(TextActions.executeCallback(Help.execute(help.getRawCommand()))).append(Text.of("/" + help.getRawCommand())).build());
					}	
				}
			} else {
				if(help.hasChildren()) {
					pages.add(Text.builder().color(TextColors.GREEN).onHover(TextActions.showText(Text.of("Click command for list of sub commands "))).onClick(TextActions.executeCallback(Help.executeList(Help.getChildren(help.getRawCommand())))).append(Text.of("/" + help.getRawCommand())).build());
				} else {
					pages.add(Text.builder().color(TextColors.GREEN).onHover(TextActions.showText(Text.of("Click command for more information "))).onClick(TextActions.executeCallback(Help.execute(help.getRawCommand()))).append(Text.of("/" + help.getRawCommand())).build());
				}
			}
		}
		
		if (src instanceof Player) {
			PaginationList.builder()
					.title(Text.builder().color(TextColors.DARK_GREEN).append(Text.of(TextColors.GREEN, "Command List")).build())
					.contents(pages)
					.sendTo(src);
		} else {
			for (Text text : pages) {
				src.sendMessage(text);
			}
		}
	}
	
	public static List<Help> getParents() {
		List<Help> list = new ArrayList<>();
		
		for(Entry<String, Help> entry : map.entrySet()) {
			Help help = entry.getValue();
			
			if(CharMatcher.WHITESPACE.matchesNoneOf(help.getRawCommand())) {
				list.add(help);
			}
		}
		
		return list;
	}
	
	public static List<Help> getChildren(String parentCommand) {
		List<Help> list = new ArrayList<>();
		
		for(Entry<String, Help> entry : map.entrySet()) {
			Help help = entry.getValue();

			if(help.getRawCommand().startsWith(parentCommand)) {
				String child = help.getRawCommand().replace(parentCommand, "");
				
				if(child.equals("")) {
					continue;
				}
				
				child = child.substring(1, child.length() - 1);
				
				if(CharMatcher.WHITESPACE.matchesNoneOf(child)) {
					list.add(help);
				}
			}
		}

		return list;
	}
	
	public static List<Help> getAll() {
		List<Help> list = new ArrayList<>();
		
		for(Entry<String, Help> entry : map.entrySet()) {
			list.add(entry.getValue());
		}

		return list;
	}
}
