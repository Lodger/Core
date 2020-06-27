package tk.retromc.Core;

import java.io.File;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.ChatColor;
import org.bukkit.Bukkit;

public class Core extends JavaPlugin {
	public YamlConfiguration messages;
	public YamlConfiguration config;

	public HashMap<UUID, Entry<UUID, UUID>> tpaMap; /* I do not know how to make this private  */

	private File configFile = new File(getDataFolder(), "config.yml");
	private File messageFile = new File(getDataFolder(), "messages.yml");

	@Override
	public void onEnable() {
		config = getDefaultFile(configFile);
		messages = getDefaultFile(messageFile);

		tpaMap = new HashMap<UUID, Entry<UUID, UUID>>();

		Bukkit.getPluginManager().registerEvents(new SeenListener(this), this);
		Bukkit.getPluginManager().registerEvents(new HomeListener(this), this);
		Bukkit.getPluginManager().registerEvents(new SignListener(), this);

		getCommand("fly").setExecutor(new FlyExecutor(this));
		getCommand("seen").setExecutor(new SeenExecutor(this));
		getCommand("enderchest").setExecutor(new EnderchestExecutor(this));
		getCommand("home").setExecutor(new HomeExecutor(this));
		getCommand("homes").setExecutor(new HomesExecutor(this));
		getCommand("delhome").setExecutor(new DelhomeExecutor(this));
		getCommand("warp").setExecutor(new WarpExecutor(this));
		getCommand("setwarp").setExecutor(new SetwarpExecutor(this));
		getCommand("delwarp").setExecutor(new DelwarpExecutor(this));
		getCommand("addfriend").setExecutor(new AddfriendExecutor(this));
		getCommand("removefriend").setExecutor(new RemovefriendExecutor(this));
		getCommand("homeholo").setExecutor(new HomeholoExecutor(this));
		getCommand("tpa").setExecutor(new TpaExecutor(this));
		getCommand("tpahere").setExecutor(new TpahereExecutor(this));
		getCommand("tpaccept").setExecutor(new TpacceptExecutor(this));
	}

	public File getPlayerFile(UUID target, boolean overwrite) {
		File f = new File(getDataFolder(), config.getString("userDataPath") + target + ".yml");

		try {
			if (overwrite && !f.exists())
				f.createNewFile();
		} catch (Exception x) {
			x.printStackTrace();
		}

		return f;
	}

	public YamlConfiguration getDefaultFile(File f) {
		if (!f.exists())
			saveResource(f.getName(), false);
		return YamlConfiguration.loadConfiguration(f);
	}

	/* not the best solution */
	public String getMessage(String path) {
		if (messages.getString(path) == null)
			saveResource(messageFile.getName(), true);

		return color(messages.getString(path));
	}

	public String color(String s) {
		return ChatColor.translateAlternateColorCodes('&', s);
	}
}
