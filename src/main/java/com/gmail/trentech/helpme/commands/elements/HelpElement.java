package com.gmail.trentech.helpme.commands.elements;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.helpme.Help;

public class HelpElement extends CommandElement {

	CommandContext context = new CommandContext();
	
    public HelpElement(Text key) {
        super(key);
    }

    @Override
    public void parse(CommandSource source, CommandArgs args, CommandContext context) throws ArgumentParseException {
    	this.context = context;
    	
        Object val = parseValue(source, args);
        String key = getUntranslatedKey();
        if (key != null && val != null) {
            if (val instanceof Iterable<?>) {
                for (Object ent : ((Iterable<?>) val)) {
                    context.putArg(key, ent);
                }
            } else {
                context.putArg(key, val);
            }
        }
    }
    
    @Override
    protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
    	final String next = args.next();

    	if(context.hasAny(getKey())) {
    		Collection<Help> collection = context.<Help>getAll(getKey());
    		
    		Help parent = collection.toArray(new Help[collection.size()])[collection.size() - 1];
    		
    		for(Help help : parent.getChildren()) {
    			if(help.getCommand().equals(next)) {
    				return help;
    			}
    		}
    	} else {
    		for(Help help : Help.getParents()) {
    			if(help.getCommand().equals(next)) {
    				return help;
    			}
    		}
    	}

		throw args.createError(Text.of(TextColors.RED, "Command not found"));
    }

    @Override
    public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {  	
    	List<String> list = new ArrayList<>();

    	Optional<String> next = args.nextIfPresent();
    	
    	if(next.isPresent() && !next.get().equals("")) {
        	if(context.hasAny(getKey())) {
        		Collection<Help> collection = context.<Help>getAll(getKey());
        		
        		Help parent = collection.toArray(new Help[collection.size()])[collection.size() - 1];
        		
        		for(Help help : parent.getChildren()) {
        			if(help.getCommand().startsWith(next.get())) {
        				list.add(help.getCommand());
        			}
        		}
        	} else {
        		for(Help help : Help.getParents()) {
        			if(help.getCommand().startsWith(next.get())) {
        				list.add(help.getCommand());
        			}
        		}
        	}
    	} else {
        	if(context.hasAny(getKey())) {
        		Collection<Help> collection = context.<Help>getAll(getKey());
        		
        		Help parent = collection.toArray(new Help[collection.size()])[collection.size() - 1];
        		
        		for(Help help : parent.getChildren()) {
        			list.add(help.getCommand());
        		}
        	} else {
        		for(Help help : Help.getParents()) {
        			list.add(help.getCommand());
        		}
        	}
    	}

        return list;
    }

    @Override
    public Text getUsage(CommandSource src) {
        return Text.of(getKey());
    }
}
