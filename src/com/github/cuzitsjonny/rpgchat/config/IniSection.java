package com.github.cuzitsjonny.rpgchat.config;

import java.util.HashMap;
import java.util.Map;

public class IniSection {

	private Map<String, String> values;

	public IniSection() {
		values = new HashMap<String, String>();
	}

	public String getAsString(String key, String defaultReturnValue) {
		if (values.containsKey(key)) {
			return values.get(key);
		}

		return defaultReturnValue;
	}

	public int getAsInt(String key, int defaultReturnValue) {
		if (values.containsKey(key)) {
			try {
				return Integer.parseInt(values.get(key));
			} catch (NumberFormatException e) {
			}
		}

		return defaultReturnValue;
	}

	public boolean getAsBoolean(String key, boolean defaultReturnValue) {
		if (values.containsKey(key)) {
			String value = values.get(key).toLowerCase();

			if (value.equals("true") || value.equals("yes") || value.equals("1")) {
				return true;
			} else if (value.equals("false") || value.equals("no") || value.equals("0")) {
				return false;
			}
		}

		return defaultReturnValue;
	}

	public void set(String key, String value) {
		values.put(key, value);
	}

	public void set(String key, int value) {
		values.put(key, String.valueOf(value));
	}

	public void set(String key, boolean value) {
		values.put(key, String.valueOf(value));
	}

}
