package me.dkim19375.race.util;

import me.dkim19375.dkim19375core.Entry;
import me.dkim19375.dkim19375core.Region;
import me.dkim19375.race.RacePlugin;
import me.dkim19375.race.placeholders.RaceExpansion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;

public class LocationUtils {
    private final RacePlugin plugin;

    public LocationUtils(RacePlugin plugin) {
        this.plugin = plugin;
    }

    public void setCheckpoint(int id, double z) {
        plugin.getLocationsConfig().getConfig().set("checkpoints." + id + ".z.", z);
        plugin.getLocationsConfig().save();
    }
    public void setCheckpoint(String world) {
        plugin.getLocationsConfig().getConfig().set("checkpoints.world.", world);
        plugin.getLocationsConfig().save();
    }
    public void setSpawn(Location loc) {
        plugin.getLocationsConfig().getConfig().set("spawn.x.", loc.getX());
        plugin.getLocationsConfig().getConfig().set("spawn.y.", loc.getY());
        plugin.getLocationsConfig().getConfig().set("spawn.z.", loc.getZ());
        plugin.getLocationsConfig().getConfig().set("spawn.world.", loc.getWorld().toString());
        plugin.getLocationsConfig().save();
    }
    public Location getSpawn() {
        Double x = plugin.getLocationsConfig().getConfig().getDouble("spawn.x");
        Double y = plugin.getLocationsConfig().getConfig().getDouble("spawn.y");
        Double z = plugin.getLocationsConfig().getConfig().getDouble("spawn.z");
        World world = Bukkit.getWorld(plugin.getLocationsConfig().getConfig().getString("spawn.world"));
        if (world == null) {
            System.out.println("World is null");
        }
        return new Location(world, x, y, z);
    }
    public void setEnd(Region region) {
        plugin.getLocationsConfig().getConfig().set("end.a.x", region.getPos1().getX());
        plugin.getLocationsConfig().getConfig().set("end.a.y", region.getPos1().getY());
        plugin.getLocationsConfig().getConfig().set("end.a.z", region.getPos1().getZ());
        plugin.getLocationsConfig().getConfig().set("end.b.x", region.getPos2().getX());
        plugin.getLocationsConfig().getConfig().set("end.b.y", region.getPos2().getY());
        plugin.getLocationsConfig().getConfig().set("end.b.z", region.getPos2().getZ());
        plugin.getLocationsConfig().getConfig().set("end.world", region.getPos1().getWorld().toString());
        plugin.getLocationsConfig().save();
    }

    public Location getNearestEnd(Entity player) {
        int y1 = (int) getEnd().getPos1().distance(player.getLocation());
        int y2 = (int) getEnd().getPos2().distance(player.getLocation());
        if (Math.min(y1, y2) == y1) {
            return getEnd().getPos1();
        }
        return getEnd().getPos2();
    }

    public Region getEnd() {
        FileConfiguration config = plugin.getLocationsConfig().getConfig();
        double aX = config.getDouble("end.a.x");
        double aY = config.getDouble("end.a.y");
        double aZ = config.getDouble("end.a.z");
        double bX = config.getDouble("end.b.x");
        double bY = config.getDouble("end.b.y");
        double bZ = config.getDouble("end.b.z");
        World world = Bukkit.getWorld(config.getString("end.world"));
        Location a = new Location(world, aX, aY, aZ);
        Location b = new Location(world, bX, bY, bZ);
        return new Region(a, b);
    }

    public Integer getNearestCheckpoint(Entity player) {
        Double[] array = plugin.getLocationsConfig().getConfig().getDoubleList("checkpoints.coords").toArray(new Double[0]);
        int i = 0;
        List<Entry<Double, Integer>> locs = new ArrayList<>();
        for (Double d : array) {
            locs.add(new Entry<>(getFakeLocation(player, array[i]).getZ(), i + 1));
            i++;
        }

        int ii = 0;
        List<Double> loc = new ArrayList<>();
        for (Double d : array) {
            loc.add(new Entry<Location, Double>(getFakeLocation(player, array[ii]).getZ()));
            ii++;
        }
        loc.sort(Collections.reverseOrder());
        for (Entry<Double, Integer> e : locs) {
            if (e.getKey().equals(loc.get(0))) {
                return e.getValue();
            }
        }


/*        Double[] checkpoints = getCheckpoints().getValue();
        if (checkpoints.length < 2) {
            if (checkpoints.length < 1) {
                return null;
            }
            return 1;
        }
        Integer id = null;
        Integer distance = -1;
        int i = 1;
        for (Double d : checkpoints) {
            if ((getFakeLocation(player, d).distance(player.getLocation())) > distance) {
                distance = (int) getFakeLocation(player, d).distance(player.getLocation());
                id = i;
            }
            i++;
        }
        if (distance == -1) {
            throw new IllegalStateException("distance cannot be -1");
        }
        return id;*/
    }

    public Location getNearestCheckpointLocation(Entity player) {
        Integer id = getNearestCheckpoint(player);
        System.out.println("ID: " + id);
        System.out.println(getFakeLocation(player, getCheckpoint(id)));
        return getFakeLocation(player, getCheckpoint(id));
    }

    public Double getPercentFinished(Player player) {
        double beginning = plugin.getLocationUtils().getSpawn().getZ();
        double end = plugin.getLocationUtils().getEnd().getPos1().getZ();
        double end2 = plugin.getLocationUtils().getEnd().getPos2().getZ();
        double difference1 = RaceExpansion.calculateDifference(player.getWorld(), beginning, end);
        double difference2 = RaceExpansion.calculateDifference(player.getWorld(), beginning, end2);
        double diff = Math.min(difference1, difference2);
        double perPercent = diff / 100;
        return player.getLocation().getZ() / perPercent;
    }

    public Location getFakeLocation(Entity player, Double z) {
        return new Location(player.getWorld(), player.getLocation().getX(), player.getLocation().getY(), z);
    }

    public Entry<World, Double[]> getCheckpoints() {
        List<Double> StringList = plugin.getLocationsConfig().getConfig().getDoubleList("checkpoints.coords");
        return new Entry<>(Bukkit.getWorld(plugin.getLocationsConfig().getConfig().getString("checkpoints.world")),
                StringList.toArray(new Double[0]));
    }

    public void setCheckpoints(Entry<World, Double[]> a) {
        Double[] doubles = a.getValue();
        int i = 1;
        for (Double d : doubles) {
            setCheckpoint(i, d);
            setCheckpoint(a.getKey().getName());
            i++;
        }
    }

    public Double getCheckpoint(int id) {
        List<Double> list = plugin.getLocationsConfig().getConfig().getDoubleList("checkpoints.coords");
        Double[] array = list.toArray(new Double[0]);
        return array[id - 1];
    }
    
    public boolean verify(RacePlugin plugin) {
        List<String> paths = new ArrayList<>();
        paths.add("spawn.x");
        paths.add("spawn.y");
        paths.add("spawn.z");
        paths.add("spawn.world");
        paths.add("end.a.x");
        paths.add("end.a.y");
        paths.add("end.a.z");
        paths.add("end.b.x");
        paths.add("end.b.y");
        paths.add("end.b.z");
        paths.add("end.world");
        boolean checking = true;
        for (String s : paths) {
            if (plugin.getLocationsConfig().getConfig().get(s) == null) {
                checking = false;
            }
        }
        return checking;
    }
}
