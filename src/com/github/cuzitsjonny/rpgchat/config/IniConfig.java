package com.github.cuzitsjonny.rpgchat.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class IniConfig {

	private File file;
	private Map<String, IniSection> values;

	public IniConfig(File file) {
		this.file = file;
		this.values = new HashMap<String, IniSection>();
	}

	public File getFile() {
		return file;
	}

	public void load() throws IOException {
		values.clear();

		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
		String section = null;
		String line = null;

		while ((line = reader.readLine()) != null) {
			if (!line.startsWith(";")) {
				if (!(line.startsWith("[") && line.endsWith("]"))) {
					if (line.length() - line.replace("=", "").length() == 1) {
						if (section != null) {
							IniSection iniSection = null;
							String[] split = line.split("=");

							if (values.containsKey(section)) {
								iniSection = values.get(section);
							} else {
								iniSection = new IniSection();
								values.put(section, iniSection);
							}

							if (split.length == 1) {
								iniSection.set(split[0], "");
							} else if (split.length == 2) {
								iniSection.set(split[0], split[1]);
							}
						}
					}
				} else {
					section = line.substring(1, line.length() - 1);
				}
			}
		}

		reader.close();
	}

	public IniSection getSection(String name) {
		IniSection iniSection = null;

		if (values.containsKey(name)) {
			iniSection = values.get(name);
		} else {
			iniSection = new IniSection();
			values.put(name, iniSection);
		}

		return iniSection;
	}

}