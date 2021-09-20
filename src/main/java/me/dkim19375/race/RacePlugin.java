package me.dkim19375.race;

import me.dkim19375.dkim19375core.ConfigFile;
import me.dkim19375.dkim19375core.CoreJavaPlugin;
import me.dkim19375.dkim19375core.Licensing;
import me.dkim19375.race.commands.RaceCommand;
import me.dkim19375.race.commands.TabCompleterClass;
import me.dkim19375.race.listeners.PlayerDeathListener;
import me.dkim19375.race.listeners.PlayerMoveListener;
import me.dkim19375.race.listeners.PlayerQuitListener;
import me.dkim19375.race.placeholders.RaceExpansion;
import me.dkim19375.race.util.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

public class RacePlugin extends CoreJavaPlugin {
    public static boolean gameRunning;
    public static Racing racing;
    public static boolean starting = false;
    private final LocationUtils locationUtils = new LocationUtils(this);
    private final ConfigFile locationsConfig = new ConfigFile(this, "locations.yml");

    public void onEnable() {
        registerCommands();
        registerExpansions();
        registerListeners();
        validate();
        createConfigs();
        reset();
        RacePlugin.gameRunning = false;
    }

    public void registerExpansions() {
        new RaceExpansion(this).register();
    }

    public void reset() {
        UUID[] uuidArray = new UUID[Bukkit.getOnlinePlayers().size()];
        int i = 0;
        for (Player p : Bukkit.getOnlinePlayers()) {
            uuidArray[i] = p.getUniqueId();
            i++;
        }
        racing = new Racing(uuidArray);
    }
    public void validate() {
        try {
            Licensing.validate(new URL("https://gist.githubusercontent.com/dkim19375/1d0029d2957f6df54ef24f9b00fc23ea/raw"), this, (validated, plugin, exception) -> {
                if (validated == null || !validated) {
                    printToConsole(ChatColor.RED + "Please contact dkim19375, as there is an error!");
                }
            });
        } catch (MalformedURLException e) {
            printToConsole(ChatColor.RED + "Please contact dkim19375, as there is an error!");
            e.printStackTrace();
        }
    }

    private void createConfigs() {
        saveDefaultConfig();
        locationsConfig.createConfig();
        reloadConfigs();
    }

    public void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerMoveListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);
    }

    public void registerCommands() {
        getCommand("race").setExecutor(new RaceCommand(this));
        getCommand("race").setTabCompleter(new TabCompleterClass());
    }

    public void onDisable() {
        locationsConfig.save();
    }

    public void reloadConfigs() {
        reloadConfig();
        locationsConfig.reload();
    }

    public Racing getGame() {
        return racing;
    }

    public ConfigFile getLocationsConfig() {
        return locationsConfig;
    }

    public LocationUtils getLocationUtils() {
        return locationUtils;
    }
}
