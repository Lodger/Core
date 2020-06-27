package tk.retromc.Core;

import java.util.AbstractMap.SimpleEntry;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

public class TpacceptExecutor implements CommandExecutor {
	Core instance;

	public TpacceptExecutor(Core i) {
		instance = i;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String args[]) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(instance.getMessage("invalidSender"));
			return false;
		}

		UUID u = ((Player) sender).getUniqueId();

		if (!instance.tpaMap.containsKey(u)) {
			sender.sendMessage(instance.getMessage("tpaccept.noRequests"));
			return false;
		}

		UUID dispatcherId;
		dispatcherId = ((Player) sender).getUniqueId();

		Player dest, target;
		dest = Bukkit.getPlayer(instance.tpaMap.get(dispatcherId).getValue());
		target = Bukkit.getPlayer(instance.tpaMap.get(dispatcherId).getKey());

		if (!dest.isOnline() || !target.isOnline()) {
			sender.sendMessage(instance.getMessage("playerNotFound"));
			return false;
		}

		target.teleport(dest.getLocation());

		instance.tpaMap.remove(dispatcherId);

		return true;
	}
}
