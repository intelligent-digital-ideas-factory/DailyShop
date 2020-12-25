package net.nanohard.dailyshop;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.ImmutableMap;

public class ShopCommands implements CommandExecutor{
	
	DailyShop plugin;
	
	public ShopCommands(DailyShop plugin){
		this.plugin = plugin;
		plugin.getCommand("dailyshop").setExecutor(this);
		plugin.getCommand("dsa").setExecutor(this);
	}

	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args) {
		if (!(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED + "This command can only be run by a player!");
			return true;
		}
		Player player = (Player) sender;
		if (command.getName().equalsIgnoreCase("dailyshop")){
			if (args.length > 0 && args[0].equalsIgnoreCase("admin")){
				this.handleAdmin("/" + label +" admin", player, Arrays.asList(args).subList(1, args.length));
			}
			else if (args.length > 0){
				if (!plugin.getShopFactory().getAllShops().contains(args[0])){
					plugin.getHelper().sendMessage(Message.SHOP_NOT_FOUND, player, null);
				}
				else if (!plugin.getShopFactory().getAllowedShops(player).contains(args[0])){
					plugin.getHelper().sendMessage(Message.NO_SHOP_PERMISSION, player, null);
				}
				else{
					ShopGUI newShop = new ShopGUI(plugin, args[0], player);
					newShop.openInventory();
				}
			}
			else {
				List<String> allowedShops = plugin.getShopFactory().getAllowedShops(player);
				if (!allowedShops.isEmpty()){
					String shop = allowedShops.get(ThreadLocalRandom.current().nextInt(allowedShops.size()));
					ShopGUI newShop = new ShopGUI(plugin, shop, player);
					newShop.openInventory();
				}
				else {
					plugin.getHelper().sendMessage(Message.NO_ALLOWED_SHOPS, player, null);
				}
			}
			return true;
		}
		else if (command.getName().equalsIgnoreCase("dsa")){
			this.handleAdmin("/dsa", player, Arrays.asList(args));
			return true;		
		}
		return false;
	}
	
	public void handleAdmin(String inputCommand, Player player, List<String> args){
		if (!player.hasPermission("dailyshop.admin")){
			plugin.getHelper().sendMessage(Message.NO_ADMIN, player, null);
		}
		else if (args.size() < 2){
			plugin.getHelper().sendMessage(Message.USAGE_MESSAGE, player, ImmutableMap.of("input", inputCommand));
		}
		else if (args.get(0).equalsIgnoreCase("create")){
			if (args.get(1).length() > plugin.getShopFactory().getMaxNameSize()){
				plugin.getHelper().sendMessage(Message.NAME_TOO_LONG, player, null);
			}
			else if (plugin.getShopFactory().createShop(args.get(1))){
				plugin.getHelper().sendMessage(Message.CREATED, player, ImmutableMap.of("shop_name", args.get(1)));
			}
			else {
				plugin.getHelper().sendMessage(Message.ALREADY_EXISTS, player, ImmutableMap.of("shop_name", args.get(1)));
			}
		}
		else if (args.get(0).equalsIgnoreCase("remove")){
			if (plugin.getShopFactory().removeShop(args.get(1))){
				plugin.getHelper().sendMessage(Message.REMOVED, player, ImmutableMap.of("shop_name", args.get(1)));
			}
			else {
				plugin.getHelper().sendMessage(Message.NOT_EXIST, player, ImmutableMap.of("shop_name", args.get(1)));
			}
		}
		else if (args.get(0).equalsIgnoreCase("view")){
			if (!plugin.getShopFactory().openAdminInv(player, args.get(1))){
				plugin.getHelper().sendMessage(Message.NOT_EXIST, player, ImmutableMap.of("shop_name", args.get(1)));
			}
		}
		else if (args.get(0).equalsIgnoreCase("additem")){
			if (args.size() < 3){
				plugin.getHelper().sendMessage(Message.NO_PRICE, player, null);
			}
			else if (!plugin.getShopFactory().getAllShops().contains(args.get(1))){
				plugin.getHelper().sendMessage(Message.NOT_EXIST, player, ImmutableMap.of("shop_name", args.get(1)));
			}
			else if (player.getInventory().getItemInMainHand() == null || player.getInventory().getItemInMainHand().getType().isAir()){
				plugin.getHelper().sendMessage(Message.EMPTY_HAND, player, null);
			}
			else {
				try{
					int price = Integer.parseInt(args.get(2));
					if (price < 0) throw new NumberFormatException();
					ItemStack handCopy = new ItemStack(player.getInventory().getItemInMainHand());
					ShopItem newItem = new ShopItem(handCopy, price);
					if (plugin.getShopFactory().addItem(args.get(1), newItem)){
						plugin.getHelper().sendMessage(Message.ADDED_ITEM, player, ImmutableMap.of("shop_name", args.get(1), "currency", plugin.getHelper().getCurreny(), "price", args.get(2)));
					}
					else {
						plugin.getHelper().sendMessage(Message.FULL_SHOP, player, null);
					}
					
				}catch (NumberFormatException e){
					plugin.getHelper().sendMessage(Message.INVALID_PRICE, player, null);
				}
			}
			
		}
		else {
			plugin.getHelper().sendMessage(Message.USAGE_MESSAGE, player, ImmutableMap.of("input", inputCommand));
		}
	}
	

}
