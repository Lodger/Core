package tk.retromc.Core;

import java.io.File;
import java.util.UUID;
import java.util.Set;
import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.OfflinePlayer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.Location;

public class HomesExecutor extends UuidApplicator {
	public HomesExecutor(Core i) {
		instance = i;

		isSelf = true;
		messagePath = "seen";
	}

	public boolean executeCommand(CommandSender sender, Command cmd, String label, UUID target, String name, String args[]) {
		if (!sender.hasPermission("core.home")) {
			sender.sendMessage(instance.getMessage("permissionDenied"));
			return false;
		}

		File f = instance.getPlayerFile(target, false);
		ConfigurationSection playerdata = YamlConfiguration.loadConfiguration(f).getConfigurationSection("homes");

		if (playerdata == null) {
			sender.sendMessage(instance.getMessage("home.noHomes"));
			return false;
		}

		Set<String> homes = playerdata.getKeys(false);
		
		UUID uuid = ((Player) sender).getUniqueId();

		/* remove all homes that sender doesn't have permission to see */
		if (!sender.hasPermission("core.seen.other") && target != uuid) {
			for (String s : playerdata.getKeys(false))
				if (!playerdata.getStringList(s + ".friends").contains(uuid.toString()))
					homes.remove(s);
		}

		if (homes.size() == 0) {
			sender.sendMessage(instance.getMessage("home.noHomes"));
			return false;
		}

		/* TODO fix this mess */
		sender.sendMessage(instance.getMessage("homes.format").replaceAll("\\$quantity", Integer.toString(homes.size())).replaceAll("\\$homes", String.join(", ", homes)));
		return true;
	}
}
