package tk.retromc.Core;

import java.util.Arrays;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

public class FlyExecutor extends Applicator {
	public FlyExecutor(Core i) {
		instance = i;
		isSelf = true;
		messagePath = "fly";
	}

	public boolean executeCommand(CommandSender sender, Command cmd, String label, Player target, String args[]) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(instance.getMessage("invalidSender"));
			return false;
		}

		if (!sender.hasPermission("core.fly")) {
			sender.sendMessage(instance.getMessage("permissionDenied"));
			return false;
		}

		if (target.getUniqueId() != ((Player) sender).getUniqueId() && !sender.hasPermission("core.fly.other")) {
			sender.sendMessage(instance.getMessage("permissionDenied"));
			return false;
		}

		boolean flyStatus;
		flyStatus = target.getAllowFlight();

		target.setAllowFlight(!flyStatus);
		target.setFlying(!flyStatus);

		target.sendMessage(instance.getMessage(target.getAllowFlight() ? "fly.enable" : "fly.disable"));

		if (target != sender)
			sender.sendMessage(instance.getMessage(target.getAllowFlight() ?
			                                       "fly.enableOther" :
			                                       "fly.disableOther").replaceAll("\\$player", target.getName()));

		return true;
	}
}
