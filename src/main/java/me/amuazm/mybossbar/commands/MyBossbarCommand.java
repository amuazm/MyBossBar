package me.amuazm.mybossbar.commands;

import java.util.function.Predicate;
import me.amuazm.mybossbar.MyBossbar;
import me.amuazm.mybossbar.managers.BossbarManager;
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

    senderPlayer.sendMessage("Invalid arguments!");
  }

  private void list(Player senderPlayer, String[] args) {
    // /mbsb list
    if (args.length == 1) {
      // Get the list of tracked entities
      senderPlayer.sendMessage(bossbarManager.getTrackedEntities().toString());
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
        player.getWorld().rayTraceEntities(playerEyeLocation, playerDirection, 50, 2, filter);
    // Get Entity
    return rayTraceResult != null ? rayTraceResult.getHitEntity() : null;
  }
}
