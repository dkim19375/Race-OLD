package me.dkim19375.race.commands;

import me.dkim19375.dkim19375core.Entry;
import me.dkim19375.dkim19375core.PlayerUtils;
import me.dkim19375.race.RacePlugin;
import me.dkim19375.race.Racing;
import me.dkim19375.race.util.GameUtils;
import me.dkim19375.race.util.Help;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class RaceCommand implements CommandExecutor {
    private final RacePlugin plugin;

    public RaceCommand(RacePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("race.admin")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to run this command!");
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Not enough arguments!");
            Help.showHelp(sender);
            return true;
        }
        if ((args.length > 1) && (!args[0].equalsIgnoreCase("checkpoint")) && (!args[0].equalsIgnoreCase("tp")) && (!args[0].equalsIgnoreCase("leave"))) {
            sender.sendMessage(ChatColor.RED + "Too many arguments!");
            Help.showHelp(sender);
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "help":
                Help.showHelp(sender);
                return true;
            case "start":
                if (RacePlugin.gameRunning) {
                    sender.sendMessage(ChatColor.RED + "The race is already started!");
                    return true;
                }
                GameUtils.startGame(plugin, sender);
                return true;
            case "leave":
                if (args.length > 2) {
                    sender.sendMessage(ChatColor.RED + "Too many arguments!");
                    Help.showHelp(sender);
                    return true;
                }
                if (args.length > 1) {
                    if (PlayerUtils.getFromAll(args[1]) == null) {
                        sender.sendMessage(ChatColor.RED + "Invalid player!");
                        Help.showHelp(sender);
                        return true;
                    }
                    GameUtils.playerLeave(plugin, PlayerUtils.getFromAll(args[1]).getUniqueId());
                    return true;
                }
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "You must be a player!");
                    Help.showHelp(sender);
                    return true;
                }
                GameUtils.playerLeave(plugin, ((Player) sender).getUniqueId());
                return true;
            case "checkpoint":
                if (args.length < 2 || (args.length < 3 && args[1].equalsIgnoreCase("remove"))) {
                    sender.sendMessage(ChatColor.RED + "Not enough arguments!");
                    Help.showHelp(sender);
                    return true;
                }
                switch (args[1].toLowerCase()) {
                    case "create":
                        if (!(sender instanceof Player)) {
                            sender.sendMessage(ChatColor.RED + "You must be a player!");
                            Help.showHelp(sender);
                            return true;
                        }
                        plugin.getLocationUtils().setCheckpoint(plugin.getLocationUtils().getCheckpoints().getValue().length + 1, (((Player) sender).getLocation().getZ()));
                        sender.sendMessage(ChatColor.GREEN + "New checkpoint with ID: " + (plugin.getLocationUtils().getCheckpoints().getValue().length + 1)
                                + " and Z: " + (((Player) sender).getLocation().getZ()));
                        return true;
                    case "remove":
                        Double[] array = plugin.getLocationUtils().getCheckpoints().getValue();
                        if (array.length < 1) {
                            sender.sendMessage(ChatColor.RED + "There are no checkpoints!");
                            Help.showHelp(sender);
                            return true;
                        }
                        if (Integer.parseInt(args[2]) > array.length) {
                            sender.sendMessage(ChatColor.RED + "That checkpoint doesn't exist!");
                            Help.showHelp(sender);
                            return true;
                        }
                        Double[] newArray = new Double[array.length - 1];
                        int i = 0;
                        for (Double d : array) {
                            if (!d.toString().equalsIgnoreCase(args[2])) {
                                newArray[i] = d;
                            }
                            i++;
                        }
                        final World world = plugin.getLocationUtils().getCheckpoints().getKey();
                        plugin.getLocationUtils().setCheckpoints(new Entry<>(world, newArray));
                        sender.sendMessage(ChatColor.GREEN + "Successfully removed checkpoint " + args[2] + ", Z = "
                                + plugin.getLocationUtils().getCheckpoint(Integer.parseInt(args[2])));
                        return true;
                    case "list":
                        sender.sendMessage(ChatColor.GOLD + "World: " + plugin.getLocationUtils().getCheckpoints().getKey().getName());
                        int s = 1;
                        for (Double d : plugin.getLocationUtils().getCheckpoints().getValue()) {
                            sender.sendMessage(ChatColor.GOLD + "ID: " + s + ", Z: " + d);
                            s++;
                        }
                        return true;
                    case "nearest":
                        if (!(sender instanceof Player)) {
                            sender.sendMessage(ChatColor.RED + "You must be a player!");
                            Help.showHelp(sender);
                            return true;
                        }
                        Location loc = plugin.getLocationUtils().getNearestCheckpointLocation((Player) sender);
                        sender.sendMessage(ChatColor.GOLD + "Nearest checkpoint: World: " + loc.getWorld().getName() + ", " + loc.getX() + ", " + loc.getY() + ", " + loc.getZ());
                        return true;
                }
                return true;
            case "reload":
                plugin.reloadConfigs();
                sender.sendMessage(ChatColor.GREEN + "Successfully reloaded the config files!");
                return true;
            case "tp":
                UUID[] ranking = plugin.getGame().getFinishedRanking().toArray(new UUID[0]);
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "You must be a player!");
                    Help.showHelp(sender);
                    return true;
                }
                for (int i = 1; i < (ranking.length + 1); i++) {
                    if (i == Integer.parseInt(args[1])) {
                        sender.sendMessage(ChatColor.GREEN + "Teleporting to user: " + Bukkit.getPlayer(ranking[i - 1]).getDisplayName());
                        ((Player) sender).teleport(Bukkit.getPlayer(ranking[i - 1]));
                    }
                }
                return true;
            case "stop":
                if (!RacePlugin.gameRunning) {
                    sender.sendMessage(ChatColor.RED + "The race is already stopped!");
                    return true;
                }
                Bukkit.broadcastMessage(ChatColor.RED + "An admin has force stopped the race!");
                plugin.reset();
                RacePlugin.gameRunning = false;
                return true;
            case "players":
                sender.sendMessage(ChatColor.GREEN + "Players in the race:");
                for (UUID uuid : Racing.getPlayers()) {
                    sender.sendMessage(ChatColor.GOLD + Bukkit.getPlayer(uuid).getDisplayName());
                }
                return true;
            default:
                sender.sendMessage(ChatColor.RED + "Invalid argument!");
                Help.showHelp(sender);
                return true;
        }
    }
}
