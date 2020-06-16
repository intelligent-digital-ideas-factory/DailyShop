package com.ericdebouwer.dailyshop;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class DailyShop extends JavaPlugin{
	
	Economy economy;
	String logPrefix;
	ShopFactory shopFactory;
	ConfigHelper config;
	
	static {
		ConfigurationSerialization.registerClass(ShopItem.class);
	}
	
	@Override
	public void onEnable(){
		this.logPrefix = "[" + this.getName() + "] ";
		this.config = new ConfigHelper(this);
		if (!config.isValid()){
			getServer().getConsoleSender().sendMessage(ChatColor.BOLD + "" +ChatColor.RED + this.logPrefix +"Invalid config.yml, plugin will disable to prevent crashing!");
 			getServer().getConsoleSender().sendMessage(ChatColor.BOLD + "" + ChatColor.RED + this.logPrefix + "See the header of the config.yml about fixing the problem.");
			return;
		}
		Bukkit.getConsoleSender().sendMessage(this.logPrefix + "Configuration loaded successfully!");
		this.shopFactory = new ShopFactory(this);
		
		if (!setupEconomy()) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + this.logPrefix + "Disabled due to no Vault dependency found!");
            		getServer().getPluginManager().disablePlugin(this);
            		return;
        	}
		new ShopCommands(this);
		new CommandCompleter(this);
	}
	
	private boolean setupEconomy() {
        	if (getServer().getPluginManager().getPlugin("Vault") == null) {
            		return false;
        	}
        	RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        	if (rsp == null) {
            		return false;
        	}
        	economy = rsp.getProvider();
        	return economy != null;
    	}
	
	public Economy getEconomy(){
		return economy;
	}
	
	public ConfigHelper getHelper(){
		return this.config;
	}
	
	public ShopFactory getShopFactory(){
		return shopFactory;
	}
}
