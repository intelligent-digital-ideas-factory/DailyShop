package net.nanohard.dailyshop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class CommandCompleter implements TabCompleter{

	DailyShop plugin;
	
	public CommandCompleter(DailyShop plugin){
		this.plugin = plugin;
		plugin.getCommand("dailyshop").setTabCompleter(this);
		plugin.getCommand("dsa").setTabCompleter(this);
		
	}
	
	public List<String> filter(Collection<String> original_list, String token){
		List<String> result = new ArrayList<String>();
		for (String s: original_list){
			if (s != null && s.startsWith(token)) {
				result.add(s);
			}
		}
		return result;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if (!(sender instanceof Player)) return null;
		if (command.getName().equalsIgnoreCase("dsa")){
			return this.handleAdmin(sender, Arrays.asList(args));
		}
		else if (command.getName().equalsIgnoreCase("dailyshop")){
			if (args.length > 0 && args[0].equalsIgnoreCase("admin")){
				return this.handleAdmin(sender, Arrays.asList(args).subList(1, args.length));
			}
			if (args.length == 1){
				return this.filter(plugin.getShopFactory().getAllowedShops((Player) sender), args[0]);
			}
			return Collections.emptyList();
		}
		return null;
	}
	
	public List<String> handleAdmin(CommandSender sender, List<String> args) {
		if (!sender.hasPermission("dailyshop.admin")) return Collections.emptyList();
		if (args.size() == 1){
			return this.filter(Arrays.asList("create", "view", "additem", "remove"), args.get(0));
		}
		else if (args.size() == 2 && Arrays.asList("view", "remove", "additem").contains(args.get(0))){
			return this.filter(plugin.getShopFactory().getAllShops(), args.get(1));
		}
		else if (args.size() == 3 && args.get(0).equalsIgnoreCase("additem")){
			return this.filter(Arrays.asList("1", "10", "100", "1000"), args.get(2));
		}
		return Collections.emptyList();
	}

}
