package net.nanohard.dailyshop;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class ShopFactory {

	DailyShop plugin;
	private final DataStorage dataHandler;
	private final String PERMISSION_KEY = "dailyshop.shop.";
	private final int MAX_NAME_SIZE = 12;
	
	public ShopFactory(DailyShop plugin){
		this.plugin = plugin;
		this.dataHandler = new DataStorage(plugin);
		
		for (String shop: this.getAllShops()){
			Permission perm = new Permission(PERMISSION_KEY + shop);
			Bukkit.getPluginManager().addPermission(perm);
		}
	}
	
	public List<String> getAllShops(){
		return dataHandler.getAllShops();
	}
	
	public List<String> getAllowedShops(Player player){
		if (player.hasPermission("dailyshop.admin")) return this.getAllShops();
		List<String> result = new ArrayList<String>();
		for (String shop: this.getAllShops()){
			if (player.hasPermission(PERMISSION_KEY + shop)){
				result.add(shop);
			}
		}
		return result;
	}
	
	public boolean createShop(String name){
		if (this.getAllShops().contains(name)) return false;
		if (name.length() > MAX_NAME_SIZE) return false;
		dataHandler.createShop(name);
		Bukkit.getPluginManager().addPermission(new Permission(PERMISSION_KEY + name));
		return true;
	}
	
	public boolean removeShop(String name){
		if (!this.getAllShops().contains(name)) return false;
		dataHandler.removeShop(name);
		Bukkit.getPluginManager().removePermission(PERMISSION_KEY + name);
		return true;
	}
	
	public boolean openAdminInv(Player ent, String name){
		if (!this.getAllShops().contains(name)) return false;
		AdminGUI adminInv = new AdminGUI(plugin, name);
		adminInv.openInventory(ent);
		return true;
	}
	
	public void resetItems(String shopName, List<ShopItem> items){
		dataHandler.resetItems(shopName, items);
	}
	
	public boolean addItem(String shopName, ShopItem item){
		if (!this.getAllShops().contains(shopName)) return false;
		return dataHandler.addItem(shopName, item);
	}
	
	public ArrayList<ShopItem> getAllItems(String shopName){
		if (!this.getAllShops().contains(shopName)) return null;
		return this.dataHandler.getItems(shopName);
	}
	
	public ArrayList<ShopItem> getDailyItems(String shopName){
		ArrayList<ShopItem> fullList = this.getAllItems(shopName);
		if (fullList == null) return null;

		long SECONDS_PER_DAY = 1200000L;  // minecraft day == 1200 seconds == 20 minutes
		int seed = (int) (new Date().getTime()/SECONDS_PER_DAY);
		Random rand = new Random(seed);

		ArrayList<ShopItem> dailyList = new ArrayList<ShopItem>();
		int numberOfElements = 9;
		if (fullList.size() < numberOfElements) {
			numberOfElements = fullList.size();
		}

		for (int i = 0; i < numberOfElements; i++) {
			int randomIndex = rand.nextInt(fullList.size());
			ShopItem randomElement = fullList.get(randomIndex);
			dailyList.add(randomElement);
			fullList.remove(randomIndex);
		}

		return dailyList;
	}
	
	public int getMaxInvSize(){
		return dataHandler.MAX_SIZE;
	}
	public int getMaxNameSize(){
		return this.MAX_NAME_SIZE;
	}
	
}
