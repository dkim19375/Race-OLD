package me.dkim19375.race;

import org.bukkit.OfflinePlayer;

import java.util.*;

public class Racing {
    private List<UUID> finishedRanking;
    public static LinkedHashMap<UUID, Double> positionsRanked;
    private static UUID[] players;
    public static int countDown = 0;
    public static Map<UUID, Integer> checkpoint;
    public static Map<UUID, Integer> deaths;

    public Racing(UUID[] players) {
        checkpoint = new HashMap<>();
        countDown = 0;
        positionsRanked = new LinkedHashMap<>();
        Racing.players = players;
        finishedRanking = new ArrayList<>();
        deaths = new HashMap<>();
    }

    public static void removePlayer(UUID uuid) {
        List<UUID> list = new ArrayList<>(Arrays.asList(players));
        list.remove(uuid);
        players = list.toArray(new UUID[0]);
    }

    public static void removePlayer(OfflinePlayer player) {
        List<UUID> list = new ArrayList<>(Arrays.asList(players));
        list.remove(player.getUniqueId());
        players = list.toArray(new UUID[0]);
    }

    public static UUID[] getPlayers() {
        return players;
    }

    public void setPlayers(UUID[] players) {
        Racing.players = players;
    }

    public List<UUID> getFinishedRanking() {
        return finishedRanking;
    }
}
