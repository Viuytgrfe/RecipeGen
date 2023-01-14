package me.vineer.recipegen.tabCompleters;

import me.vineer.recipegen.RecipeGen;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class addItemCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if(args.length == 1) {
            List<String> stringList = new ArrayList<>();
            stringList.addAll(RecipeGen.getPlugin().getConfig().getConfigurationSection("Menu").getKeys(false));
            return stringList;
        } else if (args.length == 2) {
            List<String> list = new ArrayList<>();
            list.add("X-slot");
            return list;
        } else if (args.length == 3) {
            List<String> list = new ArrayList<>();
            list.add("Y-slot");
            return list;
        }
        return null;
    }
}
