package tk.retromc.Core;

import java.util.UUID;

import org.bukkit.scheduler.BukkitRunnable;

public class TpaCanceler extends BukkitRunnable {
	Core instance;
	UUID key;

	public TpaCanceler(Core i, UUID k) {
		instance = i;
		key = k;
	}

	public void run() {
		instance.tpaMap.remove(key);
	}
}
