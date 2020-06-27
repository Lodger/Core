package tk.retromc.Core;

import java.io.File;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.Location;

public class DelwarpExecutor implements CommandExecutor {
	private Core instance;

	public DelwarpExecutor(Core i) {
		instance = i;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String args[]) {
		if (!sender.hasPermission("core.del")) {
			sender.sendMessage(instance.getMessage("permissionDenied"));
			return false;
		}

		File f = new File(instance.getDataFolder(), "warps.yml");
		YamlConfiguration warps = YamlConfiguration.loadConfiguration(f);

		if (args.length == 0) {
			sender.sendMessage(instance.getMessage("warp.noargs"));
			return false;
		}

		if (!warps.isSet(args[0])) {
			sender.sendMessage(instance.getMessage("delwarp.invalid"));
			return false;
		}

		warps.set(args[0], null);

		try {
			warps.save(f);
		} catch (Exception x) {
			x.printStackTrace();
		}

		sender.sendMessage(instance.getMessage("delwarp.confirm").replaceAll("\\$warpname", args[0]));
		return true;
	}
}
