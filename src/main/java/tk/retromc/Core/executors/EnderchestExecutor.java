package tk.retromc.Core;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

public class EnderchestExecutor implements CommandExecutor {
	private Core instance;

	public EnderchestExecutor(Core i) {
		instance = i;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String args[]) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(instance.getMessage("invalidSender"));
			return false;
		}

		if (!sender.hasPermission("core.enderchest")) {
			sender.sendMessage(instance.getMessage("permissionDenied"));
			return false;
		}

		if (args.length == 0) {
			((Player) sender).openInventory(((Player) sender).getEnderChest());
			return true;
		}

		if (!sender.hasPermission("core.enderchest.other")) {
			sender.sendMessage(instance.getMessage("permissionDenied"));
			return false;
		}

		if (args.length != 1) {
			sender.sendMessage(instance.getMessage("enderchest.usageMessage"));
			return false;
		}

		Player other;
		if ((other = Bukkit.getPlayer(args[0])) == null) {
			sender.sendMessage(instance.getMessage("playerNotFound"));
			return false;
		}

		((Player) sender).openInventory(other.getEnderChest());
		return true;
	}
}
