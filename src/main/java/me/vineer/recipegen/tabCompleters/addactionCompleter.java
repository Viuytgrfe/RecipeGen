package me.vineer.recipegen.tabCompleters;

import me.vineer.economyapi.money.MoneyType;
import me.vineer.recipegen.RecipeGen;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class addactionCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        /*RecipeGen.getPlugin().getConfig().getConfigurationSection("Recipes").getKeys(false).forEach(key -> {
            List<String> recipes;
            if(RecipeGen.getPlugin().getConfig().isList("Recipes." + key)) {
                recipes = RecipeGen.getPlugin().getConfig().getStringList("Recipes." + key);
            }
        });*/
        if(args.length == 1) {
            List<String> stringList = new ArrayList<>();
            stringList.addAll(RecipeGen.getPlugin().getConfig().getConfigurationSection("Menu").getKeys(false));
            return stringList;
        } else if (args.length == 2) {
            List<String> stringList = new ArrayList<>();
            stringList.add("null_action");
            stringList.add("open_inventory");
            stringList.add("buy_item");
            return stringList;
        } else if (args.length == 3) {
            List<String> list = new ArrayList<>();
            list.add("X-slot");
            return list;
        } else if (args.length == 4) {
            List<String> list = new ArrayList<>();
            list.add("Y-slot");
            return list;
        } else if (args.length == 5 && args[1].equals("open_inventory")) {
            List<String> stringList = new ArrayList<>();
            stringList.addAll(RecipeGen.getPlugin().getConfig().getConfigurationSection("Menu").getKeys(false));
            return stringList;
        } else if (args.length == 5 && args[1].equals("buy_item")) {
            List<String> list = new ArrayList<>();
            list.add(MoneyType.MONEY.getName());
            list.add(MoneyType.DONATE_MONEY.getName());
            return list;
        } else if (args.length == 6 && args[1].equals("buy_item")) {
            List<String> list = new ArrayList<>();
            list.add("<buyprice>");
            return list;
        } else if (args.length == 7 && args[1].equals("buy_item")) {
            List<String> list = new ArrayList<>();
            list.add("<sellprice>");
            return list;
        }

        return null;
    }
}
