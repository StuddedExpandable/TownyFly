package thevoxstudios.vtownyfly;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.earth2me.essentials.IEssentials;
import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.event.PlayerChangePlotEvent;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownyUniverse;

import net.md_5.bungee.api.ChatColor;

public class TownyFly extends JavaPlugin implements Listener {
	
	
	public File configf;
	public FileConfiguration config;
	IEssentials ess;
	Towny towny;
	Resident res;
	ArrayList<String> tflyp = new ArrayList<String>();
	
	public void onEnable() {
		createConfig();
		towny = (Towny)getServer().getPluginManager().getPlugin("Towny");
		ess = (IEssentials)getServer().getPluginManager().getPlugin("Essentials");
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
		Bukkit.getLogger().info("[TownyFly] Is now enabled!");
	}
	public void onDisable() {
		Bukkit.getLogger().info("[TownyFly] Is now disabled......bye!");
	}
	
	
	void createConfig() {
	    configf = new File(getDataFolder() + File.separator + "config.yml");
		config = YamlConfiguration.loadConfiguration(configf);
		
		boolean save = false;
		if (!configf.exists()) {
			config.set("Messages.Prefix", "&8[&3Towny&8]");
			config.set("Messages.FlyEnabled", "&aTownyFly is now enabled!");
			config.set("Messages.FlyDisabled", "&aTownyFly is now disabled!");
			config.set("Messages.NotInATown", "&cYou must be in your joined Town in order to use this command!");
			config.set("Messages.NotInJoinedTown", "&cYou are not in your joined Town! If you want to use this command you must be in the town you have joined.");
			config.set("Messages.NoPermission", "&cYou do not have permission to execute this command! &cWant to be able to execute this command? Become a registered &8[&aMember&]&a @ &dwww.thevoxmc.net &aand gain perks like this one!");
		    config.set("Messages.OutOfTownBoundaries", "&aTeleported to the ground and changed your Towny Fly mode to off. You have just flown outside of the town boundaries! You can only fly inside of the town with /tfly, nub ;p! ");
			save = true;
		}
		if (save) {
			try {
				config.save(configf);
				Bukkit.getLogger().info("[TownyFly] Creating config.yml....");
				Bukkit.getLogger().info("[TownyFly] Created config.yml!");
			} catch (IOException e) {
				Bukkit.getLogger().info("[TownyFly] Failed to create/save config.yml!");
				Bukkit.getLogger().info("[TownyFly] Caused by: " + e.getMessage());
			}
		}	
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		if (tflyp.contains(p.getName())) {
			p.setFlying(false);
			tflyp.remove(p.getName());
		}
	}
	@EventHandler
	public void onPlotLeave(PlayerChangePlotEvent e) throws NotRegisteredException {
		System.out.println("onPlotLeave Event Fires");
		Player p = e.getPlayer();
		TownBlock tb = e.getTo().getTownBlock();
		if (tflyp.contains(p.getName())) {
	        try {
				res = TownyUniverse.getDataSource().getResident(p.getName());
			} catch (NotRegisteredException e1) {
				e1.printStackTrace();
			}
			try {
				@SuppressWarnings("unused")
				tb = e.getTo();http://prntscr.com/b1brkn
			} catch (NotRegisteredException tbe) {
				System.out.println("Resident has entered the wilderness.");
				p.teleport(p.getPlayer().getWorld().getHighestBlockAt(p.getPlayer().getLocation().getBlockX(), p.getPlayer().getLocation().getBlockZ()).getLocation());
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Messages.OutOfTownBoundaries")));
				p.setFlying(false);
				tflyp.remove(p.getName());
				return;
			}			
			Town townTo = tb.getTown();
			if (res.getTown() != townTo) {
					System.out.println("Resident has left their town.");
				p.teleport(p.getPlayer().getWorld().getHighestBlockAt(p.getPlayer().getLocation().getBlockX(), p.getPlayer().getLocation().getBlockZ()).getLocation());
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Messages.OutOfTownBoundaries")));
				p.setFlying(false);
				tflyp.remove(p.getName());
				return;
			} 
		}
	}
	public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("tfly"))
			if (s instanceof Player) {
				Player p = (Player)s;
				if (p.hasPermission("vox.towny.fly")) {
				try {
					res = TownyUniverse.getDataSource().getResident(p.getName());
				} catch (NotRegisteredException e) {
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', "A problem occoured when the server attempted to gather your location data! Please notifiy the System Administrator(s) of this server in order to resolve this problem!"));
					Bukkit.getLogger().info("Couldn't get " + p.getName() + "'s location!");
					Bukkit.getLogger().info("Caused by: " + e.getMessage());
				}
					if (res.hasTown()) {
						if (TownyUniverse.isWilderness((Block) p.getLocation().getBlock())) {
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Messages.NotInATown")));
						} else {
							try {
								if (res.getTown() != null) {
									if (p.getAllowFlight()) {
										tflyp.remove(p.getName());
										p.setFlying(false);
										p.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Messages.FlyDisabled")));
									} else {
										tflyp.add(p.getName());
										p.setAllowFlight(true);
										p.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Messages.FlyEnabled")));
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
		         	}
	           } else {
	        	   p.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Messages.NoPermission")));
	           }
	
	     } else {
	    	 Bukkit.getLogger().info("Only players can do this command, silly Admin!!!");
	     }
		return true;
	}
}