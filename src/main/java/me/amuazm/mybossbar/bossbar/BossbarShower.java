package me.amuazm.mybossbar.bossbar;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import java.util.Collection;
import java.util.function.Consumer;
import me.amuazm.mybossbar.MyBossbar;
import me.amuazm.mybossbar.managers.BossbarManager;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
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
          Collection<Player> nearbyPlayers =
              trackedEntity.getWorld().getNearbyPlayers(trackedEntity.getLocation(), 32);
          Audience audience = Audience.audience(nearbyPlayers);
          if (trackedEntity.isDead()) {
            bossbarManager.stopTracking(trackedEntity);
            audience.hideBossBar(bossbar);
            return;
          }
          bossbar.progress(
              (float) trackedEntity.getHealth()
                  / (float) trackedEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
          audience.showBossBar(bossbar);
        };

    bossbarTask = trackedEntity.getScheduler().runAtFixedRate(plugin, task, null, 1, 1);
  }

  public void cancelBossbarTask() {
    bossbarTask.cancel();
  }
}
