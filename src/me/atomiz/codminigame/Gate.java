package me.atomiz.codminigame;

import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Gate {
    public List<Material> materials = new ArrayList<>();
    public List<BlockState> blocks = new ArrayList<>();

    public Gate() {
    }

    public static Gate toGate(World world, String config) {
        try {
            Gate gate = new Gate();

            List<String> values = new ArrayList<>(Arrays.asList(config.split(" ")));
            Location loc1 = Helpers.toLocation(world, values.remove(0));
            Location loc2 = Helpers.toLocation(world, values.remove(0));

            assert loc1 != null && loc2 != null;
            gate.blocks = Helpers.getFill(loc1, loc2);

            if (values.isEmpty())
                return gate;

            List<Material> exclude = new ArrayList<>();
            List<Material> exact = new ArrayList<>();
            List<String> failed = new ArrayList<>();

            for (String value : values) {

                if (Material.matchMaterial(value) != null) {
                    exact.add(Material.matchMaterial(value));
                    continue;
                }

                if (value.startsWith("-")) {
                    int size = exclude.size();

                    for (Material material : Material.values())
                        if (material.name().contains(value.substring(1).toUpperCase()))
                            exclude.add(material);

                    if (size == exclude.size())
                        failed.add(value);

                    continue;
                }

                if (value.endsWith("*")) {
                    int size = gate.materials.size();

                    for (Material material : Material.values())
                        if (material.name().contains(value.substring(0, value.length() - 1).toUpperCase()))
                            gate.materials.add(material);

                    if (size == gate.materials.size())
                        failed.add(value);

                    continue;
                }

                failed.add(value);
            }

            gate.materials.removeIf(m -> {
                for (Material e : exclude)
                    if (m.name().contains(e.name()))
                        return true;
                return false;
            });

            gate.materials.addAll(exact);

            if (!failed.isEmpty())
                Main.main().getLogger().warning("- Invalid materials: " + String.join(" ", failed));

            while (gate.materials.remove(null)) ;

            gate.blocks.removeIf(blockState -> !gate.materials.contains(blockState.getType()) || blockState.getType() == Material.AIR);

            if (gate.blocks.isEmpty()) {
                Main.main().getLogger().warning("- No matching blocks for the gate");
                return null;
            }

            return gate;
        } catch (Exception ex) {
            Main.main().getLogger().warning("- Invalid gate: " + config);
        }

        return null;
    }

    public static void selectGate(Player p, Location loc) {
        if (select(p, loc)) return;

        List<BlockState> states = Main.editors.get(p).states;

        Location loc1 = states.get(0).getLocation();
        Location loc2 = states.get(1).getLocation();

        for (BlockState state : Main.editors.get(p).states)
            state.update(true);

        Main.worlds.get(p.getWorld().getName()).gate = toGate(loc.getWorld(),
                Helpers.stringifyLocation(loc1, true) + " " + Helpers.stringifyLocation(loc2, true) + " " +
                        Helpers.getFill(loc1, loc2).stream().map(blockState -> blockState.getType().name()).collect(Collectors.joining(" ")));

        Main.editors.remove(p);

        p.sendMessage(ChatColor.GOLD + "Gate set at: " +
                ChatColor.AQUA + Helpers.stringifyLocation(loc1, true) + " " + Helpers.stringifyLocation(loc2, true));
    }

    public static boolean select(Player p, Location loc) {
        if (Main.editors.get(p).states.size() == 2) return false;

        List<BlockState> states = Main.editors.get(p).states;

        states.add(loc.getBlock().getState());
        loc.getBlock().setType(Material.WOOL);
        //noinspection deprecation
        loc.getBlock().setData(DyeColor.YELLOW.getData());

        if (states.size() == 0) p.sendMessage(ChatColor.GOLD + "Select the first gate");
        else if (states.size() == 1) p.sendMessage(ChatColor.GOLD + "Select the last gate");
        else return false;

        return true;
    }
}