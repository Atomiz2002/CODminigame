package me.atomiz.codminigame;

import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Gate {
    static final List<Material> materials = new ArrayList<>();
    public List<BlockState> blocks = new ArrayList<>();

    public Gate(Location l1, Location l2) {
        Helpers.getFill(l1, l2).forEach(blockState -> {
            if (materials.contains(blockState.getType())) blocks.add(blockState);
        });
    }

    public static Gate toGate(String worldName, String config) {
        return toGate(Bukkit.getWorld(worldName), config);
    }

    public static Gate toGate(World world, String config) {
        List<String> values = new ArrayList<>(Arrays.asList(config.split(" ")));
        Location loc1 = Helpers.toLocation(world, values.remove(0));
        Location loc2 = Helpers.toLocation(world, values.remove(0));
        materials.clear();

        List<Material> exclude = new ArrayList<>();
        List<String> failed = new ArrayList<>();

        for (String value : values) {
            if (value.startsWith("-")) {

                int size = exclude.size();

                for (Material material : Material.values())
                    if (material.name().contains(value.substring(1).toUpperCase())) {
                        exclude.add(material);
                    }

                if (size == exclude.size()) failed.add(value);

                continue;
            }

            if (value.endsWith("*")) {

                int size = materials.size();

                for (Material material : Material.values())
                    if (material.name().contains(value.substring(0, value.length() - 1).toUpperCase())) {
                        materials.add(material);
                    }

                if (size == materials.size()) failed.add(value);

                continue;
            }

            if (Material.matchMaterial(value) == null)
                failed.add(value);
            else
                materials.add(Material.matchMaterial(value));
        }

        List<Material> r = new ArrayList<>();

        materials.removeIf(m -> {
            for (Material e : exclude)
                if (m.name().contains(e.name())) return true;
            return false;
        });

        if (!failed.isEmpty()) Main.main().getLogger().warning("Invalid gate block types: " + String.join(", ", failed));

        while (materials.remove(null)) ;

        return new Gate(loc1, loc2);
    }

    public static void setGate(Player p, Location loc) {
        if (select(p, loc)) return;

        List<BlockState> states = Main.editors.get(p).states;

        Location loc1 = states.get(0).getLocation();
        Location loc2 = states.get(1).getLocation();

        for (BlockState state : Main.editors.get(p).states)
            state.update(true);

        Main.worlds.get(p.getWorld().getName()).gate = new Gate(loc1, loc2);
        Main.editors.remove(p);

        p.sendMessage(ChatColor.GOLD + "Gate set at: " + ChatColor.AQUA + Helpers.stringifyLocation(loc1) + " " + Helpers.stringifyLocation(loc2));
    }

    public static boolean select(Player p, Location loc) {
        if (Main.editors.get(p).states.size() == 2) return false;

        List<BlockState> states = Main.editors.get(p).states;

        states.add(loc.getBlock().getState());
        loc.getBlock().setType(Material.WOOL);
        loc.getBlock().setData(DyeColor.YELLOW.getData());

        if (states.size() == 0) p.sendMessage(ChatColor.GOLD + "Select the first gate");
        else if (states.size() == 1) p.sendMessage(ChatColor.GOLD + "Select the last gate");
        else return false;

        return true;
    }
}