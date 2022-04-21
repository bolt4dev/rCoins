package me.rin.coin.util;

import org.bukkit.ChatColor;

public class CoinUtils {

    public static String colored(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}