package thevoxstudios.vtownyfly;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.event.PlayerChangePlotEvent;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownyUniverse;

public class TownyFly extends JavaPlugin implements Listener 
{
	
	
	File configf = null;
	FileConfiguration config = null;
	Towny towny = null;
	Resident res = null;
	ArrayList<String> tflyp = new ArrayList<String>();
	ArrayList<String> nofall = new ArrayList<String>();
	
	@Override
	public void onEnable() 
	{
		createConfig();
		towny = (Towny)getServer().getPluginManager().getPlugin("Towny");
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
		log("Is now enabled!");
	}
	
	@Override
	public void onDisable() 
	{
		log("Is now disabled......bye!");
	}
	
	public void log(String string)
	{
		System.out.println("[TownyFly] " + string);
	}
	
	void createConfig() {
	    configf = new File(getDataFolder() + File.separator + "config.yml");
		config = YamlConfiguration.loadConfiguration(configf);
		
		boolean save = false;
		if (!configf.exists()) 
		{
			config.set("Messages.Prefix", "&aTownyFly &8»");
			config.set("Messages.FlyEnabled", "<prefix> &aTownyFly is now enabled!");
			config.set("Messages.FlyDisabled", "<prefix> &aTownyFly is now disabled!");
			config.set("Messages.NotJoinedTown", "<prefix> &cYou do not belong to this town, so you cannot fly in it!");
			config.set("Messages.NotInATown", "<prefix> &cYou must be in your joined Town in order to use this command!");
			config.set("Messages.NotInJoinedTown", "<prefix> &cYou are not in your joined Town! If you want to use this command you must be in the town you have joined.");
			config.set("Messages.NoPermission", "<prefix> &cYou do not have permission to execute this command! &cWant to be able to execute this command? Become a registered &8[&aMember&]&a @ &dwww.thevoxmc.net &aand gain perks like this one!");
		    config.set("Messages.OutOfTownBoundaries", "<prefix> &aTeleported to the ground and changed your Towny Fly mode to off. You have just flown outside of the town boundaries! You can only fly inside of the town with /tfly, nub ;p! ");
			save = true;
		}
		if (save) 
		{
			try 
			{
				config.save(configf);
				log("Creating config.yml....");
				log("Created config.yml!");
			} 
			catch (IOException e) 
			{
				log("Failed to create/save config.yml!");
				log("Caused by: " + e.getMessage());
			}
		}	
	}
	

	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent e)
	{
		Player p = (Player) e.getPlayer();
		if (tflyp.contains(p.getName()))
		{
			if (!p.getAllowFlight())
			{
				p.setAllowFlight(true);
				p.setFlying(true);
			}
			e.setCancelled(true);
			log("tflyp contains " + tflyp);
			return;
		}
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e)
	{
		Player p = (Player) e.getPlayer();
		if (tflyp.contains(p.getName()));
		{
			if (!p.getAllowFlight())
			{
				p.setAllowFlight(true);
				p.setFlying(true);
			}
			log("tflyp contains " + tflyp + " x2");
			return;
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e)
	{
		Player p = (Player) e.getViewers();
		if (tflyp.contains(p.getName()));
		{
			if (!p.getAllowFlight())
			{
				p.setAllowFlight(true);
				p.setFlying(true);
			}
			log("tflyp contains " + tflyp + " x2");
			return;
		}
	}
	
	
	@EventHandler
	public void onDamage(EntityDamageEvent e)
	{
		log("onDamage fired.");
		if (e.getEntity() instanceof Player)
		{
			Player p = (Player) e.getEntity();
			if (nofall.contains(p.getName()))
			{
				if (e.getCause() == DamageCause.FALL && (p.hasPermission("vox.towny.fly")))
				{
					e.setCancelled(true);
					nofall.remove(p.getName());
					log("Passed conditions sucessfully, removed from nofall array");
				}
				else
				{
					log("Damage not fall damage/Conditions not met");
				}
				if (p.getLastDamageCause().getCause() == DamageCause.FALL && (p.hasPermission("vox.towny.fly")))
				{
					e.setCancelled(true);
					nofall.remove(p.getName());
					log("Passed conditions sucessfully, removed from nofall array");
				}		
				else
				{
					log("Damage not fall damage/Conditions not met x2");
				}
			}
		}
	}
	
	@EventHandler
	public void onPlotLeave(PlayerChangePlotEvent e) throws NotRegisteredException 
	{
		Player p = e.getPlayer();

		if (tflyp.contains(p.getName())) 
		{
	        try 
	        {
				res = TownyUniverse.getDataSource().getResident(p.getName());
			} 
	        catch (NotRegisteredException e1) 
	        {
				e1.printStackTrace();
			}
			try 
			{
				TownBlock tb = e.getTo().getTownBlock();
				if (tb != null) 
				{
					Town townTo = tb.getTown();
					if (res.getTown() != townTo) 
					{
							log("Resident "+ p + " has left their town.");
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Messages.OutOfTownBoundaries").replace("<prefix>", getConfig().getString("Messages.Prefix"))));
						p.setAllowFlight(false);
						p.setFlying(false);
						tflyp.remove(p.getName());
						if (Material.AIR == p.getLocation().subtract(0, 1, 0).getBlock().getType())
						{
							nofall.add(p.getName());
							log(p.getName() + " has been added to the nofall arraylist");
						}
						return;
					}
				}
			} 
			catch (NotRegisteredException tbe) 
			{
				log("Resident has entered the wilderness.");
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Messages.OutOfTownBoundaries").replace("<prefix>", getConfig().getString("Messages.Prefix"))));
				p.setAllowFlight(false);
				p.setFlying(false);
				tflyp.remove(p.getName());
				if (Material.AIR == p.getLocation().subtract(0, 1, 0).getBlock().getType())
				{
					nofall.add(p.getName());
					log(p.getName() + " has been added to the nofall arraylist");
				}
				return;
			}
		}
	}
	
	public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) 
	{
		if (cmd.getName().equalsIgnoreCase("tfly"))
			if (s instanceof Player) {
				Player p = (Player)s;
				if (p.hasPermission("vox.towny.fly")) 
				{
				try 
				{
					res = TownyUniverse.getDataSource().getResident(p.getName());
				} 
				catch (NotRegisteredException e) 
				{
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', "A problem occoured when the server attempted to gather your location data! Please notifiy the System Administrator(s) of this server in order to resolve this problem!"));
					log("Couldn't get " + p.getName() + "'s location!");
					log("Caused by: " + e.getMessage());
				}
					if (res.hasTown()) 
					{
						if (TownyUniverse.isWilderness((Block) p.getLocation().getBlock())) 
						{
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Messages.NotInATown").replace("<prefix>", getConfig().getString("Messages.Prefix"))));
						} 
						else 
						{
							try 
							{
								if (TownyUniverse.getTownBlock(p.getLocation()).getTown().equals(res.getTown()))
								{
									if (p.getAllowFlight()) 
									{
										tflyp.remove(p.getName());
										p.setAllowFlight(false);
										p.setFlying(false);
										p.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Messages.FlyDisabled").replace("<prefix>", getConfig().getString("Messages.Prefix"))));
										if (Material.AIR == p.getLocation().subtract(0, 1, 0).getBlock().getType())
										{
											nofall.add(p.getName());
											log(p.getName() + " has been added to the nofall arraylist");
										}
									} 
									else 
									{
										tflyp.add(p.getName());
										p.setAllowFlight(true);
										p.setFlying(true);
										p.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Messages.FlyEnabled").replace("<prefix>", getConfig().getString("Messages.Prefix"))));
									}
								}
								else
								{
									p.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Messages.NotJoinedTown")));
									log("User attempted to toggle /tfly outside of their town");
								}
							} 
							catch (Exception e) 
							{
								e.printStackTrace();
							}
						}
		         	}
	           }
				else 
				{
	        	   p.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Messages.NoPermission").replace("<prefix>", getConfig().getString("Messages.Prefix"))));
	           }
	     } 
			else 
			{
	    	 log("Only players can do this command, silly Admin!!!");
	     }
		return true;
	}
}