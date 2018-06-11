package com.github.cuzitsjonny.rpgchat.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.github.cuzitsjonny.rpgchat.RPGChatPlugin;

public class AsyncPlayerChatListener implements Listener {

	private RPGChatPlugin plugin;

	public AsyncPlayerChatListener(RPGChatPlugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onAsyncPlayerChat(AsyncPlayerChatEvent e) {
		int range = plugin.getPluginConfig().getSection("Local").getAsInt("normal_range_in_blocks", 10);

		e.setCancelled(true);
		plugin.broadcastLocalMessage(e.getPlayer(), e.getMessage(), range);
	}

}
