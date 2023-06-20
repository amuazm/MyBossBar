package me.amuazm.mybossbar.managers;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import me.amuazm.mybossbar.MyBossBar;
import me.amuazm.mybossbar.bossbar.BossBarShower;
import org.bukkit.entity.LivingEntity;

public class BossBarManager {
  private final MyBossBar plugin;

  @Getter
  private final Map<LivingEntity, BossBarShower> trackedEntities = new ConcurrentHashMap<>();

  public BossBarManager(MyBossBar plugin) {
    this.plugin = plugin;
  }

  public void startTracking(LivingEntity livingEntity) {
    BossBarShower bossBarShower = new BossBarShower(plugin, livingEntity);
    trackedEntities.put(livingEntity, bossBarShower);
  }

  public void stopTracking(LivingEntity livingEntity) {
    BossBarShower bossBarShower = trackedEntities.get(livingEntity);
    if (bossBarShower == null) {
      return;
    }
    bossBarShower.stop();
    trackedEntities.remove(livingEntity);
  }
}
