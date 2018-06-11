package com.github.cuzitsjonny.rpgchat.integration;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;

public class RoleCraftReflection {

	public static Class<?> getPlayerManager() {
		Class<?> playerManager = null;

		try {
			playerManager = Class.forName("org.caliog.Rolecraft.Entities.Player.PlayerManager");
		} catch (Exception e) {
		}

		return playerManager;
	}

	public static Object getRoleCraftPlayer(UUID playerUniqueId) {
		Object roleCraftPlayer = null;

		try {
			Field playersField = getPlayerManager().getDeclaredField("players");

			playersField.setAccessible(true);

			Object players = playersField.get(null);
			Class<?> playersClass = players.getClass();
			Method playersGetMethod = playersClass.getDeclaredMethod("get", Object.class);

			roleCraftPlayer = playersGetMethod.invoke(players, playerUniqueId);
		} catch (Exception e) {
		}

		return roleCraftPlayer;
	}

	public static String getRoleCraftPlayerType(Object roleCraftPlayer) {
		String type = null;

		try {
			Method getTypeMethod = roleCraftPlayer.getClass().getDeclaredMethod("getType");

			type = (String) getTypeMethod.invoke(roleCraftPlayer);
		} catch (Exception e) {
		}

		return type;
	}

}
