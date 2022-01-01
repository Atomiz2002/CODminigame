package me.atomiz.codminigame;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Helpers {

    public static void loadConfig() {
        Main.main().saveDefaultConfig();
        Main.main().reloadConfig();
        Main.config = Main.main().getConfig();
        FileConfiguration config = Main.main().getConfig();

        Main.worlds.clear();
        Main.config.getConfigurationSection("worlds").getKeys(false).forEach(worldName -> {
            System.out.println();
            Main.main().getLogger().info("======[ Data for world: " + worldName + " ]======");

            GameWorld gameWorld = new GameWorld();
            List<String> list;
            String path = "worlds." + worldName + ".";

            // spawns
            list = config.getStringList(path + "spawns");
            list.forEach(coords -> gameWorld.spawns.add(Helpers.toLocation(worldName, coords)));

            Main.main().getLogger().info("+ Loaded " + gameWorld.spawns.size() + " valid spawn points");

            // barriers
            list = config.getStringList(path + "barriers");
            list.forEach(coords -> gameWorld.barriers.add(Barriers.toBarrier(worldName, coords)));
            while (gameWorld.barriers.remove(null)) ;

            Main.main().getLogger().info("+ Loaded " + gameWorld.barriers.size() + " valid barriers");

            // shops
            list = config.getStringList(path + "shops");
            list.forEach(values -> gameWorld.shops.add(Shop.toShop(worldName, values)));
            while (gameWorld.shops.remove(null)) ;

            Main.main().getLogger().info("+ Loaded " + gameWorld.shops.size() + " valid shops");

            // gate
            String values = config.getString(path + "gate");
            gameWorld.gate = Gate.toGate(Bukkit.getWorld(worldName), values);

            if (gameWorld.gate != null)
                Main.main().getLogger().info("+ Loaded the gate with " +
                        gameWorld.gate.blocks.size() + " blocks: " +
                        gameWorld.gate.blocks.stream().map(blockState -> blockState.getType().name()).collect(Collectors.joining(" ")));

            // chest
            if (Helpers.toLocation(worldName, config.getString(path + "chest")).getBlock().getType().name().toLowerCase().contains("chest")) {
                gameWorld.chest = Helpers.toLocation(worldName, config.getString(path + "chest"));
                Main.main().getLogger().info("+ Loaded the chest at: " + Helpers.stringifyLocation(gameWorld.chest, true));
            } else {
                Main.main().getLogger().warning("- No chest found");
            }

            if (gameWorld.isInvalid()) {
                Main.main().getLogger().warning("------[ Status: INVALID ]------");
            } else {
                Main.worlds.put(worldName, gameWorld);
                Main.main().getLogger().info("++++++[ Status: LOADED ]++++++");
            }
        });
    }

    public static Location toLocation(String worldName, String coord) {
        return toLocation(Bukkit.getWorld(worldName), coord);
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