package me.amuazm.mybossbar.bossbar;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Consumer;
import me.amuazm.mybossbar.MyBossbar;
import me.amuazm.mybossbar.managers.BossbarManager;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class BossbarShower {
  private final MyBossbar plugin;
  private final BossbarManager bossbarManager;
  private final LivingEntity trackedEntity;
  private final BossBar bossbar;
  private final ScheduledTask bossbarTask;

  public BossbarShower(MyBossbar plugin, LivingEntity trackedEntity) {
    this.plugin = plugin;
    this.bossbarManager = plugin.getBossbarManager();
    this.trackedEntity = trackedEntity;

    bossbar =
        BossBar.bossBar(
            Component.text(trackedEntity.getName()),
            (float) trackedEntity.getHealth()
                / (float) trackedEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(),
            BossBar.Color.RED,
            BossBar.Overlay.PROGRESS);

    Consumer<ScheduledTask> task =
        (scheduledTask) -> {
          // Show health in bossbar to nearby players
          Audience allAudience = Audience.audience(Bukkit.getOnlinePlayers());
          Collection<UUID> nearbyPlayerUuids =
              trackedEntity.getWorld().getNearbyPlayers(trackedEntity.getLocation(), 32).stream()
                  .map(Player::getUniqueId)
                  .toList();
          Audience bossbarAudience =
              allAudience.filterAudience(
                  audience ->
                      audience instanceof Player player
                          && nearbyPlayerUuids.contains(player.getUniqueId()));
          Audience otherAudience =
              allAudience.filterAudience(
                  audience ->
                      audience instanceof Player
                          && !nearbyPlayerUuids.contains(((Player) audience).getUniqueId()));
          otherAudience.hideBossBar(bossbar);

          if (!trackedEntity.isValid() || !(trackedEntity.getChunk().isLoaded())) {
            bossbarManager.stopTracking(trackedEntity);
            bossbarAudience.hideBossBar(bossbar);
            return;
          }

          bossbar.progress(
              (float) trackedEntity.getHealth()
                  / (float) trackedEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
          bossbarAudience.showBossBar(bossbar);
        };

    bossbarTask = trackedEntity.getScheduler().runAtFixedRate(plugin, task, null, 1, 1);
  }

  public void stop() {
    Audience.audience(Bukkit.getOnlinePlayers()).hideBossBar(bossbar);
    bossbarTask.cancel();
  }
}
