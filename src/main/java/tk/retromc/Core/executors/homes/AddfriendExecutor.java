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

public class AddfriendExecutor implements CommandExecutor {
	private Core instance;

	public AddfriendExecutor(Core i) {
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

		if (args.length == 1) {
			/* send message they fucked up */
			return false;
		}

		File f = instance.getPlayerFile(((Player) sender).getUniqueId(), false);
		YamlConfiguration playerdata = YamlConfiguration.loadConfiguration(f);

		if (playerdata.getConfigurationSection("homes").getKeys(false).size() == 0) {
			sender.sendMessage(instance.getMessage("home.noHomes"));
			return false;
		}

		/*
		 * yes, it would have looked nicer to just extend UuidApplicator
		 * here, but it makes absolutely no sense to do that once it
		 * gets down to adding the UUIDs to the friends list, since
		 * right now that involves reading the old friends list first,
		 * and rewriting the whole thing.
		 */
		int home;
		for (home = 0; home < args.length && args[home].charAt(args[home].length() - 1) == ','; ++home);
		++home;

		/* home now points to the home name */

		if (playerdata.getConfigurationSection("homes." + args[home]) == null) {
			sender.sendMessage(instance.getMessage("home.notFound").replaceAll("\\$homename", args[home]));
			return false;
		}

		/*
		 * if anyone reading this knows of a better way to do this,
		 * please tell me. I want to add to a list in a
		 * ConfigurationSection object, so I need to first pull all the
		 * old list elements, and then add the new ones, then set that
		 * master list as the new value. This feels very inefficient.
		 */
		List<String> friends = playerdata.getStringList("homes." + args[home] + ".friends");

		for (int i = 0; i < home; ++i) {
			OfflinePlayer p;

			p = Bukkit.getOfflinePlayer(args[i].replaceAll(",", ""));
			if (p == null) {
				sender.sendMessage(instance.getMessage("playerNotFound"));
				return false;
			}

			friends.add(p.getUniqueId().toString());

			/* notify parties */
			/* TODO: fix ths mess */
			sender.sendMessage(instance.getMessage("homefriend.addedFriend").replaceAll("\\$friend", p.getName()).replaceAll("\\$homename", args[home]));
			if (p.isOnline())
				((Player) p).sendMessage(instance.getMessage("homefriend.addedYou").replaceAll("\\$friend", sender.getName()).replaceAll("\\$homename", args[home]));
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
