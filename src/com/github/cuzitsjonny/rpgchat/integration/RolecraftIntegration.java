package com.github.cuzitsjonny.rpgchat.integration;

import org.bukkit.entity.Player;
import org.caliog.Rolecraft.Entities.Player.PlayerManager;

public class RolecraftIntegration {

	public static String getClass(Player player) {
		return PlayerManager.getPlayer(player.getUniqueId()).getType();
	}

}
