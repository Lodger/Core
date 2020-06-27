package tk.retromc.Core;

import java.io.File;
import java.util.ArrayList;
import java.util.TreeMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.Location;

public class SetwarpExecutor implements CommandExecutor {
	private Core instance;

	public SetwarpExecutor(Core i) {
		instance = i;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String args[]) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(instance.getMessage("invalidSender"));
			return false;
		}

		if (!sender.hasPermission("core.setwarp")) {
			sender.sendMessage(instance.getMessage("permissionDenied"));
			return false;
		}

		File f = new File(instance.getDataFolder(), "warps.yml");
		YamlConfiguration warps = YamlConfiguration.loadConfiguration(f);

		if (args.length == 0) {
			sender.sendMessage(instance.getMessage("warp.noargs"));
			return true;
		}

		if (warps.isSet(args[0])) {
			sender.sendMessage(instance.getMessage("setwarp.dupe").replaceAll("\\$warpname", args[0]));
			return false;
		}

		TreeMap<String, Object> vals = new TreeMap<String, Object>();
		Location loc = ((Player) sender).getLocation();

		vals.put("world", loc.getWorld().getName());
		vals.put("x", loc.getX());
		vals.put("y", loc.getY());
		vals.put("z", loc.getZ());
		vals.put("pitch", loc.getPitch());
		vals.put("yaw", loc.getYaw());

		warps.createSection(args[0], vals);

		try {
			warps.save(f);
		} catch (Exception x) {
			x.printStackTrace();
		}

		sender.sendMessage(instance.getMessage("setwarp.confirm").replaceAll("\\$warpname", args[0]));
		return true;
	}
}
