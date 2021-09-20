package me.dkim19375.race.listeners;

import me.dkim19375.race.RacePlugin;
import me.dkim19375.race.util.GameUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {
    private final RacePlugin plugin;

    public PlayerQuitListener(RacePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler (ignoreCancelled = true)
    public void onPlayerQuitEvent(PlayerQuitEvent e) {
        GameUtils.playerLeave(plugin, e.getPlayer().getUniqueId());
    }
}
