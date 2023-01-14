package me.vineer.recipegen;

import me.vineer.recipegen.commands.RecipeMenuCommand;
import me.vineer.recipegen.commands.ShopCommand;
import me.vineer.recipegen.events.ClickEvent;
import me.vineer.recipegen.tabCompleters.ShopTabCompleter;
import me.vineer.recipegen.tabCompleters.addItemCompleter;
import me.vineer.recipegen.tabCompleters.addactionCompleter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class RecipeGen extends JavaPlugin implements Listener {
    public static Plugin plugin;
    @Override
    public void onEnable() {
        plugin = this;
        getConfig().options().copyDefaults(true);
        saveConfig();
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new ClickEvent(), this);
        getCommand("recipemenu").setExecutor(new RecipeMenuCommand());
        getCommand("addaction").setExecutor(new RecipeMenuCommand());
        getCommand("createmenu").setExecutor(new RecipeMenuCommand());
        getCommand("additem").setExecutor(new RecipeMenuCommand());
        getCommand("addaction").setTabCompleter(new addactionCompleter());
        getCommand("additem").setTabCompleter(new addItemCompleter());
        getCommand("shop").setExecutor(new ShopCommand());
        getCommand("shop").setTabCompleter(new ShopTabCompleter());
    }

    public static Plugin getPlugin() {
        return plugin;
    }
}
