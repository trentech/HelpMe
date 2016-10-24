package com.gmail.trentech.helpme;

import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.command.TabCompleteEvent;

import com.gmail.trentech.helpme.help.Help;

public class EventListener {

	@Listener
	public void onTabCompleteEvent(TabCompleteEvent event) {
		String args = event.getRawMessage();

		if(args.equals("hm ") || args.equals("helpme ")) {
			for(Help help : Help.getParents()) {
				event.getTabCompletions().add(help.getCommand());
			}
		}
	}
}
