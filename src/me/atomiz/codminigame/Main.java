package me.atomiz.codminigame;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class Main extends JavaPlugin {

    static Economy economy;
    private static Main main;
    static final Map<String, GameWorld> worlds = new HashMap<>();
    static final Map<Player, Editor> editors = new HashMap<>();

    public static Main main() {
        return main;
    }

    @Override
    public void onEnable() { 
        main = this;
        Helpers.loadConfig();

        economy = Bukkit.getServicesManager().load(Economy.class); 

        getCommand("codm").setExecutor(new Commands());
        getServer().getPluginManager().registerEvents(new Events(),this);

        getLogger().info("Enabled");
    }

    @Override
    public void onDisable() {
        for (Player p : editors.keySet())
            Barrier.deselect(p);

        getLogger().info("Disabled");
    }
}