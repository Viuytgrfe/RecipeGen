package me.vineer.recipegen;

import me.clip.placeholderapi.PlaceholderAPI;
import me.vineer.economyapi.EconomyExpansion;
import me.vineer.economyapi.money.Balance;
import me.vineer.economyapi.money.MoneyType;
import net.md_5.bungee.api.chat.hover.content.Item;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InventorySaver {
    private static Plugin plugin = RecipeGen.getPlugin();
    public static void setInventory(Inventory inventory, String name) {
        for(int i = 0; i < 54; i++) {
            if(inventory.getItem(i) == null) {
                plugin.getConfig().set("Menu." + name + ".inventory." + i + ".item", "null");
            } else {
                plugin.getConfig().set("Menu." + name + ".inventory." + i + ".item", inventory.getItem(i));
            }
        }
        plugin.saveConfig();
    }

    public static Inventory getInventory(String name, Player player) {
        String client_name = name;
        client_name = client_name.replaceAll("_", " ");
        Inventory inventory = Bukkit.createInventory(null, 54, client_name);
        for(int i = 0; i < 54; i++) {
            try {
                if(plugin.getConfig().get("Menu." + name + ".inventory." + i + ".item").equals("null") || plugin.getConfig().get("Menu." + name + ".inventory." + i + ".item") == null)continue;
                ItemStack item = (ItemStack) plugin.getConfig().get("Menu." + name + ".inventory." + i + ".item");
                if(getAction(name, i).equals(Actions.BUY_ITEM) && item != null) {
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
                    Integer buyprice = (Integer) plugin.getConfig().get("Menu." + name + ".inventory." + i + ".action.buyprice");
                    Integer sellprice = (Integer) plugin.getConfig().get("Menu." + name + ".inventory." + i + ".action.sellprice");
                    String type = (String) plugin.getConfig().get("Menu." + name + ".inventory." + i + ".action.moneytype");
                    String money;
                    String amount;
                    if (type.equals(MoneyType.MONEY.getName())) {
                        money = "$";
                        amount = "%economy_balance%";

                    } else {
                        money = "℗";
                        amount = "%economy_donate_balance%";
                    }
                    ItemMeta meta = item.getItemMeta();
                    List<String> lore = new ArrayList<>();
                    if (meta.hasLore()) {
                        lore = meta.getLore();
                    }
                    lore.add("");
                    lore.add(ChatColor.GREEN + "ЛКМ " + ChatColor.GRAY + "- Купить за " + ChatColor.YELLOW + buyprice + money + ChatColor.GRAY + " / " + ChatColor.YELLOW + (buyprice * item.getMaxStackSize()) + money);
                    lore.add(ChatColor.RED + "ПКМ " + ChatColor.GRAY + "- Продать за " + ChatColor.YELLOW + sellprice + money + ChatColor.GRAY + " / " + ChatColor.YELLOW + (sellprice * item.getMaxStackSize()) + money);
                    lore.add(ChatColor.DARK_GRAY + "Нажмите ШИФТ для x" + item.getMaxStackSize());
                    lore.add("");
                    lore.add(ChatColor.DARK_GRAY + ">> " + ChatColor.WHITE + "Ваш баланс: " + ChatColor.GOLD + money + " " + amount);
                    meta.setLore(lore);
                    item.setItemMeta(meta);
                }
                inventory.setItem(i,item);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return inventory;
    }

    public static String getAction(String name, int slot) {
        if((String) plugin.getConfig().get("Menu." + name + ".inventory." + slot + ".action.action") == null) {
            setAction(Actions.NULL_ACTION, name, slot);
        }
        return (String) plugin.getConfig().get("Menu." + name + ".inventory." + slot + ".action.action");
    }

    public static void setOpenInventory(String name, int slot, String OpenInventory) {
        setAction(Actions.OPEN_INVENTORY, name, slot);
        plugin.getConfig().set("Menu." + name + ".inventory." + slot + ".action.inventory", OpenInventory);
        plugin.saveConfig();
    }
    public static String getOpenInventoryName(String name, int slot) {
        return (String) plugin.getConfig().get("Menu." + name + ".inventory." + slot + ".action.inventory");
    }
    public static void setAction(String action, String invName, int slot) {
        plugin.getConfig().set("Menu." + invName + ".inventory." + slot + ".action.action", action);
        plugin.saveConfig();
    }

    public static void setBuyingItem(String Invname, int slot, int buyprice, int sellprice, MoneyType type) {
        setAction(Actions.BUY_ITEM, Invname, slot);
        plugin.getConfig().set("Menu." + Invname + ".inventory." + slot + ".action.buyprice", buyprice);
        plugin.getConfig().set("Menu." + Invname + ".inventory." + slot + ".action.sellprice", sellprice);
        plugin.getConfig().set("Menu." + Invname + ".inventory." + slot + ".action.moneytype", type.getName());
        plugin.saveConfig();
    }

    public static Inventory updatePlaceholders(String name, Player player) {
        Inventory inventory = getInventory(name, player);
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if(item == null || item.getItemMeta() == null || item.getItemMeta().getLore() == null) continue;
            ItemMeta meta = item.getItemMeta();
            List<String> lore = meta.getLore();
            lore = PlaceholderAPI.setPlaceholders(player, lore);
            meta.setLore(lore);
            item.setItemMeta(meta);
            inventory.setItem(i, item);
        }
        return inventory;
    }

    public static ItemStack getItem(String name, int i) {
        if (plugin.getConfig().get("Menu." + name + ".inventory." + i + ".item").equals("null") || plugin.getConfig().get("Menu." + name + ".inventory." + i + ".item") == null) return null;
        ItemStack item = (ItemStack) plugin.getConfig().get("Menu." + name + ".inventory." + i + ".item");
        if (getAction(name, i).equals(Actions.BUY_ITEM) && item != null) {
            if ((item.getItemMeta().getLore() != null && item.getItemMeta().getLore().get(item.getItemMeta().getLore().size() - 1).startsWith(ChatColor.DARK_GRAY + ">> " + ChatColor.WHITE + "Ваш баланс: "))) {
                ItemMeta meta = item.getItemMeta();
                List<String> lore = meta.getLore();
                int loreSize = lore.size() - 1;
                for (int j = 0; j < 6; j++) {
                    lore.remove(loreSize - j);
                }
                meta.setLore(lore);
                item.setItemMeta(meta);
            }
        }
        return item;
    }

    public static boolean hasInventory(String name) {
        List<String> list = new ArrayList<>(Objects.requireNonNull(RecipeGen.getPlugin().getConfig().getConfigurationSection("Menu")).getKeys(false));
        return list.contains(name);
    }
}
