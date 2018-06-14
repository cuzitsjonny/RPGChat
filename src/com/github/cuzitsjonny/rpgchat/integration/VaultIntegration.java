package com.github.cuzitsjonny.rpgchat.integration;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.chat.Chat;

public class VaultIntegration {

	private static Chat chat;

	public static Chat getChat() {
		if (chat == null) {
			RegisteredServiceProvider<Chat> rsp = Bukkit.getServicesManager().getRegistration(Chat.class);

			chat = rsp.getProvider();
		}

		return chat;
	}

}
