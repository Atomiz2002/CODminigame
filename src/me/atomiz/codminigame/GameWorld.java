package me.atomiz.codminigame;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

public class GameWorld {
    public World world;
    public List<Location> spawns = new ArrayList<>();
    public List<Barriers> barriers = new ArrayList<>();
    public List<Shop> shops = new ArrayList<>();
    public Gate gate;
    public Location chest;

    public GameWorld() {
    }

    boolean isInvalid() {
        return world == null || spawns.isEmpty() || barriers.isEmpty() || shops.isEmpty() || gate == null || chest == null;
    }
}