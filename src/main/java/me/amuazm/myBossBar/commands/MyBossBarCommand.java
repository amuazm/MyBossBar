package me.amuazm.myBossBar.commands;

import me.amuazm.myBossBar.MyBossBar;
import me.amuazm.myBossBar.bossbar.BossBarManager;
import me.amuazm.myBossBar.bossbar.BossBarShower;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Enemy;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;

public class MyBossBarCommand implements CommandExecutor {
    private final MyBossBar plugin;
    private final BossBarManager bossBarManager;

    public MyBossBarCommand(MyBossBar plugin) {
        this.plugin = plugin;
        this.bossBarManager = plugin.getBossBarManager();
    }

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args) {
        if (!(sender instanceof Player senderPlayer)) {
            return true;
        }

        // /mbsb
        if (args.length == 0) {
            senderPlayer.sendMessage("Hello, world!");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "track" -> {
                track(senderPlayer, args);
            }
            case "remove" -> {
                remove(senderPlayer, args);
            }
            case "list" -> {
                list(senderPlayer, args);
            }
            default -> {
                senderPlayer.sendMessage("Invalid arguments!");
            }
        }

        return true;
    }

    private void track(Player senderPlayer, String[] args) {
        // /mbsb track
        if (args.length == 1) {
            // Raytrace the entity if any the player is looking at.
            Entity entity = getRayTracedEntity(senderPlayer);
            if (entity == null) {
                senderPlayer.sendMessage("You are not looking at any entity!");
                return;
            }
            if (!(entity instanceof LivingEntity livingEntity)) {
                senderPlayer.sendMessage("The entity you are looking at is not a living entity!");
                return;
            }
            // Add the entity to the tracked entities.
            bossBarManager.startTracking(livingEntity);
            return;
        }

        // /mbsb track self
        if (args.length == 2) {
            switch (args[1].toLowerCase()) {
                case "self" -> {
                    // Add the sender to the tracked entities.
                    bossBarManager.startTracking(senderPlayer);
                    return;
                }
            }
        }

        // /mbsb track player <name>
        if (args.length == 3) {
            switch (args[1].toLowerCase()) {
                case "player" -> {
                    // Get the player from the name.
                    Player player = senderPlayer.getServer().getPlayer(args[2]);
                    if (player == null) {
                        senderPlayer.sendMessage("The player with the given name is not online!");
                        return;
                    }
                    // Add the player to the tracked entities.
                    bossBarManager.startTracking(player);
                    return;
                }
            }
        }

        // /mbsb track nearest <&| nontracked &| nonplayer &| enemy>
        if (args.length >= 2) {
            switch (args[1].toLowerCase()) {
                case "nearest" -> {
                    List<String> argsList = Arrays.asList(args);
                    // Add the entity to the tracked entities.
                    senderPlayer.getWorld().getNearbyLivingEntities(senderPlayer.getLocation(), 50).stream()
                            .filter(
                                    livingEntity -> {
                                        if (livingEntity.getUniqueId().equals(senderPlayer.getUniqueId())) {
                                            return false;
                                        }
                                        if (argsList.contains("nontracked")) {
                                            if (bossBarManager.isTracking(livingEntity)) {
                                                return false;
                                            }
                                        }
                                        if (argsList.contains("nonplayer")) {
                                            if (livingEntity instanceof Player) {
                                                return false;
                                            }
                                        }
                                        if (argsList.contains("player")) {
                                            if (!(livingEntity instanceof Player)) {
                                                return false;
                                            }
                                        }
                                        if (argsList.contains("enemy")) {
                                            if (!(livingEntity instanceof Enemy)) {
                                                return false;
                                            }
                                        }
                                        return true;
                                    })
                            .min(
                                    (livingEntity1, livingEntity2) -> {
                                        Location location1 = livingEntity1.getLocation();
                                        Location location2 = livingEntity2.getLocation();
                                        return Double.compare(
                                                location1.distanceSquared(senderPlayer.getLocation()),
                                                location2.distanceSquared(senderPlayer.getLocation()));
                                    })
                            .ifPresentOrElse(
                                    bossBarManager::startTracking,
                                    () -> senderPlayer.sendMessage("No entity found!"));
                    return;
                }
            }
        }

        senderPlayer.sendMessage("Invalid arguments!");
    }

    private void remove(Player senderPlayer, String[] args) {
        // /mbsb remove
        if (args.length == 1) {
            // Raytrace the entity if any the player is looking at
            Entity entity = getRayTracedEntity(senderPlayer);
            if (entity == null) {
                senderPlayer.sendMessage("You are not looking at any entity!");
                return;
            }
            if (!(entity instanceof LivingEntity livingEntity)) {
                senderPlayer.sendMessage("The entity you are looking at is not a living entity!");
                return;
            }
            // Remove the entity from the tracked entities
            bossBarManager.stopTracking(livingEntity);
            return;
        }

        // /mbsb remove all
        if (args.length == 2) {
            switch (args[1].toLowerCase()) {
                case "all" -> {
                    // Remove all tracked entities
                    bossBarManager.stopTrackingAll();
                    return;
                }
            }
        }

        // /mbsb remove uuid <uuid>
        if (args.length == 3) {
            // Get the entity from the uuid
            Entity entity = senderPlayer.getWorld().getEntity(UUID.fromString(args[2]));
            if (entity == null) {
                senderPlayer.sendMessage("The entity with the given uuid does not exist!");
                return;
            }
            if (!(entity instanceof LivingEntity livingEntity)) {
                senderPlayer.sendMessage("The entity with the given uuid is not a living entity!");
                return;
            }
            // Remove the entity from the tracked entities
            bossBarManager.stopTracking(livingEntity);
            return;
        }

        senderPlayer.sendMessage("Invalid arguments!");
    }

    private void list(Player senderPlayer, String[] args) {
        // /mbsb list
        if (args.length == 1) {
            // Get the list of tracked entities
            Map<LivingEntity, BossBarShower> trackedEntities = bossBarManager.getTrackedEntities();

            if (trackedEntities.isEmpty()) {
                senderPlayer.sendMessage("No entities are being tracked!");
                return;
            }

            trackedEntities.forEach(
                    (livingEntity, bossBarShower) -> {
                        livingEntity.getScheduler().run(plugin, scheduledTask -> {
                            senderPlayer.sendMessage(livingEntity.getName());
                            // TELEPORT
                            Component tools = MiniMessage.miniMessage().deserialize(
                                    "<click:run_command:'/minecraft:tp "
                                            + livingEntity.getUniqueId()
                                            + "'>[Teleport]</click>");

                            // KILL
                            tools = tools.append(MiniMessage.miniMessage().deserialize(
                                    " <click:run_command:'/minecraft:kill "
                                            + livingEntity.getUniqueId()
                                            + "'>[Kill]</click>"));

                            // STOP TRACKING
                            tools = tools.append(MiniMessage.miniMessage().deserialize(
                                    " <click:run_command:'/mbsb remove uuid "
                                            + livingEntity.getUniqueId()
                                            + "'>[Stop Tracking]</click>"));

                            senderPlayer.sendMessage(tools);
                        }, null);
                    });
            return;
        }

        senderPlayer.sendMessage("Invalid arguments!");
    }

    private @Nullable Entity getRayTracedEntity(Player player) {
        final Location playerEyeLocation = player.getEyeLocation();
        final Vector playerDirection = playerEyeLocation.getDirection();
        final Predicate<Entity> filter = entity -> (entity != player);
        final RayTraceResult rayTraceResult;
        // Get ray
        rayTraceResult =
                player.getWorld().rayTraceEntities(playerEyeLocation, playerDirection, 50, 1.2, filter);
        // Get Entity
        return rayTraceResult != null ? rayTraceResult.getHitEntity() : null;
    }
}
