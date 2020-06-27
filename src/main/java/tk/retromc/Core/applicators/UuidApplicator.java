package tk.retromc.Core;

import java.util.TreeMap;
import java.util.Arrays;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public abstract class UuidApplicator implements CommandExecutor {
	public Core instance;

	public boolean isSelf;
	public String messagePath;

	abstract public boolean executeCommand(CommandSender sender, Command cmd, String label, UUID target, String name, String args[]);

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String args[]) {
		if (args.length == 0) {
			if (isSelf) {
				if (sender instanceof Player) {
					args = new String[] { ((Player) sender).getName() };
				} else {
					sender.sendMessage(instance.getMessage("noTarget"));
					return false;
				}
			} else {
				sender.sendMessage(instance.getMessage(messagePath + ".usageMessage"));
				return false;
			}
		}


		/* count the number of players in the list */

		TreeMap<UUID, String> targets = new TreeMap<UUID, String>();
		OfflinePlayer p;

		int players;
		for (players = 0; players < args.length; ++players) {
			/* check for a player with the name of args[players], minus the last character, a comma */
			p = Bukkit.getOfflinePlayer(args[players].replaceAll(",", ""));
			if (p == null) {
				sender.sendMessage(instance.getMessage("playerNotFound"));
				return false;
			}

			targets.put(p.getUniqueId(), p.getName());

			/* this should really go in the loop declaration but it's very long */
			if (args[players].charAt(args[players].length() - 1) != ',')
				break;
		}

		/* we want the number of players, not the index of the last one */
		++players;

		/* execArgs will hold the arguments minus the player names */
		String execArgs[] = Arrays.copyOfRange(args, players, args.length);

		for (UUID target : targets.keySet())
			if (!executeCommand(sender, cmd, label, target, targets.get(target), execArgs))
				return false;

		return true;
	}
}
