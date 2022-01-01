package me.atomiz.codminigame;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.stream.Collectors;

public class Events implements Listener {

    @EventHandler
    void BarrierGateSetup(PlayerInteractEvent event) {
        if (event.isCancelled()) return;
        if (!Main.editors.containsKey(event.getPlayer())) return;
        if (Main.editors.get(event.getPlayer()).type == EditingType.SHOPS) return;

        Player p = event.getPlayer();
        Location loc = event.getClickedBlock().getLocation();
        Editor editor = Main.editors.get(p);
        Action a = event.getAction();

        if (a == Action.RIGHT_CLICK_BLOCK || a == Action.LEFT_CLICK_BLOCK) {
            if (editor.type == EditingType.BARRIER)
                Barriers.addBarrier(p, loc);
            else // GATE
                Gate.selectGate(p, loc);
        }

        event.setCancelled(true);
    }

    @EventHandler
    void BlockInteractRepair(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;

        for (Barriers barriers : Main.worlds.get(event.getClickedBlock().getWorld().getName()).barriers)
            if (event.getClickedBlock().getLocation().equals(barriers.repair.getLocation())) {

                return;
            }
    }

    @EventHandler
    void ShopsSetup(PlayerInteractEntityEvent event) {
        if (event.isCancelled()) return;
        if (event.getRightClicked().getType() != EntityType.ITEM_FRAME) return;
        if (!Main.editors.containsKey(event.getPlayer())) return;
        if (Main.editors.get(event.getPlayer()).type != EditingType.SHOPS) return;

        Player p = event.getPlayer();
        ItemFrame itemFrame = (ItemFrame) event.getRightClicked();

        if (itemFrame.getItem().getType() == Material.AIR) {
            p.sendMessage(ChatColor.RED + "Item Frame is empty");
            return;
        }

        if (Main.editors.get(p).itemFrame == null) {
            p.sendMessage(ChatColor.GREEN + "Please enter a price");
            Main.editors.get(p).itemFrame = itemFrame;
            event.setCancelled(true);
            return;
        }

        p.sendMessage(ChatColor.RED + "Please insert a price for your last selection");
    }

    @EventHandler
    void ShopPriceInsert(AsyncPlayerChatEvent event) {
        if (!Main.editors.containsKey(event.getPlayer())) return;
        if (Main.editors.get(event.getPlayer()).itemFrame == null) return;

        Player p = event.getPlayer();

        try {
            Location shopLoc = event.getPlayer().getLocation();
            int price = Integer.parseInt(event.getMessage());

            Main.worlds.get(p.getWorld().getName()).shops.add(new Shop(shopLoc, price));

            Main.config.set("worlds." + p.getWorld().getName() + ".shops",
                    Main.worlds.get(p.getWorld().getName()).shops.stream().map(Shop::toString).collect(Collectors.toList()));

            Main.main().saveConfig();

            Main.editors.remove(p);

            p.sendMessage(ChatColor.GREEN + "Set the price to: " + price);

        } catch (NumberFormatException ex) {
            p.sendMessage(ChatColor.RED + "Invalid price");
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Barriers.deselect(event.getPlayer());
    }
}