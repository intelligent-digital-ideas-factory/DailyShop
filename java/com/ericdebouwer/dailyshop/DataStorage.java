package com.ericdebouwer.dailyshop;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class DataStorage {
	
	DailyShop plugin;
	File shopFile;
	FileConfiguration shopsConfig;
	final int MAX_SIZE = 54;

	
	public DataStorage(DailyShop plugin){
		this.plugin = plugin;
		this.createConfig();
	}
	
	public List<String> getAllShops(){
		return new ArrayList<String>(this.shopsConfig.getKeys(false));
	}
	
	public void createShop(String name){
		shopsConfig.createSection(name);
		this.saveConfig();
	}
	
	public void removeShop(String name){
		shopsConfig.set(name, null);
		this.saveConfig();
	}
	
	public void resetItems(String shopName, List<ShopItem> items){
		shopsConfig.set(shopName, items);
		this.saveConfig();
	}
	
	
	public boolean addItem(String shopName, ShopItem item){
		ArrayList<ShopItem> previous = this.getItems(shopName);
		if (previous == null) {
			shopsConfig.set(shopName, Arrays.asList(item));
		}
		else {
			if (previous.size() >= MAX_SIZE) return false;
			previous.add(item);
			shopsConfig.set(shopName, previous);
		}
		this.saveConfig();
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<ShopItem> getItems(String shopName){
		List<?> items = shopsConfig.getList(shopName);
		if (items != null)  return new ArrayList<ShopItem>((List<ShopItem>) items);
		return null;
	}
	
	 private void createConfig() {
        shopFile = new File(plugin.getDataFolder(), "shops.yml");
        if (!shopFile.exists()) {
            shopFile.getParentFile().mkdirs();
            plugin.saveResource("shops.yml", false);
         }

        shopsConfig = new YamlConfiguration();
        try {
            shopsConfig.load(shopFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
	}
	 
	 public void saveConfig(){
		try{
			this.shopsConfig.save(shopFile);
		} catch (IOException e){
			e.printStackTrace();
		}
	}
}
