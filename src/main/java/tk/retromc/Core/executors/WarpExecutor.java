package tk.retromc.Core;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.Location;

public class WarpExecutor implements CommandExecutor {
	private Core instance;

	public WarpExecutor(Core i) {
		instance = i;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String args[]) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(instance.getMessage("invalidSender"));
			return false;
		}

		if (!sender.hasPermission("core.warp")) {
			sender.sendMessage(instance.getMessage("permissionDenied"));
			return false;
		}

		if ("spawn".equals(label))
			args = new String[] {"spawn"};

		File f = new File(instance.getDataFolder(), "warps.yml");
		ConfigurationSection warps = YamlConfiguration.loadConfiguration(f);

		if (args.length == 0) {
			ArrayList<String> available = new ArrayList<String>();

			if (warps.getKeys(false).size() == 0) {
				sender.sendMessage(instance.getMessage("warp.noWarps"));
				return true;
			}

			for (String s : warps.getKeys(false))
//				if (sender.hasPermission("core.warp." + s))
					available.add(s);

			sender.sendMessage(instance.getMessage("warp.warpList").replaceAll("\\$warplist", String.join(", ", available)));
			return true;
		}

		warps = warps.getConfigurationSection(args[0]);

		if (warps == null) {
			sender.sendMessage(instance.getMessage("warp.invalid"));
			return false;
		}

//		if (!sender.hasPermission("core.warp." + args[0])) {
//			sender.sendMessage(instance.getMessage("permissionDenied"));
//			return false;
//		}

		((Player) sender).teleport(new Location(Bukkit.getWorld(warps.getString("world")),
		                                        (float) warps.getDouble("x"),
		                                        (float) warps.getDouble("y"),
		                                        (float) warps.getDouble("z"),
		                                        (float) warps.getDouble("yaw"),
		                                        (float) warps.getDouble("pitch")));

		return true;
	}
}
