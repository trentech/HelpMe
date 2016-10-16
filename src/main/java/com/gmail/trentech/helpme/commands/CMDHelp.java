package com.gmail.trentech.helpme.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

import com.gmail.trentech.helpme.Help;
import com.gmail.trentech.helpme.commands.elements.HelpElement;

public class CMDHelp implements CommandExecutor {

	public static CommandSpec cmdHelp = CommandSpec.builder().description(Text.of(" Get help with commands registered with this plugin")).permission("helpme.cmd").arguments(new HelpElement(Text.of("rawCommand"))).executor(new CMDHelp()).build();

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		Help help = args.<Help>getOne("rawCommand").get();
		help.execute(src);

		return CommandResult.success();
	}
}
