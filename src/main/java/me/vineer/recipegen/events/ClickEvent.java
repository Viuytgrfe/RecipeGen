package me.vineer.recipegen.events;

import me.vineer.economyapi.money.Balance;
import me.vineer.economyapi.money.MoneyType;
import me.vineer.recipegen.Actions;
import me.vineer.recipegen.InventorySaver;
import me.vineer.recipegen.RecipeGen;
import org.bukkit.ChatColor;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class ClickEvent implements Listener {
    private final Plugin plugin = RecipeGen.getPlugin();
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if(!InventorySaver.hasInventory(event.getView().getTitle()))return;
        if(event.getClickedInventory() == null) return;
        if(event.getClickedInventory().getItem(event.getSlot()) == null)return;
        event.setCancelled(true);
        ClickType click = event.getClick();
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getClickedInventory();
        String inventoryName = event.getView().getTitle();
        Balance balance = Balance.getPlayerBalance(player.getName());
        ItemStack ClickedItem = InventorySaver.getItem(inventoryName, event.getSlot());
        int slot = event.getSlot();
        String action = InventorySaver.getAction(inventoryName, slot);
        if(action.equals(Actions.NULL_ACTION)) {
            event.setCancelled(true);
        } else if (action.equals(Actions.OPEN_INVENTORY)) {
            event.setCancelled(true);
            player.closeInventory();
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.openInventory(InventorySaver.updatePlaceholders(InventorySaver.getOpenInventoryName(inventoryName, slot), player));
                }
            }.runTaskLater(plugin, 1L);
        } else if (action.equals(Actions.BUY_ITEM)) {
            if(event.getClickedInventory().getType() == InventoryType.PLAYER)return;
            inventory = InventorySaver.updatePlaceholders(inventoryName, player);
            if(ClickedItem == null)return;
            event.setCancelled(true);
            Integer buyprice = (Integer) plugin.getConfig().get("Menu." + inventoryName + ".inventory." + slot + ".action.buyprice");
            Integer sellprice = (Integer) plugin.getConfig().get("Menu." + inventoryName + ".inventory." + slot + ".action.sellprice");
            String type = (String) plugin.getConfig().get("Menu." + inventoryName + ".inventory." + slot + ".action.moneytype");
            String Itemname = (String) plugin.getConfig().get("Menu." + inventoryName + ".inventory." + slot + ".action.name");
            if(click == ClickType.LEFT) {

                if(type.equals(MoneyType.MONEY.getName())) {
                    if(balance.getMoney() < buyprice) {
                        player.sendMessage(ChatColor.YELLOW + "[EA] " + ChatColor.RED + "У вас недостаточно средств для покупки!");
                        return;
                    }
                    balance.setMoney(balance.getMoney() - buyprice);
                } else {
                    if(balance.getDonateMoney() < buyprice) {
                        player.sendMessage(ChatColor.YELLOW + "[EA] " + ChatColor.RED + "У вас недостаточно средств для покупки!");
                        return;
                    }
                    balance.setDonateMoney(balance.getDonateMoney() - buyprice);
                }
                ItemStack item = InventorySaver.getItem(inventoryName, slot);
                if(player.getInventory().addItem(item).size() != 0 ) {
                    player.getWorld().dropItem(player.getLocation(), item);
                }
                inventory = InventorySaver.updatePlaceholders(inventoryName, player);
                event.getClickedInventory().setContents(inventory.getContents());

            } else if(click == ClickType.SHIFT_LEFT) {

                if(type.equals(MoneyType.MONEY.getName())) {
                    if(balance.getMoney() < buyprice * ClickedItem.getMaxStackSize()) {
                        player.sendMessage(ChatColor.YELLOW + "[EA] " + ChatColor.RED + "У вас недостаточно средств для покупки!");
                        return;
                    }
                    balance.setMoney(balance.getMoney() - buyprice * ClickedItem.getMaxStackSize());
                } else {
                    if(balance.getDonateMoney() < buyprice * ClickedItem.getMaxStackSize()) {
                        player.sendMessage(ChatColor.YELLOW + "[EA] " + ChatColor.RED + "У вас недостаточно средств для покупки!");
                        return;
                    }
                    balance.setDonateMoney(balance.getDonateMoney() - buyprice * ClickedItem.getMaxStackSize());
                }
                ItemStack item = InventorySaver.getItem(inventoryName, slot);

                for (int i = 0; i < ClickedItem.getMaxStackSize(); i++)
                {
                    if(player.getInventory().addItem(item).size() != 0 ) {
                        player.getWorld().dropItem(player.getLocation(), item);
                    }
                }
                inventory = InventorySaver.updatePlaceholders(inventoryName, player);
                event.getClickedInventory().setContents(inventory.getContents());

            } else if (click == ClickType.RIGHT) {
                if(type.equals(MoneyType.MONEY.getName())) {
                    if(calcAmount(FromShopItem(ClickedItem), event.getWhoClicked().getInventory()) > 0) {
                        event.getWhoClicked().getInventory().removeItem(FromShopItem(ClickedItem));
                        Balance.changePlayerBalance(player.getName(), sellprice, 0);
                    } else {
                        player.sendMessage(ChatColor.YELLOW + "[EA] " + ChatColor.RED + "У вас недостаточно ресурсов для продажи!");
                    }
                } else {
                    if(event.getWhoClicked().getInventory().contains(ClickedItem, 1)) {
                        event.getWhoClicked().getInventory().removeItem(ClickedItem);
                        Balance.changePlayerBalance(player.getName(), 0, sellprice);
                    } else {
                        player.sendMessage(ChatColor.YELLOW + "[EA] " + ChatColor.RED + "У вас недостаточно ресурсов для продажи!");
                    }
                }
                inventory = InventorySaver.updatePlaceholders(inventoryName, player);
                event.getClickedInventory().setContents(inventory.getContents());
            } else if (click == ClickType.SHIFT_RIGHT) {
                if(type.equals(MoneyType.MONEY.getName())) {
                    if(calcAmount(FromShopItem(ClickedItem), event.getWhoClicked().getInventory()) > ClickedItem.getMaxStackSize()-1) {
                        for (int i = 0; i < ClickedItem.getMaxStackSize(); i++) {
                            event.getWhoClicked().getInventory().removeItem(FromShopItem(ClickedItem));
                        }
                        Balance.changePlayerBalance(player.getName(), sellprice*ClickedItem.getMaxStackSize(), 0);
                    } else {
                        player.sendMessage(ChatColor.YELLOW + "[EA] " + ChatColor.RED + "У вас недостаточно ресурсов для продажи!");
                    }
                } else {
                    if(calcAmount(FromShopItem(ClickedItem), event.getWhoClicked().getInventory()) > ClickedItem.getMaxStackSize()-1) {
                        for (int i = 0; i < ClickedItem.getMaxStackSize(); i++) {
                            event.getWhoClicked().getInventory().removeItem(FromShopItem(ClickedItem));
                        }
                        Balance.changePlayerBalance(player.getName(), 0, sellprice*ClickedItem.getMaxStackSize());
                    } else {
                        player.sendMessage(ChatColor.YELLOW + "[EA] " + ChatColor.RED + "У вас недостаточно ресурсов для продажи!");
                    }
                }
                inventory = InventorySaver.updatePlaceholders(inventoryName, player);
                event.getClickedInventory().setContents(inventory.getContents());
            } else if (click == ClickType.MIDDLE) {
                int amount = calcAmount(FromShopItem(ClickedItem), event.getWhoClicked().getInventory());
                if(type.equals(MoneyType.MONEY.getName())) {
                    if(amount > 0) {
                        event.getWhoClicked().getInventory().setContents(removeItems(FromShopItem(ClickedItem), event.getWhoClicked().getInventory()).getContents());
                        Balance.changePlayerBalance(player.getName(), sellprice*amount, 0);
                    } else {
                        player.sendMessage(ChatColor.YELLOW + "[EA] " + ChatColor.RED + "У вас недостаточно ресурсов для продажи!");
                    }
                } else {
                    if(amount > 0) {
                        event.getWhoClicked().getInventory().setContents(removeItems(FromShopItem(ClickedItem), event.getWhoClicked().getInventory()).getContents());
                        Balance.changePlayerBalance(player.getName(), 0, sellprice*amount);
                    } else {
                        player.sendMessage(ChatColor.YELLOW + "[EA] " + ChatColor.RED + "У вас недостаточно ресурсов для продажи!");
                    }
                }
                inventory = InventorySaver.updatePlaceholders(inventoryName, player);
                event.getClickedInventory().setContents(inventory.getContents());
            }
        }
    }
    private static ItemStack FromShopItem(ItemStack item) {
        if((item.getItemMeta().getLore() != null && item.getItemMeta().getLore().get(item.getItemMeta().getLore().size()-1).startsWith(ChatColor.DARK_GRAY + ">> " + ChatColor.WHITE + "Ваш баланс: "))) {
            ItemMeta meta = item.getItemMeta();
            List<String> lore = meta.getLore();
            int loreSize = lore.size() - 1;
            for(int j = 0; j < 6; j++) {
                lore.remove(loreSize - j);
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private static boolean isTheSame(ItemStack first, ItemStack second) {
        ItemStack i1 = first.clone();
        ItemStack i2 = second.clone();
        i1.setAmount(1);
        i2.setAmount(1);
        return i1.equals(i2);
    }

    private static int calcAmount(ItemStack Item, Inventory inventory) {
        int amount = 0;
        for(ItemStack i : inventory) {
            if(i == null)continue;
            if(isTheSame(Item, i)) amount+=i.getAmount();
        }
        return amount;
    }

    private static Inventory removeItems(ItemStack item, Inventory inventory) {
        for (ItemStack i:inventory) {
            if(i != null && isTheSame(i, item)) {
                i.setAmount(0);
            }
        }
        return inventory;
    }
}
