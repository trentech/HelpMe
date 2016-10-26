package com.gmail.trentech.helpme.help;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextStyle;
import org.spongepowered.api.text.format.TextStyles;

import com.gmail.trentech.helpme.utils.ConfigManager;
import com.google.common.collect.Lists;

import ninja.leaping.configurate.ConfigurationNode;

public class Help implements Comparable<Help> {

	private final String rawCommand;
	private final String command;
	private final String description;
	private Optional<String> permission = Optional.empty();
	private Optional<Usage> usage = Optional.empty();
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

	public Optional<Usage> getUsage() {
		return usage;
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

	public Help setUsage(Usage usage) {
		this.usage = Optional.of(usage);
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
		ConfigurationNode config = ConfigManager.get().getConfig();

		TextColor headersColor = Sponge.getRegistry().getType(TextColor.class, config.getNode("theme", "headers", "color").getString()).get();	
		TextColor contentColor = Sponge.getRegistry().getType(TextColor.class, config.getNode("theme", "content", "color").getString()).get();
		
		TextStyle headersStyle = Help.getStyle(config.getNode("theme", "headers", "style"));
		TextStyle contentStyle = Help.getStyle(config.getNode("theme", "content", "style"));
		
		List<Text> list = new ArrayList<>();

		list.add(Text.of(headersStyle, headersColor, "Description:"));

		StringBuilder sb = new StringBuilder(" " + getDescription());

		if (sb.indexOf(" ", 50) == -1) {
			list.add(Text.of(contentStyle, contentColor, sb.toString()));
		} else {
			int i = 0;
			while ((i = sb.indexOf(" ", i + 50)) != -1) {
				list.add(Text.of(contentStyle, contentColor, sb.substring(0, i)));

				sb.delete(0, i);

				i = 0;
			}

			list.add(Text.of(contentStyle, contentColor, sb.toString()));
		}

		Optional<String> permission = getPermission();

		if (permission.isPresent()) {
			if (!src.hasPermission(permission.get())) {
				return;
			}

			list.add(Text.of(headersStyle, headersColor, "Permission:"));
			list.add(Text.of(contentStyle, contentColor, " ", permission.get()));
		}

		Optional<Usage> optionalUsage = getUsage();

		if (optionalUsage.isPresent()) {
			Usage usage = optionalUsage.get();

			list.add(Text.of(headersStyle, headersColor, "Usage:"));

			Text command = Text.of(" /", getRawCommand());

			for (Argument argument : usage.getArguments()) {
				Optional<String> description = argument.getDescription();

				if (description.isPresent()) {
					sb = new StringBuilder(description.get());

					int i = 0;
					while ((i = sb.indexOf(" ", i + 50)) != -1) {
						sb.replace(i, i + 1, "\n");
					}

					if (command.toPlain().length() > 45) {
						list.add(Text.of(contentStyle, contentColor, command));
						command = Text.join(Text.of(" "), Text.builder().onHover(TextActions.showText(Text.of(sb.toString()))).append(Text.of(argument.getKey())).build());
					} else {
						command = Text.join(command, Text.of(" "), Text.builder().onHover(TextActions.showText(Text.of(sb.toString()))).append(Text.of(argument.getKey())).build());
					}
				} else {
					if (command.toPlain().length() > 45) {
						list.add(Text.of(contentStyle, contentColor, command));
						command = Text.join(Text.of(" "), Text.of(argument.getKey()));
					} else {
						command = Text.join(command, Text.of(" "), Text.of(argument.getKey()));
					}
				}
			}

			list.add(Text.of(contentStyle, contentColor, command));
		}

		TextColor paddingColor = Sponge.getRegistry().getType(TextColor.class, config.getNode("theme", "pagination", "padding", "color").getString()).get();
		TextColor titleColor = Sponge.getRegistry().getType(TextColor.class, config.getNode("theme", "pagination", "title", "color").getString()).get();

		TextStyle titleStyle = Help.getStyle(config.getNode("theme", "pagination", "title", "style"));
		
		List<String> examples = getExamples();

		if (!examples.isEmpty()) {
			list.add(Text.of(headersStyle, headersColor, "Example:"));

			for (String example : examples) {
				list.add(Text.of(contentStyle, contentColor, " ", example));
			}
		}

		PaginationList.builder().title(Text.builder().color(paddingColor).append(Text.of(titleStyle, titleColor, getCommand().toLowerCase())).build()).contents(list).sendTo(src);
	}

	private static TextStyle getStyle(ConfigurationNode node) {
		TextStyle style = TextStyles.NONE;
		
		String[] styles = node.getString().split(",");
		
		for(int i = 0; i < styles.length; i++) {
			if(styles[i].equalsIgnoreCase("BOLD")) {
				style = style.and(TextStyles.BOLD);
			} else if(styles[i].equalsIgnoreCase("ITALIC")) {
				style = style.and(TextStyles.ITALIC);
			} else if(styles[i].equalsIgnoreCase("OBFUSCATED")) {
				style = style.and(TextStyles.OBFUSCATED);
			} else if(styles[i].equalsIgnoreCase("UNDERLINE")) {
				style = style.and(TextStyles.UNDERLINE);
			} else if(styles[i].equalsIgnoreCase("STRIKETHROUGH")) {
				style = style.and(TextStyles.STRIKETHROUGH);
			}
		}
		
		return style;
	}
	public Consumer<CommandSource> execute() {
		return (CommandSource src) -> {
			execute(src);
		};
	}

	public static void executeList(CommandSource src, List<Help> list) {
		Help[] array = list.toArray(new Help[list.size()]);

		Arrays.parallelSort(array);

		ConfigurationNode config = ConfigManager.get().getConfig();

		TextColor listColor = Sponge.getRegistry().getType(TextColor.class, config.getNode("theme", "list", "color").getString()).get();

		List<Text> pages = new ArrayList<>();

		for (Help help : array) {
			Optional<String> optionalPermission = help.getPermission();

			if (optionalPermission.isPresent()) {
				if (src.hasPermission(optionalPermission.get())) {
					if (!help.getChildren().isEmpty()) {
						pages.add(Text.builder().color(listColor).onHover(TextActions.showText(Text.of("Click command for list of sub commands "))).onClick(TextActions.executeCallback(Help.executeList(help.getChildren()))).append(Text.of("/" + help.getRawCommand())).build());
					} else {
						pages.add(Text.builder().color(listColor).onHover(TextActions.showText(Text.of("Click command for more information "))).onClick(TextActions.executeCallback(help.execute())).append(Text.of("/" + help.getRawCommand())).build());
					}
				}
			} else {
				if (!help.getChildren().isEmpty()) {
					pages.add(Text.builder().color(listColor).onHover(TextActions.showText(Text.of("Click command for list of sub commands "))).onClick(TextActions.executeCallback(Help.executeList(help.getChildren()))).append(Text.of("/" + help.getRawCommand())).build());
				} else {
					pages.add(Text.builder().color(listColor).onHover(TextActions.showText(Text.of("Click command for more information "))).onClick(TextActions.executeCallback(help.execute())).append(Text.of("/" + help.getRawCommand())).build());
				}
			}
		}

		if (!pages.isEmpty()) {
			TextColor paddingColor = Sponge.getRegistry().getType(TextColor.class, config.getNode("theme", "pagination", "padding", "color").getString()).get();
			TextColor titleColor = Sponge.getRegistry().getType(TextColor.class, config.getNode("theme", "pagination", "title", "color").getString()).get();

			TextStyle titleStyle = Help.getStyle(config.getNode("theme", "pagination", "title", "style"));
			
			if (src instanceof Player) {
				PaginationList.builder().title(Text.builder().color(paddingColor).append(Text.of(titleStyle, titleColor, "Command List")).build()).contents(pages).sendTo(src);
			} else {
				for (Text text : pages) {
					src.sendMessage(text);
				}
			}
		}
	}

	public static Consumer<CommandSource> executeList(List<Help> list) {
		return (CommandSource src) -> {
			executeList(src, list);
		};
	}

	public static Optional<Help> get(String rawCommand) {
		for (Help help : all()) {
			if (help.getRawCommand().equals(rawCommand)) {
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

		for (Help help : children) {
			List<Help> ch = help.getChildren();

			if (!ch.isEmpty()) {
				list.addAll(getAll(ch));
			}
		}
		return list;
	}

	public static void register(Help help) {
		for(Help h : Lists.newArrayList(list)) {
			if(h.getRawCommand().equals(help.getRawCommand())) {
				list.remove(h);
				break;
			}
		}
		
		list.add(help);
	}

	@Override
	public int compareTo(Help help) {
		return getRawCommand().compareTo(help.getRawCommand());
	}
}
