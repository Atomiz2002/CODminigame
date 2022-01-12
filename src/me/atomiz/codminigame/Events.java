package me.atomiz.codminigame;

import org.bukkit.Bukkit;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
                Barrier.addBarrier(p, loc);
            else // GATE
                Gate.setGate(p, loc);
        }

        event.setCancelled(true);
    }

    @EventHandler
    void BlockInteractRepair(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;

        for (Barrier barrier : Main.worlds.get(event.getClickedBlock().getWorld().getName()).barriers)
            if (event.getClickedBlock().getLocation().equals(barrier.getRepair())) {

                return;
            }
    }

    @EventHandler
    void ShopSetup(PlayerInteractEntityEvent event) {
        if (event.isCancelled()) return;
        if (event.getRightClicked().getType() != EntityType.ITEM_FRAME) return;
        if (!Main.editors.containsKey(event.getPlayer())) return;
        if (Main.editors.get(event.getPlayer()).type != EditingType.SHOPS) return;

        Player p = event.getPlayer();
        Location loc = event.getRightClicked().getLocation();

        if (((ItemFrame) event.getRightClicked()).getItem().getType() == Material.AIR) {
            p.sendMessage(ChatColor.RED + "Item Frame is empty");
            return;
        }

        if (Main.editors.get(p).itemFrame == null) {

            if (Main.worlds.get(p.getWorld().getName()).shops.containsKey(loc))
                p.sendMessage(ChatColor.RED + "Shop already configured. Cancel or " + ChatColor.GREEN + "Set a new price:");
            else
                p.sendMessage(ChatColor.GREEN + "Enter a price");

            Main.editors.get(p).itemFrame = loc;
            event.setCancelled(true);
            return;
        }

        p.sendMessage(ChatColor.RED + "Please insert a price for your last selection");
    }

    @EventHandler
    void ShopPriceSet(AsyncPlayerChatEvent event) {
        if (!Main.editors.containsKey(event.getPlayer())) return;
        if (Main.editors.get(event.getPlayer()).itemFrame == null) return;

        if (!Helpers.isNumber(event.getMessage())) {
            event.getPlayer().sendMessage(ChatColor.RED + "Invalid price");
            return;
        }

        Player p = event.getPlayer();
        Location loc = Main.editors.get(p).itemFrame;
        int price = Integer.parseInt(event.getMessage());

        Main.worlds.get(p.getWorld().getName()).shops.put(loc, price);

        List<String> shops = new ArrayList<>();
        for (Map.Entry<Location, Integer> entry : Main.worlds.get(p.getWorld().getName()).shops.entrySet())
            shops.add(Helpers.stringifyLocation(entry.getKey(), false) + " " + entry.getValue());

        Main.main().getConfig().set("worlds." + p.getWorld().getName() + ".shops", shops);
        Main.main().saveConfig();

        Main.editors.remove(p);

        p.sendMessage(ChatColor.GREEN + "Price set to: " + price);

        event.setCancelled(true);
    }

    @EventHandler
    void ShopPurchase(PlayerInteractEntityEvent event) {
        if (event.isCancelled()) return;
        if (event.getRightClicked().getType() != EntityType.ITEM_FRAME) return;
        if (!GamePlayer.isPlayer(event.getPlayer())) return;

        Player p = event.getPlayer();

        if (Main.economy.withdrawPlayer(Bukkit.getOfflinePlayer(p.getUniqueId()), 5).transactionSuccess()) {

            p.sendMessage(ChatColor.GREEN + "Purchase successful");
        } else {
            p.sendMessage(ChatColor.RED + "Not enough money");
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Barrier.deselect(event.getPlayer());
    }
}