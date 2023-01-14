package me.vineer.recipegen.commands;

import me.vineer.recipegen.InventorySaver;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ShopCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1) {
            Player player = (Player) sender;
            if(args[0].equals("hunter")) {
                player.openInventory(InventorySaver.updatePlaceholders("Охотник", player));
            } else if (args[0].equals("miner")) {
                player.openInventory(InventorySaver.updatePlaceholders("Шахтёр", player));
            }
        }
        return true;
    }
}
