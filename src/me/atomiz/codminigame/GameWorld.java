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
    public Location chest; // the location of the chest although idk if ill be using that or just let war use vault for it

    public GameWorld() {
    }

    public GameWorld(World world, List<Location> spawns, List<Barriers> barriers, List<Shop> shops, Gate gate, Location chest) {
        this.world = world;
        this.spawns = spawns;
        this.barriers = barriers;
        this.shops = shops;
        this.gate = gate;
        this.chest = chest;
    }
}

/*
each gameworld stores the config values
worlds:
  world: is the world we will be getting by name using Bukkit.getWorld("name")

it has its own lists for the spawn points, all barriers for the zombies, the "shops"
[TODO get rid of that n use the itemframes locations i dont need a seperate shop object for them]
each GW has its own gate and a lucky chest
 */