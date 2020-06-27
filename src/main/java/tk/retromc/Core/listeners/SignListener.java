package tk.retromc.Core;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.ChatColor;

public class SignListener implements Listener {
	@EventHandler
	public void onSignChange(SignChangeEvent e) {
		if (!e.getPlayer().hasPermission("!core.signformatting"))
			return;

		for (int i = 0; i < e.getLines().length; ++i)
			e.setLine(i, ChatColor.translateAlternateColorCodes('&', e.getLine(i)));
	}
}
