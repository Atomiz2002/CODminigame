package me.atomiz.codminigame;

import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Commands implements TabExecutor {

    Main main = Main.main();

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("h")) {
            // i wanna die
            s.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "\n>> " + //
                    ChatColor.YELLOW + "" + ChatColor.BOLD + "CODM commands" + //
                    ChatColor.RED + ChatColor.BOLD + " <<" + ChatColor.AQUA + //
                    "\n> " + ChatColor.GOLD + ChatColor.UNDERLINE + "sp" + ChatColor.RESET + ChatColor.GOLD + "awn" + ChatColor.AQUA + //
                    " - Add/Remove your location as a spawnpoint" + //
                    "\n> " + ChatColor.GOLD + ChatColor.UNDERLINE + "b" + ChatColor.RESET + ChatColor.GOLD + "arrier" + //
                    ChatColor.AQUA + " - Create a new barrier " + ChatColor.RED + "(remove using the config)" + ChatColor.AQUA + //
                    "\n> " + ChatColor.GOLD + ChatColor.UNDERLINE + "g" + ChatColor.AQUA + ChatColor.RESET + ChatColor.GOLD + "ate" + //
                    ChatColor.AQUA + " - Select the gate ends" + //
                    "\n> " + ChatColor.GOLD + "shop/s" + ChatColor.AQUA + //
                    " - Enter/Leave shop editing mode" + //
                    "\n> " + ChatColor.GOLD + ChatColor.UNDERLINE + "c" + ChatColor.RESET + ChatColor.GOLD + "ancel" + ChatColor.AQUA + //
                    " - Cancel your current edit" + //
                    "\n> " + ChatColor.GOLD + ChatColor.UNDERLINE + "s" + ChatColor.RESET + ChatColor.GOLD + "how " + ChatColor.YELLOW + "<sec>" + ChatColor.AQUA + //
                    " - Show your selections" + ChatColor.YELLOW + " <default: 5s>" + ChatColor.AQUA + //
                    "\n> " + ChatColor.GOLD + ChatColor.UNDERLINE + "r" + ChatColor.RESET + ChatColor.GOLD + "eload" + ChatColor.AQUA + //
                    " - Reload the config " + ChatColor.RED + "(cancels all ongoing selections)");
            return true;
        }

        if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("r")) {
            reload(s);
            s.sendMessage("Loaded: " + String.join(", ", Gate.materials.stream().map(Material::name).collect(Collectors.toList())));
            return true;
        }

        if (!(s instanceof Player)) {
            s.sendMessage(ChatColor.RED + "You have to be a player to execute this command");
            return true;
        }

        Player p = (Player) s;

        Location loc = p.getLocation().getBlock().getLocation();
        GameWorld gameWorld = main.worlds.get(p.getWorld().getName());

        switch (args[0].toLowerCase()) {

            case "sp":
            case "spawn":

                if (!gameWorld.spawns.contains(loc)) {
                    gameWorld.spawns.add(loc.getBlock().getLocation());
                    p.sendMessage(ChatColor.GOLD + "Added spawn location at: " + ChatColor.AQUA + Helpers.stringifyLocation(loc));
                } else {
                    gameWorld.spawns.remove(loc.getBlock().getLocation());
                    p.sendMessage(ChatColor.GOLD + "Removed spawn location at: " + ChatColor.AQUA + Helpers.stringifyLocation(loc));
                }

                return true;

            case "b":
            case "barrier":

                return responseHandler(p, EditingType.BARRIER, "Click the 2 corners of the new barrier");

            case "sh":
            case "shop":
            case "shops":

                return responseHandler(p, EditingType.BARRIER, "Interact with any item frame to add it as a shop");

            case "g":
            case "gate":

                return responseHandler(p, EditingType.BARRIER, "Click the 2 corners of the gate");

            case "c":
            case "cancel":

                if (Main.editors.containsKey(p))
                    responseHandler(p, Main.editors.get(p).type, "Nothing to cancel");
                else
                    p.sendMessage(ChatColor.RED + "Nothing to cancel");

                return true;

            case "s":
            case "show":

                List<BlockState> original = new ArrayList<>();
                long delay = args.length > 1 ? Long.parseLong(args[1]) : 5;

                for (Location location : gameWorld.spawns) {
                    BlockState state = location.getBlock().getState();
                    original.add(state);
                    state.getBlock().setType(Material.WOOL);
                    state.getBlock().setData(DyeColor.LIME.getData());
                }

                for (Barriers barriers : gameWorld.barriers) {
                    for (BlockState state : barriers.blocks) {
                        original.add(state);
                        state.getBlock().setType(Material.WOOL);
                        state.getBlock().setData(DyeColor.PURPLE.getData());
                    }

                    original.add(barriers.repair);
                    barriers.repair.getBlock().setType(Material.WOOL);
                    barriers.repair.getBlock().setData(DyeColor.PINK.getData());
                }

                for (Shop shop : gameWorld.shops) {
                    BlockState state = shop.location.getBlock().getState();
                    original.add(state);
                    state.getBlock().setType(Material.WOOL);
                    state.getBlock().setData(DyeColor.BLUE.getData());
                }

                for (BlockState state : gameWorld.gate.blocks) {
                    original.add(state);
                    state.getBlock().setType(Material.WOOL);
                    state.getBlock().setData(DyeColor.YELLOW.getData());
                }

                Bukkit.getScheduler().runTaskLater(main, () -> {
                    for (BlockState state : original)
                        state.update(true);
                }, 20 * delay);

                p.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "\n# Spawns: " + gameWorld.spawns.size() + //
                        ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "\n# Barriers: " + gameWorld.barriers.size() + //
                        ChatColor.BLUE + "" + ChatColor.BOLD + "\n# Shops: " + gameWorld.shops.size() + //
                        ChatColor.YELLOW + "" + ChatColor.BOLD + "\n# Gates: " + !gameWorld.gate.blocks.isEmpty());
                return true;
        }

        return false;
    }

    private boolean responseHandler(Player p, EditingType type, String confirm) {

        if (!main.editors.containsKey(p)) {
            main.editors.put(p, new Editor(type));
            p.sendMessage(ChatColor.GOLD + confirm);

        } else if (main.editors.get(p).type == type) {
            p.sendMessage(ChatColor.RED + "Cancelled editing the " + ChatColor.DARK_RED + main.editors.get(p).type.name().toLowerCase());
            main.editors.remove(p);

        } else {
            p.sendMessage(ChatColor.RED + "Currently editing: " + ChatColor.DARK_RED + main.editors.get(p).type.name().toLowerCase() +
                    ChatColor.RED + " > " + ChatColor.GREEN + "/codm cancel");
        }

        return true;
    }

    private void reload(CommandSender s) {
        for (Player p : Main.editors.keySet())
            Barriers.deselect(p);

        Helpers.loadConfig();
        s.sendMessage(ChatColor.GREEN + "Reloaded config");
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {

        List<String> list = new ArrayList<>();

        if (commandSender instanceof Player) {

            if (Main.editors.containsKey((Player) commandSender)) {
                list.add(0, "cancel");
            } else {
                list.add("spawn");
                list.add("barrier");
                list.add("gate");
                list.add("shops");
            }
        }
        list.add("show");
        list.add("reload");

        return list;
    }
}