package me.atomiz.codminigame;

import org.bukkit.entity.Player;

public class GamePlayer {
    Player player;
//    int kills;

    public static boolean isPlayer(Player p) {
        for (GamePlayer player : Main.worlds.get(p.getWorld().getName()).players)
            if (player.player.equals(p))
                return true;

        return false;
    }
}