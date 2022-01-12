package me.atomiz.codminigame;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class GameWorld {
    public List<Location> spawns = new ArrayList<>();
    public List<Barrier> barriers = new ArrayList<>();
    public Map<Location, Integer> shops = new HashMap<>();
    public Gate gate;
    public boolean valid;

    public List<GamePlayer> players = new ArrayList<>();

    public GameWorld(String worldName) {
        FileConfiguration config = Main.main().getConfig();
        String path = "worlds." + worldName + ".";
        World world = Bukkit.getWorld(worldName);
        Logger log = Main.main().getLogger();

        log.info("[ Data for world: " + worldName + " ]");
        setSpawns(config, path + "spawns", world, log);
        setBarriers(config, path + "barriers", world, log);
        setShops(config, path + "shops", world, log);
        Bukkit.getLogger().info(config.getString(path + "gate"));
        setGate(config, path + "gate", world, log);
//        setChest(config, path + "chest", world);

        if (validate()) {
            log.info("[ Status: LOADED ]");
        } else {
            log.warning("[ Status: INVALID ]");
        }
    }

    public void setSpawns(FileConfiguration config, String path, World world, Logger log) {
        config.getStringList(path).forEach(coords -> spawns.add(Helpers.toLocation(world, coords)));

        if (spawns.isEmpty())
            log.warning("= No spawns specified");
        else
            log.info("+ Loaded " + spawns.size() + " valid spawn points");
    }

    public void setBarriers(FileConfiguration config, String path, World world, Logger log) {
        config.getStringList(path).forEach(coords -> barriers.add(Barrier.toBarrier(world, coords)));
        while (barriers.remove(null)) ;

        if (barriers.isEmpty())
            log.warning("= No valid barriers loaded");
        else
            log.info("+ Loaded " + barriers.size() + " valid barriers");
    }

    public void setShops(FileConfiguration config, String path, World world, Logger log) {
        config.getStringList(path).forEach(value -> {
            if (value.split(" ").length != 2) return;

            String coords = value.split(" ")[0];
            int price = Integer.parseInt(value.split(" ")[1]);
            Location location = Helpers.toLocation(world, coords);

            if (location == null) {
                Main.main().getLogger().warning("- Invalid shop: " + config);
                return;
            }

            Bukkit.getScheduler().runTask(Main.main(), () -> {

                boolean wasLoaded = world.isChunkLoaded(location.getChunk());

                location.getChunk().load();

                if (location.getWorld().getNearbyEntities(location, 0, 0, 0).isEmpty())
                    Main.main().getLogger().warning("- No shop found at " + coords);

                if (!wasLoaded)
                    location.getChunk().unload();
            });

            shops.put(location, price);
        });

        if (shops.isEmpty())
            log.warning("= No valid shops loaded");
        else
            log.info("+ Loaded " + shops.size() + " valid shops");
    }

    public void setGate(FileConfiguration config, String path, World world, Logger log) {
        gate = Gate.toGate(world, config.getString(path));
        if (gate == null) return;

        List<String> materials = new ArrayList<>();

        for (BlockState block : gate.blocks)
            if (!materials.contains(block.getType().name()))
                materials.add(block.getType().name());

        log.info("+ Loaded the gate with " + gate.blocks.size() + " blocks of type: " + String.join(" ", materials));
    }

    boolean validate() {
        return valid = !(spawns.isEmpty() || barriers.isEmpty() || shops.isEmpty() || gate == null);
    }
}