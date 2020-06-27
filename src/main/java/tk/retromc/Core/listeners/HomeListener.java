package tk.retromc.Core;

import java.util.UUID;
import java.util.LinkedHashMap;
import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ArmorStand;

public class HomeListener implements Listener {
	private Core instance;
	private LinkedHashMap<UUID, LinkedHashMap<String, Object>> homeQueue;

	public HomeListener(Core i) {
		instance = i;
		homeQueue = new LinkedHashMap<UUID, LinkedHashMap<String, Object>>();
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPlace(BlockPlaceEvent e) {
		if (e.isCancelled() ||
		    e.getBlockPlaced().getType() != Material.BEDROCK ||
		    !e.getPlayer().hasPermission("core.home"))
			return;

		LinkedHashMap<String, Object> vals = new LinkedHashMap<String, Object>();

		Block b = e.getBlockPlaced();

		vals.put("world", b.getWorld().getName());
		vals.put("x", b.getX());
		vals.put("y", b.getY());
		vals.put("z", b.getZ());

		vals.put("tempLoc", shift(b.getLocation()));

		homeQueue.put(e.getPlayer().getUniqueId(), vals);
		e.getPlayer().sendMessage(instance.getMessage("home.namePrompt"));
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onChat(PlayerChatEvent e) {
		if (!homeQueue.containsKey(e.getPlayer().getUniqueId()))
			return;

		String home = e.getMessage().split(" ")[0];

		if ("!cancel".equals(home)) {
			((Location) homeQueue.get(e.getPlayer().getUniqueId()).get("tempLoc")).getBlock().setType(Material.AIR);
			e.getPlayer().getInventory().addItem(new ItemStack(Material.BEDROCK, 1));
			homeQueue.remove(e.getPlayer().getUniqueId());
			e.setCancelled(true);
			return;
		}

		File f = instance.getPlayerFile(e.getPlayer().getUniqueId(), false);
		YamlConfiguration playerdata = YamlConfiguration.loadConfiguration(f);

		if (!playerdata.contains("homes"))
			playerdata.createSection("homes");

		Location loc = (Location) homeQueue.get(e.getPlayer().getUniqueId()).get("tempLoc");
		ArmorStand a = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);

		homeQueue.get(e.getPlayer().getUniqueId()).put("holoUUID", a.getUniqueId().toString());
		homeQueue.get(e.getPlayer().getUniqueId()).remove("tempLoc");

		a.setVisible(false);
		a.setGravity(false);
		a.setInvulnerable(true);

		a.setCustomName(home);
		a.setCustomNameVisible(true);

		playerdata.createSection("homes." + home, homeQueue.get(e.getPlayer().getUniqueId()));
		playerdata.createSection("homes." + home + ".friends");

		try {
			playerdata.save(f);
		} catch (Exception x) {
			x.printStackTrace();
		}

		e.getPlayer().sendMessage(instance.getMessage("home.confirmation").replaceAll("\\$homename", home));
		e.setCancelled(true);
		homeQueue.remove(e.getPlayer().getUniqueId());
	}

	private Location shift(Location loc) {
		loc.setX(loc.getX() + 0.5);
		loc.setY(loc.getY() + 1);
		loc.setZ(loc.getZ() + 0.5);

		return loc;
	}
}
