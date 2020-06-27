package tk.retromc.Core;

import java.util.LinkedHashMap;
import java.io.File;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.Location;

public class SeenListener implements Listener {
	private Core instance;

	public SeenListener(Core i) {
		instance = i;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		if (!e.getPlayer().hasPlayedBefore())
			e.getPlayer().performCommand("spawn");

		File f = instance.getPlayerFile(e.getPlayer().getUniqueId(), true);
		YamlConfiguration playerdata = YamlConfiguration.loadConfiguration(f);

		playerdata.set("ip", e.getPlayer().getAddress().toString().replaceAll("/", "").split(":")[0]);
		playerdata.set("name", e.getPlayer().getName());
		playerdata.set("time", System.currentTimeMillis());

		try {
			playerdata.save(f);
		} catch (Exception x) {
			x.printStackTrace();
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		File f = instance.getPlayerFile(e.getPlayer().getUniqueId(), true);
		YamlConfiguration playerdata = YamlConfiguration.loadConfiguration(f);

		Location coords = e.getPlayer().getLocation();
		LinkedHashMap<String, Integer> vals = new LinkedHashMap<String, Integer>();

		vals.put("x", coords.getBlockX());
		vals.put("y", coords.getBlockY());
		vals.put("z", coords.getBlockZ());

		playerdata.set("time", System.currentTimeMillis());
		playerdata.createSection("lastcoords", vals);

		try {
			playerdata.save(f);
		} catch (Exception x) {
			x.printStackTrace();
		}
	}
}
