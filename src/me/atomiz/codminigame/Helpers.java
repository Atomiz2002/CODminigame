package me.atomiz.codminigame;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;

import java.util.ArrayList;
import java.util.List;

public class Helpers {

    public static void loadConfig() {
        Main.main().saveDefaultConfig();
        Main.main().reloadConfig();

        Main.worlds.clear();
        Main.main().getConfig().getConfigurationSection("worlds").getKeys(false).forEach(worldName -> Main.worlds.put(worldName,
                new GameWorld(worldName)));
    }

    public static Location toLocation(World world, String coord) {
        try {
            String[] coords = coord.split(",");
            double[] xyz = new double[3];

            for (int i = 0; i < 3; i++)
                xyz[i] = Double.parseDouble(coords[i]);

            return new Location(world, xyz[0], xyz[1], xyz[2]);
        } catch (Exception ex) {
//            Main.main().getLogger().warning("- Invalid location: " + coord);
        }

        return null;
    }

    public static String stringifyLocation(Location location, boolean rounded) {
        if (rounded)
            return location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ();
        else
            return location.getX() + "," + location.getY() + "," + location.getZ();
    }

    public static boolean isNumber(String string) {
        try {
            Integer.parseInt(string);
        } catch (NumberFormatException ex) {
            return false;
        }
        return true;
    }

    public static List<BlockState> getFill(Location loc1, Location loc2, List<Material> materials) {
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
                    if (materials == null || materials.contains(new Location(loc1.getWorld(), x, y, z).getBlock().getState().getType()))
                        blocks.add(new Location(loc1.getWorld(), x, y, z).getBlock().getState());

        return blocks;
    }

    public static <T> List<T> removeDuplicatesAndNulls(List<T> list) {
        if (list == null) return null;

        List<T> result = new ArrayList<>();

        for (T element : list)
            if (element != null && !result.contains(element))
                result.add(element);

        return result;
    }

    public static void addToConfigList(String path, String value) {

    }
}