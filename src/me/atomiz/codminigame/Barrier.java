package me.atomiz.codminigame;

import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Barrier {
    public List<BlockState> blocks = new ArrayList<>();
    public Location[] values;

    public Barrier(Location l1, Location l2, Location repair) {
        values = new Location[]{l1, l2, repair};
        blocks.addAll(Helpers.getFill(l1, l2, null));
    }

    public static Barrier toBarrier(World world, String config) {
        try {
            String[] values = config.split(" ");
            Location loc1 = Helpers.toLocation(world, values[0]);
            Location loc2 = Helpers.toLocation(world, values[1]);
            Location repair = Helpers.toLocation(world, values[2]);

            assert repair != null;
            return new Barrier(loc1, loc2, repair);
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

        Barrier barrier = new Barrier(loc1, loc2, repair);

        Main.worlds.get(p.getWorld().getName()).barriers.add(barrier);

        List<String> locs = Main.main().getConfig().getStringList("worlds." + p.getWorld().getName() + ".barriers");
        locs.add(barrier.toString());

        Main.main().getConfig().set("worlds." + p.getWorld().getName() + ".barriers", locs);
        Main.main().saveConfig();

        Main.editors.remove(p);
        p.sendMessage(ChatColor.GREEN + "New barrier added - " +
                Helpers.getFill(loc1, loc2, null).size() + ", " + Main.worlds.get(p.getWorld().getName()).barriers.size());
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

    public Location getRepair() {
        return values[2];
    }

    public void breakRandom() {
        if (blocks.isEmpty()) return;

        int r = new Random().nextInt(blocks.size());
        blocks.get(r).getBlock().breakNaturally();
        // TODO
    }

    @Override
    public String toString() {
        return Helpers.stringifyLocation(values[0], true) + " " +
                Helpers.stringifyLocation(values[1], true) + " " +
                Helpers.stringifyLocation(values[2], true);
    }
}