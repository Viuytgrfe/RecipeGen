package me.vineer.recipegen.commands;

import me.vineer.economyapi.money.MoneyType;
import me.vineer.recipegen.Actions;
import me.vineer.recipegen.InventorySaver;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class RecipeMenuCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        if(!player.isOp()) return true;
        if(command.getName().equalsIgnoreCase("recipemenu")) {
            player.openInventory(InventorySaver.updatePlaceholders("Recipes", player));
        } else if(command.getName().equalsIgnoreCase("addaction")) {
            if(args.length >= 4) {
                // args[0] - Название меню в котором добавляешь действие
                // args[1] - действие
                if(args[1].equals(Actions.OPEN_INVENTORY)) {
                    // args[2] - координата слота X
                    // args[3] - координата слота Y
                    // args[4] - меню которое откроется
                    int X = Integer.parseInt(args[2]);
                    int Y = Integer.parseInt(args[3]);
                    int slot = ((Y-1)*9)+X-1;
                    if(slot < 0 || slot > 53) {
                        player.sendMessage(ChatColor.RED + "координаты указаны неверно!");
                        return true;
                    }
                    InventorySaver.setOpenInventory(args[0], slot, args[4]);
                } else if(args[1].equals(Actions.NULL_ACTION)) {
                    // args[2] - координата слота X
                    // args[3] - координата слота Y
                    int X = Integer.parseInt(args[2]);
                    int Y = Integer.parseInt(args[3]);
                    int slot = ((Y-1)*9)+X-1;
                    if(slot < 0 || slot > 53) {
                        player.sendMessage(ChatColor.RED + "координаты указаны неверно!");
                        return true;
                    }
                    InventorySaver.setAction(Actions.NULL_ACTION, args[0], slot);
                } else if (args[1].equals(Actions.BUY_ITEM)) {
                    // args[2] - координата слота X
                    // args[3] - координата слота Y
                    // args[4] - тип валюты
                    // args[5] - цена покупки
                    // args[6] - цена продажи
                    // args[7] - имя предмета
                    int X = Integer.parseInt(args[2]);
                    int Y = Integer.parseInt(args[3]);
                    int buyprice = Integer.parseInt(args[5]);
                    int sellprice = Integer.parseInt(args[6]);
                    int slot = ((Y-1)*9)+X-1;
                    if(slot < 0 || slot > 53) {
                        player.sendMessage(ChatColor.RED + "координаты указаны неверно!");
                        return true;
                    }
                    InventorySaver.setBuyingItem(args[0], slot, buyprice, sellprice, MoneyType.getEnum(args[4]));
                }
            }
        } else if(command.getName().equalsIgnoreCase("createmenu")) {
            if(args.length == 1) {
                // args[0] - название меню
                args[0].replace("_", " ");
                Inventory inv = Bukkit.createInventory(null, 54, args[0]);
                InventorySaver.setInventory(inv, args[0]);
            }
        } else if (command.getName().equalsIgnoreCase("additem")) {
            if(args.length == 3) {
                args[0].replace("_", " ");
                // args[0] - название меню, в которое добавить предмет
                // args[1] - координата X
                // args[2] - координата Y
                int X = Integer.parseInt(args[1]);
                int Y = Integer.parseInt(args[2]);
                int slot = ((Y-1)*9)+X-1;
                if(slot < 0 || slot > 53){
                    player.sendMessage(ChatColor.RED + "координаты указаны неверно!");
                    return true;
                }
                Inventory inventory = InventorySaver.getInventory(args[0], player);
                inventory.setItem(slot, player.getInventory().getItemInMainHand());
                InventorySaver.setInventory(inventory, args[0]);
            }
        }
        return true;
    }
}
