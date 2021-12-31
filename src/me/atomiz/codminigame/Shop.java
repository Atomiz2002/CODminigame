package me.atomiz.codminigame;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Shop {
    public Location location;
    public int price;
    public ItemStack item; // could be a gun or a regular item

    public Shop(Location location, int price, ItemStack item) {
        this.location = location;
        this.price = price;
        this.item = item;
    }

    // shop: 0,0,0 price
    public static Shop toShop(String worldName, String config) {
        String coords = config.split(" ")[0];
        int price = Integer.parseInt(config.split(" ")[1]);

        Location location = Helpers.toLocation(worldName, coords);
        ItemStack item = null;

        return new Shop(location, price, item);
    }

    public static void newShop(Player p, Location loc) {

    }
}