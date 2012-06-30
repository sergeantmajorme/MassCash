package me.SgtMjrME.MassCash;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.earth2me.essentials.Essentials;

public class MassCash extends JavaPlugin{
	private PluginManager pm;
	private Essentials essentials;
	private Logger log;
	private boolean onlogin;
	private boolean auto;
	private double amt;
	private MCPlayerListener playerListener = new MCPlayerListener(this);
	private ArrayList<Player> paidPlayers = new ArrayList<Player>();
	
	@Override
	public void onEnable()
	{
		log = getServer().getLogger();
		pm = getServer().getPluginManager();
		pm.registerEvents(playerListener, this);
		essentials = (Essentials) getServer().getPluginManager().getPlugin("Essentials");
		this.getConfig();
		setStats();
		File f = new File("MassCashCrash.dat");
		if (f.exists())
		{
			if (auto)
				onlogin = true;
			log.info("[MassCash] CRASH DETECTED");
		}
		else
		{
			try {
				f.createNewFile();
				log.info("[MassCash] Crash detection file created");
			} catch (IOException e) {
				log.info("[MassCash] Could not create crash file");
			}
		}
		log.info("[MassCash] Setting up value's");
		log.info("[MassCash] Auto: " + auto + "   Onlogin: " + onlogin + "   Amount: " + amt);
		log.info("[MassCash] Loaded");
	}
	
	@Override
	public void onDisable()
	{
		File f = new File("MassCashCrash.dat");
		if (f.exists())
			f.delete();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		Player player = (Player) sender;
		if (!player.isOp())
			return false;
		if (args.length >= 1 && args[0].equalsIgnoreCase("reload"))
		{
			if (args.length == 3)
			{
				if (args[1].equalsIgnoreCase("amount") && isDouble(args[2]))
					this.getConfig().set("amountPaid", Double.parseDouble(args[2]));
				else if (args[1].equalsIgnoreCase("auto") && (args[2].equals("true") || args[2].equals("false")))
					this.getConfig().set("autoCrashDetect", Boolean.parseBoolean(args[2]));
				else if (args[1].equalsIgnoreCase("onlogin") && (args[2].equals("true") || args[2].equals("false")))
					this.getConfig().set("onlogin", Boolean.parseBoolean(args[2]));
				this.saveConfig();
			}
			this.reloadConfig();
			this.getConfig();
			setStats();
			player.sendMessage("[MassCash] Auto: " + auto + "   Onlogin: " + onlogin + "   Amount: " + amt);
		}
		else if (args[0].equalsIgnoreCase("reset"))
		{
			reset();
			player.sendMessage("All variables reset");
		}
		else if (args[0].equalsIgnoreCase("online") && args.length == 2)
		{
			if (!isDouble(args[1])){
				player.sendMessage("Please put in a number to be paid");
				return true;
			}
			Player[] list = getServer().getOnlinePlayers();
			if (paidPlayers.isEmpty())
				player.sendMessage("All players given $" + args[1]);
			else
				player.sendMessage("All players who have not received money have been given $" + args[1]);
			for (int x = 0; x < list.length; x++)
			{
				if (!paidPlayers.contains(list[x])){
					essentials.getUser(list[0]).giveMoney(Double.parseDouble(args[1]));
					paidPlayers.add(list[x]);//You can call this many times and not pay the same person twice
				}
			}
			return true;
		}
		else if (args[0].equalsIgnoreCase("clearplayers"))
			paidPlayers.clear();
		else
			player.sendMessage("Incorrect command");
		return true;
	}

	private void reset() {
		paidPlayers.clear();
		this.getConfig().set("amountPaid", 0);
		this.getConfig().set("autoCrashDetect", false);
		this.getConfig().set("onlogin", false);
		this.saveConfig();
		this.reloadConfig();
		this.getConfig();
		setStats();
	}

	private void setStats() {
		onlogin = this.getConfig().getBoolean("onlogin");
		amt = this.getConfig().getDouble("amountPaid");
		auto = this.getConfig().getBoolean("autoCrashDetect");
	}

	public void loggedin(Player player) {
		if (onlogin && essentials.getUser(player) != null && !paidPlayers.contains(player))
		{
			player.sendMessage("Crash has been detected since last login");
			player.sendMessage("Paying $" + amt + " to cover damages");
			essentials.getUser(player).giveMoney(amt);
			paidPlayers.add(player);
		}
	}
	
	private boolean isDouble(String s)
	{
		try{
			Double.parseDouble(s);
			return true;
		}
		catch (Exception e)
		{
		}
		try{
			Integer.parseInt(s);
			return true;
		}
		catch (Exception e){
			return false;
		}
	}
	
}
