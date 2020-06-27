package tk.retromc.Core;

import java.io.File;
import java.util.UUID;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.OfflinePlayer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.Location;

public class HomeExecutor implements CommandExecutor {
	private Core instance;

	public HomeExecutor(Core i) {
		instance = i;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String args[]) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(instance.getMessage("invalidSender"));
			return false;
		}

		if (!sender.hasPermission("core.home")) {
			sender.sendMessage(instance.getMessage("permissionDenied"));
			return false;
		}

		if (args.length == 0) {
			((Player) sender).performCommand("homes");
			return false;
		}

		UUID uuid = ((Player) sender).getUniqueId();

		File f;
		if (args.length > 1) {
			OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
			if (p == null) {
				sender.sendMessage(instance.getMessage("playerNotFound"));
				return false;
			}

			f = instance.getPlayerFile(p.getUniqueId(), false);
			args[0] = args[1];
		} else {
			f = instance.getPlayerFile(uuid, false);
		}

		ConfigurationSection playerdata = YamlConfiguration.loadConfiguration(f).getConfigurationSection("homes");

		if (playerdata == null ) {
			sender.sendMessage(instance.getMessage("home.noHomes"));
			return false;
		}

		/* if sender is targeting somebody else, isn't friends with them, or doesn't have permission to */
		if (args.length > 1 && !(areFriends(args[0], playerdata, uuid) || sender.hasPermission("core.home.other"))) {
			sender.sendMessage(instance.getMessage("permissionDenied"));
			return false;
		} else if ((playerdata = playerdata.getConfigurationSection(args[0])) == null) {
			sender.sendMessage(instance.getMessage("home.notFound").replaceAll("\\$homename", args[0]));
			return false;
		}

		((Player) sender).teleport(new Location(Bukkit.getWorld(playerdata.getString("world")),
		                                        playerdata.getInt("x") + 0.5,
		                                        playerdata.getInt("y") + 1,
		                                        playerdata.getInt("z") + 0.5));

		return true;
	}

	private boolean areFriends(String home, ConfigurationSection homes, UUID uuid) {
		return homes.getStringList(home + ".friends").contains(uuid.toString());
	}
}
