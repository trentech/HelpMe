package com.gmail.trentech.helpme.help;

import java.util.ArrayList;
import java.util.List;

public class Usage {

	private List<Argument> arguments = new ArrayList<>();
	
	public Usage() {
		
	}
	
	public Usage addArgument(Argument argument) {
		arguments.add(argument);
		return this;
	}
	
	public List<Argument> getArguments() {
		return arguments;
	}
}
