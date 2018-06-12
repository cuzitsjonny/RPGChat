package com.github.cuzitsjonny.rpgchat;

public class StringUtility {

	public static String arrayToString(String[] array, char delimiter) {
		StringBuilder builder = new StringBuilder();

		for (int i = 0; i < array.length; i++) {
			if (delimiter != 0x00) {
				if (i > 0) {
					builder.append(delimiter);
				}
			}

			builder.append(array[i]);
		}

		return builder.toString();
	}

}
