import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.NumberConversions;

public class ShopItem implements ConfigurationSerializable {
	
	private ItemStack item;
	private int price;
	private static final String ITEM_KEY = "item";
	private static final String PRICE_KEY = "price";

	public ShopItem(ItemStack item, int price){
		this.item = item;
		this.price = price;
	}
	
	public void setItem(ItemStack item){
		this.item = item;
	}
	public void setPrice(int price){
		this.price = price;
	}
	
	public ItemStack getFake(double balance, String currency){
		ItemStack itemCopy = new ItemStack(item);
		ItemMeta meta = itemCopy.getItemMeta();
		if (balance >= price){
			meta.setDisplayName(ChatColor.GREEN + currency + price);
		}
		else {
			meta.setDisplayName(ChatColor.RED + currency + price);
		}
		itemCopy.setItemMeta(meta);
		return itemCopy;
	}
	
	public int getPrice(){
		return this.price;
	}
	public ItemStack getItem(){
		return this.item;
	}
	
	@Override
    	public Map<String, Object> serialize() {
        	Map<String, Object> serialized = new HashMap<>();
        	serialized.put(ITEM_KEY, item);
        	serialized.put(PRICE_KEY, price);
        	return serialized;
   	}

    	public static ShopItem deserialize(Map<String, Object> deserialize) {
		ItemStack item = (ItemStack) deserialize.get(ITEM_KEY);
        	return new ShopItem(item, NumberConversions.toInt(deserialize.get(PRICE_KEY)));
    	}
}
