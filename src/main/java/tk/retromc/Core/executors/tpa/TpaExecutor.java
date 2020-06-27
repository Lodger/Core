package tk.retromc.Core;

import java.util.AbstractMap.SimpleEntry;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class TpaExecutor extends Applicator {
	public TpaExecutor(Core i) {
		instance = i;
		isSelf = false;
		messagePath = "tpa";
	}

	public boolean executeCommand(CommandSender sender, Command cmd, String label, Player target, String args[]) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(instance.getMessage("invalidSender"));
			return false;
		}

		UUID dispatcherId, targetId;
		dispatcherId = ((Player) sender).getUniqueId();
		targetId = target.getUniqueId();

		if (!sender.hasPermission("core.tpa")) {
			sender.sendMessage(instance.getMessage("permissionDenied"));
			return false;
		}

		if (target == (Player) sender) {
			sender.sendMessage(instance.getMessage("tpa.usageMessage"));
			return false;
		}

		instance.tpaMap.put(targetId, new SimpleEntry(dispatcherId, targetId));

		sender.sendMessage(instance.getMessage("tpa.send").replaceAll("\\$target", target.getName()));
		target.sendMessage(instance.getMessage("tpa.recieve").replaceAll("\\$dispatcher", sender.getName()));

		new TpaCanceler(instance, targetId).runTaskLater(instance, 1200);
		return true;
	}
}
