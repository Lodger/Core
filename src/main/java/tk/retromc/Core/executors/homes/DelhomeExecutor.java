package tk.retromc.Core;

import java.io.File;
import java.util.UUID;
import java.util.Set;

import org.bukkit.entity.ArmorStand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.OfflinePlayer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

public class DelhomeExecutor implements CommandExecutor {
	private Core instance;

	public DelhomeExecutor(Core i) {
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

		if (args.length == 0) {
			sender.sendMessage(instance.getMessage("home.noHomes"));
			return false;
		}

		File f;
		if (args.length > 1 && sender.hasPermission("core.home.other")) {
			OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
			if (p == null) {
				sender.sendMessage(instance.getMessage("playerNotFound"));
				return false;
			}

			f = instance.getPlayerFile(p.getUniqueId(), false);
			args[0] = args[1];
		} else {
			f = instance.getPlayerFile(((Player) sender).getUniqueId(), false);
		}

		YamlConfiguration playerdata = YamlConfiguration.loadConfiguration(f);

		if (playerdata.getConfigurationSection("homes") == null) {
			sender.sendMessage(instance.getMessage("home.noHomes"));
			return false;
		}

		if (!playerdata.isSet("homes." + args[0])) {
			sender.sendMessage(instance.getMessage("home.notFound").replaceAll("\\$homename", args[0]));
			return false;
		}

		Location loc = new Location(Bukkit.getWorld(playerdata.getString("homes." + args[0] + ".world")),
		                            playerdata.getInt("homes." + args[0] + ".x"),
		                            playerdata.getInt("homes." + args[0] + ".y"),
		                            playerdata.getInt("homes." + args[0] + ".z"));

		loc.getBlock().setType(Material.AIR);

		sender.sendMessage(playerdata.getString("homes." + args[0] + ".holoUUID"));

		ArmorStand a = (ArmorStand) Bukkit.getEntity(UUID.fromString(playerdata.getString("homes." + args[0] + ".holoUUID")));
		a.remove();

		playerdata.set("homes." + args[0], null);

		try {
			playerdata.save(f);
		} catch (Exception x) {
			x.printStackTrace();
		}

		((Player) sender).getInventory().addItem(new ItemStack(Material.BEDROCK, 1));
		sender.sendMessage(instance.getMessage("home.deleted").replaceAll("\\$homename", args[0]));

		return true;
	}
}
