package thevoxstudios.vtownyfly;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.palmergames.bukkit.towny.Towny;

public class TownyFly extends JavaPlugin implements Listener {
	
	
	public File configf;
	public YamlConfiguration config;
	Towny towny = null;
	public void onEnable() {
		towny = (Towny)getServer().getPluginManager().getPlugin("Towny");
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
	}
	public void onDisbale() {}
	
	
	public void createConfig() {
		if (configf.exists()) {
			config.set("Messages.Prefix", "&8[&3Towny&8]");
			config.set("Messages.FlyEnabled", "TownyFly is now enabled!");
			config.set("Messages.FlyDisabled", "TownyFly is now disabled!");
		}
	}
	
	
	public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("t fly"))
			if (s instanceof Player) {
				Player p = (Player)s;
				if (p.hasPermission("vox.towny.fly")) {
					
				}
			}
		return true;
	}
	
	

}
