import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

public class ShopFactory {

	DailyShop plugin;
	private DataStorage dataHandler;
	
	private final long SECONDS_PER_DAY = 86400000L;
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
		int seed = (int) (new Date().getTime()/SECONDS_PER_DAY);
		Random rand = new Random(seed);
		ArrayList<ShopItem> result = new ArrayList<ShopItem>();
		if (this.getAllItems(shopName) == null) return result; 
		for (ShopItem item: this.getAllItems(shopName)){
			if (rand.nextDouble() < plugin.getHelper().getItemChange()){
				result.add(item);
			}
		}
		return result;
	}
	
	public int getMaxInvSize(){
		return dataHandler.MAX_SIZE;
	}
	public int getMaxNameSize(){
		return this.MAX_NAME_SIZE;
	}
	
}
