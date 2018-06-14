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
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.cuzitsjonny.rpgchat.config.IniConfig;
import com.github.cuzitsjonny.rpgchat.config.IniSection;
import com.github.cuzitsjonny.rpgchat.integration.RolecraftIntegration;
import com.github.cuzitsjonny.rpgchat.integration.VaultIntegration;
import com.github.cuzitsjonny.rpgchat.listeners.AsyncPlayerChatListener;

public class RPGChatPlugin extends JavaPlugin {

	private IniConfig pluginConfig;
	private boolean isRolecraftIntegrationEnabled;
	private boolean isVaultIntegrationEnabled;

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

		isRolecraftIntegrationEnabled = false;
		isVaultIntegrationEnabled = false;

		if (isEnabled()) {
			PluginManager pm = Bukkit.getPluginManager();

			IniSection rolecraftSection = pluginConfig.getSection("Rolecraft");
			boolean enableRolecraftIntegration = rolecraftSection.getAsBoolean("enable_integration", true);

			IniSection vaultSection = pluginConfig.getSection("Vault");
			boolean enableVaultIntegration = vaultSection.getAsBoolean("enable_integration", true);

			if (enableRolecraftIntegration) {
				if (pm.isPluginEnabled("Rolecraft")) {
					Plugin rolecraft = pm.getPlugin("Rolecraft");

					AsyncPlayerChatEvent.getHandlerList().unregister(rolecraft);
					getLogger().info("Integrated into Rolecraft.");

					isRolecraftIntegrationEnabled = true;
				} else {
					getLogger().warning("Rolecraft integration has been enabled in the config,"
							+ " but this server does not seem to be running Rolecraft.");
				}
			}

			if (enableVaultIntegration) {
				if (pm.isPluginEnabled("Vault")) {
					isVaultIntegrationEnabled = true;
				} else {
					getLogger().warning("Vault integration has been enabled in the config,"
							+ " but this server does not seem to be running Vault.");
				}
			}

			pm.registerEvents(new AsyncPlayerChatListener(this), this);
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
				String message = StringUtility.arrayToString(args, ' ');

				if (message.length() > 0) {
					broadcastGlobalMessage(p, message);
				}
			}

			if (command.getName().equals("whisper")) {
				String message = StringUtility.arrayToString(args, ' ');

				if (message.length() > 0) {
					IniSection localSection = pluginConfig.getSection("Local");
					int range = localSection.getAsInt("whisper_range_in_blocks", 5);

					broadcastLocalMessage(p, message, range);
				}
			}

			if (command.getName().equals("yell")) {
				String message = StringUtility.arrayToString(args, ' ');

				if (message.length() > 0) {
					IniSection localSection = pluginConfig.getSection("Local");
					int range = localSection.getAsInt("yell_range_in_blocks", 20);

					broadcastLocalMessage(p, message, range);
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

	public boolean isRoleCraftIntegrationEnabled() {
		return isRolecraftIntegrationEnabled;
	}

	public void broadcastLocalMessage(Player sender, String message, int range) {
		Location senderLoc = sender.getLocation();
		IniSection localSection = pluginConfig.getSection("Local");
		String format = localSection.getAsString("message_format", "&4A problem occured. Check your RPGChat config.");
		String chatPrefix = localSection.getAsString("prefix", "&4A problem occured. Check your RPGChat config.");
		boolean autoPeriod = localSection.getAsBoolean("auto_period", true);

		String actualMessage = ChatColor.translateAlternateColorCodes('&', format);

		actualMessage = actualMessage.replace("%chatprefix%", ChatColor.translateAlternateColorCodes('&', chatPrefix));
		actualMessage = actualMessage.replace("%player%", sender.getName());
		actualMessage = actualMessage.replace("%equals%", "=");
		actualMessage = actualMessage.replace("%level%", String.valueOf(sender.getLevel()));

		if (isRolecraftIntegrationEnabled) {
			String clazz = RolecraftIntegration.getClass(sender);

			actualMessage = actualMessage.replace("%class%", clazz);
		}

		if (isVaultIntegrationEnabled) {
			String prefix = VaultIntegration.getChat().getPlayerPrefix(sender);
			String suffix = VaultIntegration.getChat().getPlayerSuffix(sender);

			if (prefix == null) {
				prefix = "";
			}

			if (suffix == null) {
				suffix = "";
			}

			actualMessage = actualMessage.replace("%prefix%", prefix);
			actualMessage = actualMessage.replace("%suffix%", suffix);
		}

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
		String chatPrefix = globalSection.getAsString("prefix", "&4A problem occured. Check your RPGChat config.");
		boolean autoPeriod = globalSection.getAsBoolean("auto_period", true);

		String actualMessage = ChatColor.translateAlternateColorCodes('&', format);

		actualMessage = actualMessage.replace("%chatprefix%", ChatColor.translateAlternateColorCodes('&', chatPrefix));
		actualMessage = actualMessage.replace("%player%", sender.getName());
		actualMessage = actualMessage.replace("%equals%", "=");
		actualMessage = actualMessage.replace("%level%", String.valueOf(sender.getLevel()));

		if (isRolecraftIntegrationEnabled) {
			String clazz = RolecraftIntegration.getClass(sender);

			actualMessage = actualMessage.replace("%class%", clazz);
		}
		
		if (isVaultIntegrationEnabled) {
			String prefix = VaultIntegration.getChat().getPlayerPrefix(sender);
			String suffix = VaultIntegration.getChat().getPlayerSuffix(sender);

			if (prefix == null) {
				prefix = "";
			}

			if (suffix == null) {
				suffix = "";
			}

			actualMessage = actualMessage.replace("%prefix%", prefix);
			actualMessage = actualMessage.replace("%suffix%", suffix);
		}

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
