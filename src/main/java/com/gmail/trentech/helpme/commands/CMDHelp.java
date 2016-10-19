package com.gmail.trentech.helpme.commands;

import java.util.Collection;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

import com.gmail.trentech.helpme.Help;
import com.gmail.trentech.helpme.commands.elements.HelpElement;

public class CMDHelp implements CommandExecutor {

	public static CommandSpec cmdHelp = CommandSpec.builder().description(Text.of(" Get help with commands registered with this plugin")).permission("helpme.cmd")
			.arguments(GenericArguments.optional(GenericArguments.allOf(new HelpElement(Text.of("rawCommand"))))).executor(new CMDHelp()).build();

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {		
		if(args.hasAny("rawCommand")) {
    		Collection<Help> collection = args.<Help>getAll("rawCommand");
    		
    		Help help = collection.toArray(new Help[collection.size()])[collection.size() - 1];
    		
			if(!help.getChildren().isEmpty()) {
				Help.executeList(src, help.getChildren());	
			} else {
				help.execute(src);
			}
		} else {
			Help.executeList(src, Help.getParents());
		}

		return CommandResult.success();
	}
}
