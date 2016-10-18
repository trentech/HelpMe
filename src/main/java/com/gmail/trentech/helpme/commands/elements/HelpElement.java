package com.gmail.trentech.helpme.commands.elements;

import java.util.ArrayList;
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

    public HelpElement(Text key) {
        super(key);
    }

    @Override
    protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
        final StringBuilder ret = new StringBuilder(args.next());
        
        while (args.hasNext()) {
            ret.append(' ').append(args.next());
        }
        String rawCommand = ret.toString();

        Optional<Help> optionalHelp = Help.get(rawCommand);

        if(optionalHelp.isPresent()) {
        	return optionalHelp.get();
        }
        
		throw args.createError(Text.of(TextColors.RED, "Command not found"));
    }

    @Override
    public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {  	
    	List<String> list = new ArrayList<>();

    	String next = args.getRaw();

    	if(next.equals("")) {
    		for(Help help : Help.getParents()) {   			
    			list.add(help.getRawCommand());
    		}
    	} else {
    		if(next.contains(" ")) { 			
    			String[] split = next.split(" ");

    			Optional<Help> optionalParent = Help.get(split[0]);
    			
    			if(optionalParent.isPresent()) {			
    				Help parent = optionalParent.get();

    				String command = parent.getCommand();
    				
        			for(int i = 1; i < split.length; i++) {
        				command = command + " " + split[i];
        				
        				if(i == (split.length - 1)) {
                    		for(Help help : parent.getChildren()) {
                    			list.add(help.getCommand());
                    		}
                    		
                    		return list;
        				}
        				
                		for(Help help : parent.getChildren()) {
                			if(help.getRawCommand().startsWith(command)) {
                				if(help.getRawCommand().equals(command)) {
                					if(i == (split.length - 1)) {
	            						System.out.println("equals command, Last arg");
	            						
	                            		for(Help h : help.getChildren()) {
	                            			list.add(h.getCommand());
	                            		}
	                				}
                				} else if(i == (split.length - 1)) {
                					list.add(help.getCommand());
                				}
                			}
                			
                			parent = help;
                		}
        			}
    			}
    		} else {
    			System.out.println("No space");
        		for(Help help : Help.getParents()) {
        			if(help.getRawCommand().startsWith(next)) {
        				list.add(help.getCommand());
        			}
        		}
    		}

    	}

        return list;
    }

    @Override
    public Text getUsage(CommandSource src) {
        return Text.of(getKey());
    }
    
    public List<String> get(List<Help> list, String compare) {
    	List<String> l = new ArrayList<>();
    	
        for(Help help : list) {
        	if(help.getRawCommand().startsWith(compare)) {
        		if(help.getRawCommand().equals(compare)) {
        			if(help.getChildren().isEmpty()) {
        				l.add(help.getCommand());
        			} else {
        				l.addAll(get(help.getChildren(), compare));
        			}
        		}
        	}
        }
        
        return l;
    }
}
