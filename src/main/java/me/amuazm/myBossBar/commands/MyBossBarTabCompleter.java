package me.amuazm.myBossBar.commands;

import me.amuazm.myBossBar.MyBossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class MyBossBarTabCompleter implements TabCompleter {
    private final MyBossBar plugin;

    public MyBossBarTabCompleter(MyBossBar plugin) {
        this.plugin = plugin;
    }

    @Override
    public @Nullable List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args) {
        // /mbsb track
        // /mbsb remove
        // /mbsb list
        if (args.length == 1) {
            return List.of("track", "remove", "list");
        }

        // /mbsb track self
        // /mbsb track player
        // /mbsb track nearest
        // /mbsb remove all
        if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "track" -> {
                    return List.of("self", "player", "nearest");
                }
                case "remove" -> {
                    return List.of("all");
                }
            }
        }

        // /mbsb track nearest <&| nontracked &| nonplayer &| enemy>
        if (args.length > 2) {
            switch (args[0].toLowerCase()) {
                case "track" -> {
                    switch (args[1].toLowerCase()) {
                        case "nearest" -> {
                            List<String> argsList = Arrays.asList(args);
                            List<String> options = List.of("nontracked", "nonplayer", "player", "enemy");
                            return options.stream().filter(option -> !argsList.contains(option)).toList();
                        }
                    }
                }
            }
        }

        // /mbsb track player <name>
        if (args.length == 3) {
            switch (args[0].toLowerCase()) {
                case "track" -> {
                    switch (args[1].toLowerCase()) {
                        case "player" -> {
                            return null;
                        }
                    }
                }
            }
        }

        return List.of();
    }
}
