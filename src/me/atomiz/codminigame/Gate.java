package me.atomiz.codminigame;

import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Gate {
    public Location[] corners;
    public List<Material> materials;
    public List<BlockState> blocks;

    public Gate(Location loc1, Location loc2, List<Material> materials) {
        this.corners = new Location[]{loc1, loc2};
        this.materials = materials;
        this.blocks = Helpers.getFill(loc1, loc2, materials);

        String path = "worlds." + loc1.getWorld().getName() + ".gate";
        String location1 = Helpers.stringifyLocation(loc1, true);
        String location2 = Helpers.stringifyLocation(loc2, true);
        String value = location1 + " " + location2;

        if (materials != null) value += " " + materials.stream().map(Material::name).collect(Collectors.joining(" "));

        Main.main().getConfig().set(path, value);
        Main.main().saveConfig();
    }

    public static Gate toGate(World world, String config) {
        try {
            List<String> values = new ArrayList<>(Arrays.asList(config.split(" ")));
            Location loc1 = Helpers.toLocation(world, values.remove(0));
            Location loc2 = Helpers.toLocation(world, values.remove(0));

            assert loc1 != null && loc2 != null;

            if (values.isEmpty()) return new Gate(loc1, loc2, null);

            List<Material> materials = new ArrayList<>();
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
                        if (material.name().contains(value.substring(1).toUpperCase())) exclude.add(material);

                    if (size == exclude.size()) failed.add(value);

                    continue;
                }

                if (value.endsWith("*")) {
                    int size = materials.size();

                    for (Material material : Material.values())
                        if (material.name().contains(value.substring(0, value.length() - 1).toUpperCase())) materials.add(material);

                    if (size == materials.size()) failed.add(value);

                    continue;
                }

                failed.add(value);
            }

            materials.removeIf(m -> {
                for (Material e : exclude)
                    if (m.name().contains(e.name())) return true;
                return false;
            });

            materials.addAll(exact);

            if (!failed.isEmpty()) Main.main().getLogger().warning("- Invalid materials: " + String.join(" ", failed));

            materials = Helpers.removeDuplicatesAndNulls(materials);

            if (Helpers.getFill(loc1, loc2, materials).isEmpty()) {
                Main.main().getLogger().warning("= No blocks found for the gate");
                return null;
            }

            return new Gate(loc1, loc2, materials);
        } catch (Exception ex) {
            Main.main().getLogger().warning("= Invalid gate: " + config);
            return null;
        }
    }

    public static void setGate(Player p, Location loc) {
        if (select(p, loc)) return;

        List<BlockState> states = Main.editors.get(p).states;

        Location loc1 = states.get(0).getLocation();
        Location loc2 = states.get(1).getLocation();

        for (BlockState state : Main.editors.get(p).states)
            state.update(true);

        List<String> materials = new ArrayList<>();
        String location1 = Helpers.stringifyLocation(loc1, true);
        String location2 = Helpers.stringifyLocation(loc2, true);

        for (BlockState block : Helpers.getFill(loc1, loc2, null))
            materials.add(block.getType().name());

        Helpers.removeDuplicatesAndNulls(materials);

        Main.worlds.get(p.getWorld().getName()).gate = toGate(loc.getWorld(), String.join(" ", location1, location2, String.join(" ", materials)));
        Main.editors.remove(p);

        p.sendMessage(ChatColor.GOLD + "Gate set at: " + ChatColor.AQUA + Helpers.stringifyLocation(loc1, true) + " " + Helpers.stringifyLocation(loc2, true));
    }

    public static boolean select(Player p, Location loc) {
        if (Main.editors.get(p).states.size() == 2) return false;

        List<BlockState> states = Main.editors.get(p).states;

        states.add(loc.getBlock().getState());
        loc.getBlock().setType(Material.WOOL);
        //noinspection deprecation
        loc.getBlock().setData(DyeColor.YELLOW.getData());

        if (states.size() == 0) p.sendMessage(ChatColor.GOLD + "Select the first corner");
        else if (states.size() == 1) p.sendMessage(ChatColor.GOLD + "Select the second corner");
        else return false;

        return true;
    }
}