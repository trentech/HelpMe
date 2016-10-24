package com.gmail.trentech.helpme.help;

import java.util.Optional;

public class Argument {

	private final String key;
	private Optional<String> description = Optional.empty();
	
	public Argument(String key) {
		this.key = key;
	}
	
	public Argument(String key, String description) {
		this.key = key;
		this.description = Optional.of(description);
	}

	public Optional<String> getDescription() {
		return description;
	}

	public void setDescription(Optional<String> description) {
		this.description = description;
	}

	public String getKey() {
		return key;
	}
	
}
