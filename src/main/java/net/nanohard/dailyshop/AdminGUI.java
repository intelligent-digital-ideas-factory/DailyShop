package net.nanohard.dailyshop;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class AdminGUI implements Listener{

	DailyShop plugin;
	private final Inventory inv;
	private final List<ShopItem> storedItems;
	private final String name;
	
	public AdminGUI(DailyShop plugin, String shopTitle){
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		
		this.name = shopTitle;
		inv = Bukkit.createInventory(null, plugin.getShopFactory().getMaxInvSize(), "Admin GUI: " + shopTitle);
		storedItems = plugin.getShopFactory().getAllItems(shopTitle);
		if (storedItems != null){
			for (int i=0; i<Math.min(plugin.getShopFactory().getMaxInvSize(), storedItems.size()); i++){
				ItemStack fake = storedItems.get(i).getFake(Double.MAX_VALUE, plugin.getHelper().getCurrency());
				inv.setItem(i, fake);
			}
		}
	}
	
	
	
	public void openInventory(final Player ent) {
		plugin.getHelper().sendMessage(Message.ITEM_REMOVE_HINT, ent, null);
       		ent.openInventory(inv);
    	}
	
	@EventHandler
	public void noDragging(InventoryDragEvent e){
		if (e.getInventory() == inv) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e){
		if (e.getInventory() == inv){
			e.setCancelled(true);
			if (e.getClickedInventory() == inv && e.getClick() == ClickType.DOUBLE_CLICK){
				if (e.getCurrentItem() == null) return;
				inv.setItem(e.getRawSlot(), null);
				storedItems.remove(e.getRawSlot());
				plugin.getShopFactory().resetItems(name, storedItems);
			}
		}
	}
}
