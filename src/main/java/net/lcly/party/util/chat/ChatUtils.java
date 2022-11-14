package net.lcly.party.util.chat;

import org.bukkit.ChatColor;

public class ChatUtils {

    public static String colorize(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
}
