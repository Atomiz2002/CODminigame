package me.atomiz.codminigame;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class Main extends JavaPlugin {

    static Economy economy;
    private static Main main;
    static FileConfiguration config;
    static final Map<String, GameWorld> worlds = new HashMap<>();
    static final Map<Player, Editor> editors = new HashMap<>();

    public static Main main() {
        return main;
    }

    @Override
    public void onEnable() { // Adding some comments to help myself understand; Could be wrong about some
        main = this;
        Helpers.loadConfig(); // load the GWs

        economy = Bukkit.getServicesManager().load(Economy.class); // for the currency substraction n checks

        getCommand("codm").setExecutor(new Commands());
        getServer().getPluginManager().registerEvents(new Events(),this);

        getLogger().info("Enabled");
    }

    @Override
    public void onDisable() {
        for (Player p : editors.keySet())
            Barriers.deselect(p);

        getLogger().info("Disabled");
    }
}

/*
ok so the big ideas is.
i have a custom object -> GameWorld
a game world is equal to a map
each map has its own gameworld representing it
and each map has to be defined in the config
so basically worlds: in the config contains the gameworlds
 */