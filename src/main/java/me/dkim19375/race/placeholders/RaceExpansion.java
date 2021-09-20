package me.dkim19375.race.placeholders;

import me.dkim19375.dkim19375core.external.PAPIExpansion;
import me.dkim19375.race.RacePlugin;
import me.dkim19375.race.Racing;
import me.dkim19375.race.util.GameUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;

public class RaceExpansion extends PAPIExpansion {
    private final RacePlugin plugin;
    public RaceExpansion(JavaPlugin plugin) {
        super(plugin, "race", "dkim19375", plugin.getDescription().getVersion());
        this.plugin = (RacePlugin) plugin;
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (identifier == null || identifier.equalsIgnoreCase("")) {
            return null;
        }
        if (!RacePlugin.gameRunning) {
            return ChatColor.RED + "Game not started yet!";
        }
        switch (identifier.toLowerCase()) {
            case "position":
                if (GameUtils.getPosition(player.getUniqueId(), plugin) == 0) {
                    return null;
                }
                return String.valueOf(GameUtils.getPosition(player.getUniqueId(), plugin));
            case "deaths":
                Map<UUID, Integer> map = Racing.deaths;
                if (map.containsKey(player.getUniqueId())) {
                    return map.get(player.getUniqueId()).toString();
                }
                return "0";
            case "completed":
                int beginning = (int) plugin.getLocationUtils().getSpawn().getZ();
                int end = (int) plugin.getLocationUtils().getEnd().getPos1().getZ();
                int end2 = (int) plugin.getLocationUtils().getEnd().getPos2().getZ();
                int difference1 = calculateDifference(player.getWorld(), beginning, end);
                int difference2 = calculateDifference(player.getWorld(), beginning, end2);
                int diff = Math.min(difference1, difference2);
                int perPercent = diff / 100;
                return String.valueOf((int) player.getLocation().getZ() / perPercent);
            case "competitors":
                return String.valueOf(Racing.getPlayers().length);
            default:
                return null;
        }
    }

    public static int calculateDifference(World world, double z, double z2) {
        Location a = new Location(world, 0.0, 0.0, z);
        Location b = new Location(world, 0.0, 0.0, z2);
        return (int) a.distance(b);
    }
}
