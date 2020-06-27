package tk.retromc.Core;

import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.Date;
import java.util.UUID;
import java.io.File;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.Bukkit;

public class SeenExecutor extends UuidApplicator {
	private SimpleDateFormat dateFormat;
	private SimpleDateFormat timeFormat;

	public SeenExecutor(Core i) {
		instance = i;

		isSelf = true;
		messagePath = "seen";

		/* format for displaying dates */
		dateFormat = new SimpleDateFormat(instance.config.getString("dateFormat"));
		dateFormat.setTimeZone(TimeZone.getTimeZone(instance.config.getString("timeZone")));

		/* format for displaying just time */
		timeFormat = new SimpleDateFormat(instance.config.getString("timeFormat"));
		timeFormat.setTimeZone(TimeZone.getTimeZone(instance.config.getString("timeZone")));
	}

	public boolean executeCommand(CommandSender sender, Command cmd, String label, UUID target, String name, String args[]) {
		if (!sender.hasPermission("core.seen")) {
			sender.sendMessage(instance.getMessage("permissionDenied"));
			return false;
		}

		if (((Player) sender).getUniqueId() != target && !sender.hasPermission("core.seen.other")) {
			sender.sendMessage(instance.getMessage("permissionDenied"));
			return false;
		}

		File f = instance.getPlayerFile(target, false);

		if (!f.exists()) {
			sender.sendMessage(instance.getMessage("playerNotFound"));
			return false;
		}

		YamlConfiguration playerdata = YamlConfiguration.loadConfiguration(f);
		String messageFormat;

		if ((sender instanceof Player && ((Player) sender).getUniqueId() == target) || sender.hasPermission("core.seen.other.enhanced"))
			messageFormat = Bukkit.getPlayer(target) != null ? "seen.opOnline" : "seen.opOffline";
		else 
			messageFormat = Bukkit.getPlayer(target) != null ? "seen.online" : "seen.offline";

		messageFormat = instance.getMessage(messageFormat);
		sender.sendMessage(decorate(messageFormat, playerdata, sender));
		return true;
	}

	private String decorate(String s, YamlConfiguration d, CommandSender f) {
		String date;

		date = dateFormat.format(new Date(d.getLong("time")));

		/* TODO fix this mess */
		return s.replaceAll("\\$player", d.getString("name")).replaceAll("\\$date", date).replaceAll("\\$ip", d.getString("ip")).replaceAll("\\$x", Integer.toString(d.getInt("lastcoords.x"))).replaceAll("\\$y", Integer.toString(d.getInt("lastcoords.y"))).replaceAll("\\$z", Integer.toString(d.getInt("lastcoords.z")));
	}
}
