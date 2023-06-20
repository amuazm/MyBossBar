package me.amuazm.mybossbar.managers;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import me.amuazm.mybossbar.MyBossbar;
import me.amuazm.mybossbar.bossbar.BossbarShower;
import org.bukkit.entity.LivingEntity;

public class BossbarManager {
  private final MyBossbar plugin;

  @Getter
  private final Map<LivingEntity, BossbarShower> trackedEntities = new ConcurrentHashMap<>();

  public BossbarManager(MyBossbar plugin) {
    this.plugin = plugin;
  }

  public void startTracking(LivingEntity livingEntity) {
    BossbarShower bossbarShower = new BossbarShower(plugin, livingEntity);
    trackedEntities.put(livingEntity, bossbarShower);
  }

  public void stopTracking(LivingEntity livingEntity) {
    BossbarShower bossbarShower = trackedEntities.get(livingEntity);
    if (bossbarShower == null) {
      return;
    }
    bossbarShower.cancelBossbarTask();
    trackedEntities.remove(livingEntity);
  }
}
