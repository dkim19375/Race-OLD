package me.dkim19375.race.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Help {
    public static void showHelp(CommandSender player) {
        player.sendMessage(ChatColor.GREEN + "RacePlugin help");
        player.sendMessage(ChatColor.GOLD + "Permission needed: race.admin");
        player.sendMessage(ChatColor.GOLD + "/race help: Show this help page");
        player.sendMessage(ChatColor.GOLD + "/race start: Start the race");
        player.sendMessage(ChatColor.GOLD + "/race leave: Leave the race");
        player.sendMessage(ChatColor.GOLD + "/race tp <rank>: Teleport to a player in a certain rank");
        player.sendMessage(ChatColor.GOLD + "/race checkpoint create|remove <id>|list: Create, remove, and list checkpoints");
    }
}
