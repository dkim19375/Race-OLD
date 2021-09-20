package me.dkim19375.race.commands;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TabCompleterClass implements TabCompleter {

    private final HashMultimap<String, String> completesListMap;

    public TabCompleterClass() {
        completesListMap = HashMultimap.create();
        add("core", "help", "start", "leave", "checkpoint", "tp", "reload", "players", "stop");
        add("checkpoints", "create", "remove", "list", "nearest");
        add("id", "<id>");
        add("rank", "<rank>");
    }

    private void add(String key, String... args) {
        completesListMap.putAll(key, Arrays.asList(args));
    }

    private List<String> getPartial(String token, Iterable<String> collection) {
        return StringUtil.copyPartialMatches(token, collection, new ArrayList<>());
    }

    public
    List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        switch (args.length) {
            case 0: return Lists.newArrayList(completesListMap.get("core"));
            case 1: return getPartial(args[0], completesListMap.get("core"));
            case 2:
                if (args[0].equalsIgnoreCase("checkpoints")) {
                    if (completesListMap.get("core").contains(args[0].toLowerCase())) {
                        return getPartial(args[1], completesListMap.get("checkpoints"));
                    }
                }
                if (args[0].equalsIgnoreCase("tp")) {
                    if (completesListMap.get("core").contains(args[0].toLowerCase())) {
                        return getPartial(args[1], completesListMap.get("rank"));
                    }
                }
            case 3:
                if (args[0].equalsIgnoreCase("remove")) {
                    if (completesListMap.get("core").contains(args[0].toLowerCase())) {
                        return getPartial(args[2], completesListMap.get("id"));
                    }
                }
            default: return ImmutableList.of();
        }
    }
}