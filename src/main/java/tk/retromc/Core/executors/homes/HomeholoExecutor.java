package tk.retromc.Core;

import java.io.File;
import java.util.UUID;
import java.util.Arrays;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.OfflinePlayer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;

public class HomeholoExecutor implements CommandExecutor {
	private Core instance;

	public HomeholoExecutor(Core i) {
		instance = i;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String args[]) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(instance.getMessage("invalidSender"));
			return false;
		}

		if (args.length == 0) {
			sender.sendMessage(instance.getMessage("homeholo.usageMessage"));
			return true;
		}

		if (!sender.hasPermission("core.home")) {
			sender.sendMessage(instance.getMessage("permissionDenied"));
			return false;
		}

		File f = instance.getPlayerFile(((Player) sender).getUniqueId(), false);
		ConfigurationSection playerdata = YamlConfiguration.loadConfiguration(f).getConfigurationSection("homes");

		if (playerdata == null) {
			sender.sendMessage(instance.getMessage("home.noHomes"));
			return false;
		}

		if ((playerdata = playerdata.getConfigurationSection(args[0])) == null) {
			sender.sendMessage(instance.getMessage("home.notFound").replaceAll("\\$homename", args[0]));
			return false;
		}

		ArmorStand a = (ArmorStand) Bukkit.getEntity(UUID.fromString(playerdata.getString("holoUUID")));

		if ("disable".equals(args[1])) {
			a.setCustomNameVisible(false);
			sender.sendMessage(instance.getMessage("homeholo.disabled"));
			return true;
		} else if ("enable".equals(args[1])) {
			a.setCustomNameVisible(true);
			sender.sendMessage(instance.getMessage("homeholo.enabled"));
			return true;
		} else if ("write".equals(args[1]) && args.length > 2) {
			String s = instance.color(String.join(" ", Arrays.copyOfRange(args, 2, args.length)));

			a.setCustomName(s);

			sender.sendMessage(instance.getMessage("homeholo.enabled"));
			return true;
		}

		sender.sendMessage(instance.getMessage("homeholo.usageMessage"));
		return false;
	}

	private boolean areFriends(String home, ConfigurationSection homes, UUID uuid) {
		return homes.getStringList(home + ".friends").contains(uuid.toString());
	}
}
