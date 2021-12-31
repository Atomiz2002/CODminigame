package me.atomiz.codminigame;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class Helpers {

    public static void loadConfig() {
        Main.main().reloadConfig();
        Main.main().saveDefaultConfig();
        Main.config = Main.main().getConfig();
        FileConfiguration config = Main.main().getConfig();

        Main.worlds.clear();
        Main.config.getConfigurationSection("worlds").getKeys(false).forEach(worldName -> {
            GameWorld gameWorld = new GameWorld();
            List<String> list;
            String path = "worlds." + worldName + ".";

            list = config.getStringList(path + "spawns");
            list.forEach(coords -> gameWorld.spawns.add(Helpers.toLocation(worldName, coords)));

            list = config.getStringList(path + "barriers");
            list.forEach(coords -> gameWorld.barriers.add(Barriers.toBarrier(worldName, coords)));

            list = config.getStringList(path + "shops");
            list.forEach(values -> gameWorld.shops.add(Shop.toShop(worldName, values)));

            String values = config.getString(path + "gate");
            gameWorld.gate = Gate.toGate(worldName, values);

            gameWorld.chest = Helpers.toLocation(worldName, config.getString(path + "chest"));

            Main.worlds.put(worldName, gameWorld);
        });
    }

    public static Location toLocation(String worldName, String coord) {
        return toLocation(Bukkit.getWorld(worldName), coord);
    }

    public static Location toLocation(World world, String coord) {
        String[] coords = coord.split(",");
        double[] xyz = new double[3];

        for (int i = 0; i < 3; i++)
            xyz[i] = Double.parseDouble(coords[i]);

        return new Location(world, xyz[0], xyz[1], xyz[2]);
    }

    public static String stringifyLocation(Location location) {
        return location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ();
    }

    public static List<BlockState> getFill(Location loc1, Location loc2) {
        List<BlockState> blocks = new ArrayList<>();

        double minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
        double minY = Math.min(loc1.getBlockY(), loc2.getBlockY());
        double minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());

        double maxX = Math.max(loc1.getBlockX(), loc2.getBlockX());
        double maxY = Math.max(loc1.getBlockY(), loc2.getBlockY());
        double maxZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());

        for (double x = minX; x <= maxX; x++)
            for (double y = minY; y <= maxY; y++)
                for (double z = minZ; z <= maxZ; z++)
                    blocks.add(new Location(loc1.getWorld(), x, y, z).getBlock().getState());

        return blocks;
    }
}