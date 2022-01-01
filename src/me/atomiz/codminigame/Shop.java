package me.atomiz.codminigame;

import org.bukkit.Location;

public class Shop {
    public Location location;
    public int price;

    public Shop(Location location, int price) {
        this.location = location;
        this.price = price;
    }

    public static Shop toShop(String worldName, String config) {
        try {
            String coords = config.split(" ")[0];
            int price = Integer.parseInt(config.split(" ")[1]);

            Location location = Helpers.toLocation(worldName, coords);
            boolean wasLoaded = location.getWorld().isChunkLoaded(location.getChunk());
            location.getChunk().load();

            if (!location.getWorld().getNearbyEntities(location, 0, 0, 0).isEmpty()) {

                if (!wasLoaded)
                    location.getChunk().unload();

                return new Shop(location, price);
            } else {
                Main.main().getLogger().warning("No shop found at " + coords);
            }

            if (!wasLoaded)
                location.getChunk().unload();
        } catch (Exception ex) {
            Main.main().getLogger().warning("- Invalid shop: " + config);
        }

        return null;
    }

    @Override
    public String toString() {
        return Helpers.stringifyLocation(location, false) + " " + price;
    }
}