package com.ericdebouwer.dailyshop;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.google.common.collect.ImmutableMap;

enum Message {
	NO_MONEY, ITEM_BOUGHT, ERROR, SHOP_NOT_FOUND,
	NO_ALLOWED_SHOPS, NO_SHOP_PERMISSION, NO_ADMIN, USAGE_MESSAGE,
	NAME_TOO_LONG, CREATED, ALREADY_EXISTS, REMOVED, 
	NOT_EXIST, NO_PRICE, INVALID_PRICE, EMPTY_HAND,
	ADDED_ITEM, FULL_SHOP, ITEM_REMOVE_HINT;
	  
	@Override
	public String toString(){
		return this.name().toLowerCase().replace('_', '-');
	}
}

public class ConfigHelper {
	
	DailyShop plugin;
	boolean functional;
	private String pluginPrefix;
	private String curreny;
	private double itemToShopChance;
	
	private final String MESSAGE_KEY = "messages.";
	
	
	public ConfigHelper(DailyShop plugin){
		this.plugin = plugin;
		plugin.saveDefaultConfig();
		this.functional = this.validateSection("", "", true);
		
		if (functional){
			this.pluginPrefix = plugin.getConfig().getString("plugin-prefix");
			this.curreny = plugin.getConfig().getString("currency-symbol");
			this.itemToShopChance = plugin.getConfig().getDouble("item-to-shop-chance");
		}
	}
	
	public boolean isValid(){
		return this.functional;
	}
	
	public String getCurreny(){
		return curreny;
	}
	
	public double getItemChange(){
		return itemToShopChance;
	}
	
	public void sendMessage(Message message, Player player, ImmutableMap<String, String> replacements){
		String msg = plugin.getConfig().getString(MESSAGE_KEY + message.toString());
		if (msg == null || msg.isEmpty()) return;
		String colorMsg = ChatColor.translateAlternateColorCodes('§', this.pluginPrefix + msg);
		if (replacements != null){
			for (Map.Entry<String, String> entry: replacements.entrySet()){
				colorMsg = colorMsg.replaceAll("\\{" + entry.getKey() + "\\}", Matcher.quoteReplacement(entry.getValue()));
			}
		}
		player.sendMessage(colorMsg);		
	}

	private boolean validateSection(String template_path, String real_path, boolean deep){
		InputStream templateFile = getClass().getClassLoader().getResourceAsStream("config.yml");
        	FileConfiguration templateConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(templateFile));
        
        	ConfigurationSection real_section = plugin.getConfig().getConfigurationSection(real_path);
       		ConfigurationSection template_section = templateConfig.getConfigurationSection(template_path);
        
        	if (real_section == null || template_section == null) return false;
        
 		for(String key: template_section.getKeys(deep)){
 			if (!real_section.getKeys(deep).contains(key) || template_section.get(key).getClass() != real_section.get(key).getClass()){
 				Bukkit.getLogger().log(Level.WARNING, plugin.logPrefix + "Missing or invalid datatype key '" + key + "' and possibly others in config.yml");
 				return false;
 			}
 		}
 		return true;
	}
}
