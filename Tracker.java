package com.gmail.rieckaaron.Tracker;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class Tracker extends JavaPlugin implements Listener {
	
	HashSet<String> players = new HashSet<String>();
	HashMap<String, String> tracked = new HashMap<String, String>();
	
	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		
		BukkitScheduler scheduler = getServer().getScheduler();
		scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
			
			@Override	
			public void run() {
				if (tracked.size() > 0) {	
					for (Map.Entry<String, String> i : tracked.entrySet()) {
						Player player = Bukkit.getServer().getPlayer(i.getKey());
						Location loc = Bukkit.getServer().getPlayer(i.getValue()).getLocation();
						player.setCompassTarget(loc);
					}
				}
			}
		}, 0L, 5L);
	}
	
	@Override
	public void onDisable() {
		players.clear();
		tracked.clear();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equals("track")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("This command can only be run by a player!");
			}
			
			else {
				Player player = (Player) sender;
				
				if (args.length != 1) {
					return false;
				}
				
				if (!players.contains(args[0])) {
					sender.sendMessage(args[0] + " is not an online player!");
					return true;
				}
				
				if (tracked.containsKey(player.getName())) {
					tracked.remove(player.getName());
				}

				tracked.put(player.getName(), args[0]);
				return true;	
			}
		}
		
		else if (cmd.getName().equalsIgnoreCase("clearTracker")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("This command can only be run by a player!");
				return true;
			}
			
			else {
				if (args.length != 0) {
					return false;
				}
				
				Player player = (Player) sender;
				Location spawn = player.getBedSpawnLocation();
				
				if (tracked.containsKey(player.getName())) {
					tracked.remove(player.getName());
				}
				
				if (spawn != null) {
					player.setCompassTarget(player.getBedSpawnLocation());
				}
				
				else {
					player.setCompassTarget(player.getWorld().getSpawnLocation());
				}
				
				return true;
			}
		}
		
		else if (cmd.getName().equalsIgnoreCase("getCompass")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("This command can only be run by a player!");
				return true;
			}
			
			else {
				if (args.length != 0) {
					return false;
				}
				
				ItemStack compass = new ItemStack(Material.COMPASS);
				Player player = (Player) sender;
				player.getInventory().addItem(compass);
				
				return true;
			}
		}
		return false;
	}
	
	@EventHandler
	public void onLogin(PlayerLoginEvent event) {
		players.add(event.getPlayer().getName());
	}
	
	@EventHandler
	public void onLogout(PlayerQuitEvent event) {
		players.remove(event.getPlayer().getName());
		if (tracked.containsKey(event.getPlayer().getName())) {
			tracked.remove(event.getPlayer().getName());
		}
		
	}
}
