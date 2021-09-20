package me.dkim19375.race.listeners;

import me.dkim19375.dkim19375core.external.FormattingUtils;
import me.dkim19375.race.RacePlugin;
import me.dkim19375.race.Racing;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class PlayerMoveListener implements Listener {
    private final RacePlugin plugin;

    public PlayerMoveListener(RacePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onPlayerMoveEvent(PlayerMoveEvent e) {
        if (!RacePlugin.gameRunning) {
            return;
        }
        if (RacePlugin.starting) {
            boolean inArray = false;
            for (UUID uuid : Racing.getPlayers()) {
                if (uuid == e.getPlayer().getUniqueId()) {
                    inArray = true;
                }
            }
            if (!inArray) {
                return;
            }
            e.setCancelled(true);
            return;
        }
        boolean inArray = false;
        for (UUID uuid : Racing.getPlayers()) {
            if (uuid == e.getPlayer().getUniqueId()) {
                inArray = true;
            }
        }
        if (!inArray) {
            return;
        }

        if (((int) plugin.getLocationUtils().getNearestEnd(e.getPlayer()).getZ() == ((int) e.getPlayer().getLocation().getZ()))) {
            System.out.println("end");
            if (plugin.getGame().getFinishedRanking().contains(e.getPlayer().getUniqueId())) {
                return;
            }
            if (plugin.getConfig().getString("commands.end." + (plugin.getGame().getFinishedRanking().size() + 1)) == null) {
                for (String cmd : plugin.getConfig().getStringList("commands.end.default")) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), FormattingUtils.formatWithPAPIAndColors(e.getPlayer(), cmd));
                }
            } else {
                for (String cmd : plugin.getConfig().getStringList("commands.end." + (plugin.getGame().getFinishedRanking().size() + 1))) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), FormattingUtils.formatWithPAPIAndColors(e.getPlayer(), cmd));
                }
            }
            Racing.removePlayer(e.getPlayer());
            Bukkit.broadcastMessage(ChatColor.GREEN + "Player " + e.getPlayer().getDisplayName() + " finished the race in "
                    + (plugin.getGame().getFinishedRanking().size() + 1) + " place!");
            plugin.getGame().getFinishedRanking().add(e.getPlayer().getUniqueId());
            if (Racing.getPlayers().length < 1) {
                Bukkit.broadcastMessage(ChatColor.GOLD + "Everyone finished the race! It is now over!");
                plugin.reset();
                RacePlugin.gameRunning = false;
            }
            System.out.println("end of end");
            return;
        }
        // FIXME
        System.out.println("middle");
        System.out.println("Nearest checkpoint location " + (int) plugin.getLocationUtils().getNearestCheckpointLocation(e.getPlayer()).getZ());
        System.out.println("Player z location" + ((int) e.getPlayer().getLocation().getZ()));
        if (((int) plugin.getLocationUtils().getNearestCheckpointLocation(e.getPlayer()).getZ() == ((int) e.getPlayer().getLocation().getZ()))) {
            System.out.println("checkpoint start");
            if (Racing.checkpoint.containsKey(e.getPlayer().getUniqueId())) {
                if (Racing.checkpoint.get(e.getPlayer().getUniqueId()).equals(plugin.getLocationUtils().getNearestCheckpoint(e.getPlayer()))) {
                    e.getPlayer().sendMessage("DEBUG: Already went to this checkpoint");
                    return;
                }
            }
            if (!Racing.checkpoint.containsKey(e.getPlayer().getUniqueId())) {
                Racing.checkpoint.put(e.getPlayer().getUniqueId(), plugin.getLocationUtils().getNearestCheckpoint(e.getPlayer()));
                e.getPlayer().sendMessage(ChatColor.AQUA + "You got to checkpoint " + plugin.getLocationUtils().getNearestCheckpoint(e.getPlayer()));
                return;
            }
            Racing.checkpoint.replace(e.getPlayer().getUniqueId(), plugin.getLocationUtils().getNearestCheckpoint(e.getPlayer()));
            e.getPlayer().sendMessage(ChatColor.AQUA + "You got to checkpoint " + plugin.getLocationUtils().getNearestCheckpoint(e.getPlayer()));
        }
    }
}
