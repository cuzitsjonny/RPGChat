package com.github.cuzitsjonny.rpgchat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.cuzitsjonny.rpgchat.config.IniConfig;
import com.github.cuzitsjonny.rpgchat.config.IniSection;
import com.github.cuzitsjonny.rpgchat.listeners.AsyncPlayerChatListener;

public class RPGChatPlugin extends JavaPlugin {

	private IniConfig pluginConfig;

	@Override
	public void onEnable() {
		File configFile = new File(getDataFolder(), "config.ini");

		if (!configFile.exists()) {
			try {
				configFile.getParentFile().mkdirs();

				FileOutputStream out = new FileOutputStream(configFile);
				InputStream in = getClassLoader().getResourceAsStream("defaultConfig.ini");
				byte[] buffer = new byte[32];
				int read = 0;

				while ((read = in.read(buffer, 0, buffer.length)) > -1) {
					out.write(buffer, 0, read);
				}

				in.close();
				out.close();
			} catch (IOException e) {
				getLogger().severe("An error occured trying to create the default config: '" + e.getMessage() + "'");
				Bukkit.getPluginManager().disablePlugin(this);
			}
		}

		try {
			pluginConfig = new IniConfig(configFile);
			pluginConfig.load();
		} catch (IOException e) {
			getLogger().severe("An error occured trying to load the config: '" + e.getMessage() + "'");
			Bukkit.getPluginManager().disablePlugin(this);
		}

		if (isEnabled()) {
			Bukkit.getPluginManager().registerEvents(new AsyncPlayerChatListener(this), this);
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player p = null;

		if (sender instanceof Player) {
			p = (Player) sender;

			if (command.getName().equals("reloadrpgchat")) {
				try {
					pluginConfig.load();

					p.sendMessage("§aSuccessfully reloaded the RPGChat config.");
				} catch (IOException e) {
					p.sendMessage("§cAn error occured trying to reload the RPGChat config: '" + e.getMessage() + "'");
				}
			}

			if (command.getName().equals("ooc")) {
				StringBuilder messageBuilder = new StringBuilder();
				String message;

				for (int i = 0; i < args.length; i++) {
					if (i > 0) {
						messageBuilder.append(' ');
					}

					messageBuilder.append(args[i]);
				}

				message = messageBuilder.toString();

				if (message.length() > 0) {
					broadcastGlobalMessage(p, message);
				}
			}
		} else {
			getLogger().warning("The console is not permitted to run any commands of this plugin.");
		}

		return true;
	}

	public IniConfig getPluginConfig() {
		return pluginConfig;
	}

	public void broadcastLocalMessage(Player sender, String message) {
		Location senderLoc = sender.getLocation();
		IniSection localSection = pluginConfig.getSection("Local");
		String format = localSection.getAsString("message_format", "&4A problem occured. Check your RPGChat config.");
		String prefix = localSection.getAsString("prefix", "&4A problem occured. Check your RPGChat config.");
		boolean autoPeriod = localSection.getAsBoolean("auto_period", true);
		int range = localSection.getAsInt("range_in_blocks", 10);

		String actualMessage = ChatColor.translateAlternateColorCodes('&', format);

		actualMessage = actualMessage.replace("%prefix%", ChatColor.translateAlternateColorCodes('&', prefix));
		actualMessage = actualMessage.replace("%player%", sender.getName());
		actualMessage = actualMessage.replace("%equals%", "=");

		if (autoPeriod) {
			if (!(message.endsWith(".") || message.endsWith("!") || message.endsWith("?"))) {
				message = message + ".";
			}
		}

		if (sender.hasPermission("rpgchat.color")) {
			actualMessage = actualMessage.replace("%message%", ChatColor.translateAlternateColorCodes('&', message));
		} else {
			actualMessage = actualMessage.replace("%message%", message);
		}

		double minX = senderLoc.getX() - range;
		double minY = senderLoc.getY() - range;
		double minZ = senderLoc.getZ() - range;
		double maxX = senderLoc.getX() + range;
		double maxY = senderLoc.getY() + range;
		double maxZ = senderLoc.getZ() + range;

		for (Player player : Bukkit.getOnlinePlayers()) {
			Location playerLoc = player.getLocation();

			if (playerLoc.getX() >= minX && playerLoc.getX() <= maxX) {
				if (playerLoc.getY() >= minY && playerLoc.getY() <= maxY) {
					if (playerLoc.getZ() >= minZ && playerLoc.getZ() <= maxZ) {
						player.sendMessage(actualMessage);
					}
				}
			}
		}
	}

	public void broadcastGlobalMessage(Player sender, String message) {
		IniSection globalSection = pluginConfig.getSection("Global");
		String format = globalSection.getAsString("message_format", "&4A problem occured. Check your RPGChat config.");
		String prefix = globalSection.getAsString("prefix", "&4A problem occured. Check your RPGChat config.");
		boolean autoPeriod = globalSection.getAsBoolean("auto_period", true);

		String actualMessage = ChatColor.translateAlternateColorCodes('&', format);

		actualMessage = actualMessage.replace("%prefix%", ChatColor.translateAlternateColorCodes('&', prefix));
		actualMessage = actualMessage.replace("%player%", sender.getName());
		actualMessage = actualMessage.replace("%equals%", "=");

		if (autoPeriod) {
			if (!(message.endsWith(".") || message.endsWith("!") || message.endsWith("?"))) {
				message = message + ".";
			}
		}

		if (sender.hasPermission("rpgchat.color")) {
			actualMessage = actualMessage.replace("%message%", ChatColor.translateAlternateColorCodes('&', message));
		} else {
			actualMessage = actualMessage.replace("%message%", message);
		}

		for (Player player : Bukkit.getOnlinePlayers()) {
			player.sendMessage(actualMessage);
		}
	}

}
