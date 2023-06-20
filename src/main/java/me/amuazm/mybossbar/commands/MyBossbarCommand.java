package me.amuazm.mybossbar.commands;

import java.util.UUID;
import java.util.function.Predicate;
import me.amuazm.mybossbar.MyBossbar;
import me.amuazm.mybossbar.managers.BossbarManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// nearest
// nearest <nontracked &| nonplayer>

public class MyBossbarCommand implements CommandExecutor {
  private final MyBossbar plugin;
  private final BossbarManager bossbarManager;

  public MyBossbarCommand(MyBossbar plugin) {
    this.plugin = plugin;
    this.bossbarManager = plugin.getBossbarManager();
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
      if (bossbarManager.getTrackedEntities().containsKey(livingEntity)) {
        senderPlayer.sendMessage("The entity you are looking at is already being tracked!");
        return;
      }
      // Add the entity to the tracked entities
      bossbarManager.startTracking(livingEntity);
      return;
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
      bossbarManager.stopTracking(livingEntity);
      return;
    }

    // /mbsb remove <uuid>
    if (args.length == 2) {
      // Get the entity from the uuid
      Entity entity = senderPlayer.getWorld().getEntity(UUID.fromString(args[1]));
      if (entity == null) {
        senderPlayer.sendMessage("The entity with the given uuid does not exist!");
        return;
      }
      if (!(entity instanceof LivingEntity livingEntity)) {
        senderPlayer.sendMessage("The entity with the given uuid is not a living entity!");
        return;
      }
      // Remove the entity from the tracked entities
      bossbarManager.stopTracking(livingEntity);
      return;
    }

    senderPlayer.sendMessage("Invalid arguments!");
  }

  private void list(Player senderPlayer, String[] args) {
    // /mbsb list
    if (args.length == 1) {
      // Get the list of tracked entities
      bossbarManager
          .getTrackedEntities()
          .forEach(
              (livingEntity, bossbarShower) -> {
                senderPlayer.sendMessage(livingEntity.getName());
                // TELEPORT
                Component tools =
                    MiniMessage.miniMessage()
                        .deserialize(
                            "<click:run_command:'/minecraft:tp "
                                + livingEntity.getUniqueId()
                                + "'>[Teleport]</click>");

                // STOP TRACKING
                tools =
                    tools.append(
                        MiniMessage.miniMessage()
                            .deserialize(
                                " <click:run_command:'/mbsb remove "
                                    + livingEntity.getUniqueId()
                                    + "'>[Stop Tracking]</click>"));

                // KILL
                tools =
                    tools.append(
                        MiniMessage.miniMessage()
                            .deserialize(
                                " <click:run_command:'/minecraft:kill "
                                    + livingEntity.getUniqueId()
                                    + "'>[Kill]</click>"));

                senderPlayer.sendMessage(tools);
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
