package me.atomiz.codminigame;

import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Barriers {
    public List<BlockState> blocks = new ArrayList<>();
    public BlockState repair;

    public Barriers(Location l1, Location l2, Location repair) {
        blocks.addAll(Helpers.getFill(l1, l2));
        this.repair = repair.getBlock().getState();
    }

    public static Barriers toBarrier(String worldName, String config) {
        return toBarrier(Bukkit.getWorld(worldName), config);
    }

    public static Barriers toBarrier(World world, String config) {
        try {
            String[] values = config.split(" ");
            Location loc1 = Helpers.toLocation(world, values[0]);
            Location loc2 = Helpers.toLocation(world, values[1]);
            Location repair = Helpers.toLocation(world, values[2]);

            assert repair != null;
            return new Barriers(loc1, loc2, repair);
        } catch (Exception ex) {
            Main.main().getLogger().warning("- Invalid barrier: " + config);
        }
        return null;
    }

    public static void addBarrier(Player p, Location l) {
        if (select(p, l)) return;

        List<BlockState> states = Main.editors.get(p).states;

        Location loc1 = states.get(0).getLocation();
        Location loc2 = states.get(1).getLocation();
        Location repair = states.get(2).getLocation();

        for (BlockState state : Main.editors.get(p).states)
            state.update(true);

        Main.worlds.get(p.getWorld().getName()).barriers.add(new Barriers(loc1, loc2, repair));
        // TODO add to config

        Main.editors.remove(p);
        p.sendMessage(ChatColor.GREEN + "New barrier added - " +
                Helpers.getFill(loc1, loc2).size() + ", " + Main.worlds.get(p.getWorld().getName()).barriers.size());
    }

    /**
     * Returns true if there is more to select
     */
    public static boolean select(Player p, Location loc) {
        if (Main.editors.get(p).states.size() == 3) return false;

        List<BlockState> states = Main.editors.get(p).states;

        states.add(loc.getBlock().getState());
        loc.getBlock().setType(Material.WOOL);
        //noinspection deprecation
        loc.getBlock().setData(DyeColor.PURPLE.getData());

        if (states.size() == 0) p.sendMessage(ChatColor.GOLD + "Select the first block");
        else if (states.size() == 1) p.sendMessage(ChatColor.GOLD + "Select the last block");
        else if (states.size() == 2) p.sendMessage(ChatColor.GOLD + "Select the repair block");
        else return false;

        return true;
    }

    public static void deselect(Player p) {
        if (!Main.editors.containsKey(p)) return;

        for (BlockState state : Main.editors.get(p).states)
            state.update(true);

        Main.editors.remove(p);
    }

    public void breakRandom() {
        if (blocks.isEmpty()) return;

        int r = new Random().nextInt(blocks.size());
        blocks.get(r).getBlock().breakNaturally();
        // TODO
    }
}