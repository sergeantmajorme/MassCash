package me.SgtMjrME.MassCash;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class MCPlayerListener implements Listener{
	private MassCash plugin;
	
	MCPlayerListener(MassCash hi)
	{
		plugin = hi;
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void PlayerEvent(PlayerJoinEvent e)
	{
		plugin.loggedin(e.getPlayer());
	}
}
