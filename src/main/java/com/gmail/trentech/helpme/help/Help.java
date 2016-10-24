package com.gmail.trentech.helpme.help;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

public class Help implements Comparable<Help> {

	private final String rawCommand;
	private final String command;
	private final String description;
	private Optional<String> permission = Optional.empty();
	private List<Usage> usages = new ArrayList<>();
	private List<String> examples = new ArrayList<>();
	private List<Help> children = new ArrayList<>();
	
	private static List<Help> list = new ArrayList<>();

	public Help(String rawCommand, String command, String description) {
		this.rawCommand = rawCommand;
		this.command = command;
		this.description = description;
	}

	public String getRawCommand() {
		return rawCommand;
	}

	public String getDescription() {
		return description;
	}

	public Optional<String> getPermission() {
		return permission;
	}
	
	public List<Usage> getUsages() {
		return usages;
	}
	
	public List<String> getExamples() {
		return examples;
	}
	
	public String getCommand() {
		return command;
	}
	
	public List<Help> getChildren() {
		return children;
	}

	public Help setPermission(String permission) {
		this.permission = Optional.of(permission);
		return this;
	}

	public Help addUsage(Usage usage) {
		this.usages.add(usage);
		return this;
	}

	public Help addExample(String example) {
		this.examples.add(example);
		return this;
	}

	public Help addChild(Help help) {
		children.add(help);
		return this;
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
		
		List<Usage> usages = getUsages();
		
		if (!usages.isEmpty()) {
			list.add(Text.of(TextColors.GREEN, "Usage:"));
			
			for(Usage usage : usages) {
				Text command = Text.of(" /", getRawCommand());

				for(Argument argument : usage.getArguments()) {
					Optional<String> description = argument.getDescription();

					if(description.isPresent()) {
						StringBuilder sb = new StringBuilder(description.get());

						int i = 0;
						while ((i = sb.indexOf(" ", i + 50)) != -1) {
						    sb.replace(i, i + 1, "\n");
						}

						command = Text.join(command, Text.of(" "), Text.builder().onHover(TextActions.showText(Text.of(sb.toString()))).append(Text.of(argument.getKey())).build());
					} else {
						command = Text.join(command, Text.of(" "), Text.of(argument.getKey()));
					}
				}
				
				list.add(Text.of(TextColors.WHITE, command));
			}
		}
		
		List<String> examples = getExamples();
		
		if (!examples.isEmpty()) {
			list.add(Text.of(TextColors.GREEN, "Example:"));
			
			for(String example : examples) {
				list.add(Text.of(TextColors.WHITE, " ", example, TextColors.DARK_GREEN));
			}		
		}

		PaginationList.builder()
				.title(Text.builder().color(TextColors.DARK_GREEN).append(Text.of(TextColors.GREEN, getCommand().toLowerCase())).build())
				.contents(list)
				.sendTo(src);
	}

	public Consumer<CommandSource> execute() {
		return (CommandSource src) -> {
			execute(src);
		};
	}

	public static void executeList(CommandSource src, List<Help> list) {
		List<Text> pages = new ArrayList<>();
		
		for (Help help : list) {
			Optional<String> optionalPermission = help.getPermission();
			
			if(optionalPermission.isPresent()) {
				if (src.hasPermission(optionalPermission.get())) {
					if(!help.getChildren().isEmpty()) {
						pages.add(Text.builder().color(TextColors.GREEN).onHover(TextActions.showText(Text.of("Click command for list of sub commands "))).onClick(TextActions.executeCallback(Help.executeList(help.getChildren()))).append(Text.of("/" + help.getRawCommand())).build());
					} else {
						pages.add(Text.builder().color(TextColors.GREEN).onHover(TextActions.showText(Text.of("Click command for more information "))).onClick(TextActions.executeCallback(help.execute())).append(Text.of("/" + help.getRawCommand())).build());
					}	
				}
			} else {
				if(!help.getChildren().isEmpty()) {
					pages.add(Text.builder().color(TextColors.GREEN).onHover(TextActions.showText(Text.of("Click command for list of sub commands "))).onClick(TextActions.executeCallback(Help.executeList(help.getChildren()))).append(Text.of("/" + help.getRawCommand())).build());
				} else {
					pages.add(Text.builder().color(TextColors.GREEN).onHover(TextActions.showText(Text.of("Click command for more information "))).onClick(TextActions.executeCallback(help.execute())).append(Text.of("/" + help.getRawCommand())).build());
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
	
	public static Consumer<CommandSource> executeList(List<Help> list) {
		return (CommandSource src) -> {
			executeList(src, list);
		};
	}
	
	public static Optional<Help> get(String rawCommand) {
		for(Help help : all()) {
			if(help.getRawCommand().equals(rawCommand)) {
				return Optional.of(help);
			}
		}

		return Optional.empty();
	}
	
	public static List<Help> getParents() {
		return list;
	}
	
	public static List<Help> all() {
		return getAll(getParents());
	}
	
	private static List<Help> getAll(List<Help> children) {
		List<Help> list = new ArrayList<>();
		
		list.addAll(children);
		
		for(Help help : children) {
			List<Help> ch = help.getChildren();
			
			if(!ch.isEmpty()) {
				list.addAll(getAll(ch));
			}
		}
		return list;
	}
	
	public static void register(Help help) {
		list.add(help);
	}

	@Override
	public int compareTo(Help help) {
		return getRawCommand().compareTo(help.getRawCommand());
	}
}
