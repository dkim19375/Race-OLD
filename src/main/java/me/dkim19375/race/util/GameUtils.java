package me.dkim19375.race.util;

import me.dkim19375.dkim19375core.PlayerUtils;
import me.dkim19375.race.RacePlugin;
import me.dkim19375.race.Racing;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class GameUtils {
    private static BukkitTask task;
    public static void playerLeave(RacePlugin plugin, UUID player) {
        UUID[] oldUUID = Racing.getPlayers();
        List<UUID> list = new ArrayList<>(Arrays.asList(oldUUID));
        //noinspection SuspiciousMethodCalls
        if (!list.contains(PlayerUtils.getFromAll(player.toString()))) {
            return;
        }
        UUID[] newUUID = new UUID[oldUUID.length - 1];
        int i = 0;
        for (UUID p : oldUUID) {
            if (!(p == player)) {
                newUUID[i] = p;
                i++;
            }
        }
        plugin.getGame().setPlayers(newUUID);
        PlayerUtils.getFromAll(player.toString()).sendMessage(ChatColor.AQUA + "You have left the race!");
        Bukkit.broadcastMessage(ChatColor.YELLOW + "Player " + ChatColor.GOLD + Bukkit.getPlayer(player).getDisplayName() +
                ChatColor.YELLOW + " has left the race!");
    }
    public static boolean startGame(RacePlugin plugin, CommandSender sender) {
        if (!plugin.getLocationUtils().verify(plugin)) {
            sender.sendMessage(ChatColor.RED + "The config isn't set up! Try doing /race reload");
            return false;
        }
        Bukkit.broadcastMessage(ChatColor.YELLOW + "RacePlugin starting! Please notify an admin if you didn't get teleported!");
        for (Player p : Bukkit.getOnlinePlayers()) {
            Location loc = plugin.getLocationUtils().getSpawn();
            p.teleport(loc);
        }
        RacePlugin.gameRunning = true;
        RacePlugin.starting = true;
        Racing.countDown = 5;
        task = Bukkit.getScheduler().runTaskTimer(plugin, GameUtils::checkCountdown, 1L, 20L);
        return true;
    }
    public static void checkCountdown() {
        Racing.countDown = Racing.countDown - 1;
        if (Racing.countDown == 0) {
            task.cancel();
            for (UUID uuid : Racing.getPlayers()) {
                Bukkit.getPlayer(uuid).sendTitle(ChatColor.GREEN + "" + ChatColor.BOLD + "GO!", "", 10, 50, 15);
            }
            Bukkit.broadcastMessage(ChatColor.GREEN + "The race started!");
            RacePlugin.starting = false;
            return;
        }
        for (UUID uuid : Racing.getPlayers()) {
            Bukkit.getPlayer(uuid).sendTitle(ChatColor.AQUA + "" + ChatColor.BOLD + "Starting in:", ChatColor.GOLD
                    + "" + ChatColor.BOLD + Racing.countDown, 0, 20, 0);
        }
    }

    public static LinkedHashMap<UUID, Double> sortMap(RacePlugin plugin) {
        //LinkedHashMap preserve the ordering of elements in which they are inserted
        LinkedHashMap<UUID, Double> reverseSortedMap = new LinkedHashMap<>();

//Use Comparator.reverseOrder() for reverse ordering
        getLocations(plugin).entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> reverseSortedMap.put(x.getKey(), x.getValue()));
        return reverseSortedMap;
    }

    public static Map<UUID, Double> getLocations(RacePlugin plugin) {
        UUID[] uuids = Racing.getPlayers();
        Double[] doubles = new Double[uuids.length];
        Map<UUID, Double> map = new HashMap<>();
        int i = 1;
        for (UUID uuid : uuids) {
            doubles[i] = plugin.getLocationUtils().getPercentFinished(Bukkit.getPlayer(uuid));
            map.put(uuid, doubles[i]);
            i++;
        }
        return map;
    }

    public static int getPosition(UUID player, RacePlugin plugin) {
        Racing.positionsRanked = GameUtils.sortMap(plugin);
        LinkedHashMap<UUID, Double> linkedHashMap = GameUtils.sortMap(plugin);
        for (UUID uuid : Racing.getPlayers()) {
            if (!linkedHashMap.containsKey(uuid)) {
                linkedHashMap.put(uuid, plugin.getLocationUtils().getPercentFinished(Bukkit.getPlayer(uuid)));
            }
        }
        for (Player p : Bukkit.getOnlinePlayers()) {
            linkedHashMap.remove(p.getUniqueId());
        }
        Racing.positionsRanked = GameUtils.sortMap(plugin);
        int i = 1;
        for (UUID uuid : Racing.positionsRanked.keySet()) {
            if (player == uuid) {
                return i;
            }
            i++;
        }
        return 0;
    }
}
