package com.gmail.trentech.helpme.help;

import java.util.Optional;

public class Argument {

	private final String key;
	private Optional<String> description = Optional.empty();
	
	private Argument(String key) {
		this.key = key;
	}
	
	private Argument(String key, String description) {
		this.key = key;
		this.description = Optional.of(description);
	}

	public Optional<String> getDescription() {
		return description;
	}

	public String getKey() {
		return key;
	}
	
	public static Argument of(String key) {
		return new Argument(key);
	}
	
	public static Argument of(String key, String description) {
		return new Argument(key, description);
	}
}
