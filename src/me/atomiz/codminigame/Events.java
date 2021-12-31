package me.atomiz.codminigame;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Events implements Listener {

    @EventHandler
    void BlockInteract(PlayerInteractEvent event) {
        if (event.isCancelled()) return;
        if (!Main.editors.containsKey(event.getPlayer())) return;

        Player p = event.getPlayer();
        Location loc = event.getClickedBlock().getLocation();
        Editor editor = Main.editors.get(p);
        Action a = event.getAction();

        if (a == Action.RIGHT_CLICK_BLOCK || a == Action.LEFT_CLICK_BLOCK) {
            switch (editor.type) {
                case BARRIER:
                    Barriers.addBarrier(p, loc);
                    break;

                case SHOPS:
                    Shop.newShop(p, loc);
                    break;

                case GATE:
                    Gate.setGate(p, loc);
                    break;
            }
        }

        event.setCancelled(true);
    }

    @EventHandler
    void BlockInteractRepair(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;

        for (Barriers barriers : Main.worlds.get(event.getClickedBlock().getWorld().getName()).barriers)
            if (event.getClickedBlock().getLocation().equals(barriers.repair.getLocation())) {
//                event.getPlayer().sendMessage("You clicked repair");

                return;
            }
    }

    @EventHandler
    void ItemFrameInteract(PlayerInteractEntityEvent event) {
        event.getPlayer().sendMessage("you clicked an entity");
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Barriers.deselect(event.getPlayer());
    }
}