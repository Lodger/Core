package tk.retromc.Core;

import java.io.File;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.OfflinePlayer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.Location;

/* this class is a UuidApplicator in disguise */
public class RemovefriendExecutor implements CommandExecutor {
	private Core instance;

	public RemovefriendExecutor(Core i) {
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

		File f = instance.getPlayerFile(((Player) sender).getUniqueId(), false);
		YamlConfiguration playerdata = YamlConfiguration.loadConfiguration(f);

		if (playerdata.getConfigurationSection("homes").getKeys(false).size() == 0) {
			sender.sendMessage(instance.getMessage("home.noHomes"));
			return false;
		}

		int home;
		for (home = 0; home < args.length && args[home].charAt(args[home].length() - 1) == ','; ++home);
		++home;

		if (playerdata.getConfigurationSection("homes." + args[home]) == null) {
			sender.sendMessage(instance.getMessage("home.notFound").replaceAll("\\$homename", args[home]));
			return false;
		}

		List<String> friends = playerdata.getStringList("homes." + args[home] + ".friends");

		for (int i = 0; i < home; ++i) {
			OfflinePlayer p;

			p = Bukkit.getOfflinePlayer(args[i].replaceAll(",", ""));
			if (p == null) {
				sender.sendMessage(instance.getMessage("playerNotFound"));
				return false;
			}

			friends.remove(p.getUniqueId().toString());

			/* TODO: fix this mess */
			sender.sendMessage(instance.getMessage("homefriend.removedFriend").replaceAll("\\$friend", p.getName()).replaceAll("\\$homename", args[home]));
			if (p.isOnline())
				((Player) p).sendMessage(instance.getMessage("homefriend.removedYou").replaceAll("\\$friend", sender.getName()).replaceAll("\\$homename", args[home]));
		}

		playerdata.set("homes." + args[home] + ".friends", friends);

		try {
			playerdata.save(f);
		} catch (Exception x) {
			x.printStackTrace();
		}

		return true;
	}
}
