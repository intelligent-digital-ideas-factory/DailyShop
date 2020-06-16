package com.ericdebouwer.dailyshop;

import java.util.ArrayList;

import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.ImmutableMap;

public class ShopGUI implements Listener {
	
	DailyShop plugin;
	private final Inventory inv;
	private ArrayList<ShopItem> storedItems;
	private Player player;
	

	public ShopGUI(DailyShop plugin, String shopTitle, Player p){
		this.plugin = plugin;
		this.player = p;
		
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		
		storedItems = plugin.getShopFactory().getDailyItems(shopTitle);
		
		int itemCount = (storedItems.size() > 0) ? storedItems.size() : 9;
		int size = (int) Math.min(plugin.getShopFactory().getMaxInvSize(), Math.max(Math.ceil(itemCount / 9.0) * 9, 9));
		String title = ChatColor.AQUA + "" + ChatColor.BOLD + "DailyShop! " + ChatColor.RESET + "(" + shopTitle + ")";
		inv = Bukkit.createInventory(null, size, title);
		
		this.loadInventory();
		
	}
	
	public void loadInventory(){
		double bal = plugin.getEconomy().getBalance(this.player);
		for (int i=0; i<Math.min(plugin.getShopFactory().getMaxInvSize(), storedItems.size()); i++){
				ItemStack fake = storedItems.get(i).getFake(bal, plugin.getHelper().getCurreny());
				inv.setItem(i, fake);
		}
	}
	
	public void openInventory() {
        player.openInventory(inv);
    }
	
	@EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (e.getInventory() != inv) return;
        e.setCancelled(true);
        if (e.getClickedInventory() != inv) return;
        if (e.getCurrentItem() == null) return;
        ShopItem clickItem = storedItems.get(e.getRawSlot());
        double balance = plugin.getEconomy().getBalance(player);
        if (balance < clickItem.getPrice()){
        	plugin.getHelper().sendMessage(Message.NO_MONEY, player, ImmutableMap.of("currency", plugin.getHelper().getCurreny(), "money", Double.toString(balance)));
        	return;
        }
        EconomyResponse resp = plugin.getEconomy().withdrawPlayer(player, clickItem.getPrice());
        if(resp.transactionSuccess()) {
        	player.getInventory().addItem(clickItem.getItem());
        	plugin.getHelper().sendMessage(Message.ITEM_BOUGHT, player, ImmutableMap.of("currency", plugin.getHelper().getCurreny(), "money", Double.toString(resp.balance)));
        	this.loadInventory();
        }
        else {
        	plugin.getHelper().sendMessage(Message.ERROR, player, null);
        }
        
    }
	
	@EventHandler
    public void onInventoryDrag(final InventoryDragEvent e) {
        if (e.getInventory() == inv) {
          e.setCancelled(true);
        }
    }
}
