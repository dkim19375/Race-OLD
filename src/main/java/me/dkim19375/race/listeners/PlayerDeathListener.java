package me.dkim19375.race.listeners;

import me.dkim19375.race.RacePlugin;
import me.dkim19375.race.Racing;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.Map;
import java.util.UUID;

public class PlayerDeathListener implements Listener {
    private final RacePlugin plugin;

    public PlayerDeathListener(RacePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeathEvent(PlayerDeathEvent e) {
        if (!RacePlugin.gameRunning) {
            return;
        }
        for (UUID uuid : Racing.getPlayers()) {
            if (uuid == e.getEntity().getUniqueId()) {
                if (Racing.deaths.containsKey(uuid)) {
                    Map<UUID, Integer> map = Racing.deaths;
                    int deaths = map.get(uuid);
                    deaths = deaths + 1;
                    map.replace(uuid, deaths);
                    return;
                }
                Map<UUID, Integer> map = Racing.deaths;
                map.put(uuid, 1);
                Racing.deaths = map;
                if (!Racing.checkpoint.containsKey(e.getEntity().getUniqueId())) {
                    e.getEntity().teleport(plugin.getLocationUtils().getSpawn());
                    e.getEntity().sendMessage(ChatColor.RED + "You died! You are now respawning to spawn.");
                    return;
                }
                e.getEntity().teleport(new Location(e.getEntity().getWorld(), e.getEntity().getLocation().getX(),
                        100.0, plugin.getLocationUtils().getCheckpoint(Racing.checkpoint.get(e.getEntity().getUniqueId()))));
                e.getEntity().sendMessage(ChatColor.RED + "You died! You are now respawning to the last checkpoint.");
                return;
            }
        }
    }
}
