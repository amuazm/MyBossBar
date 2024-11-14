package me.amuazm.myBossBar.bossbar;

import lombok.Getter;
import me.amuazm.myBossBar.MyBossBar;
import org.bukkit.entity.LivingEntity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BossBarManager {
    private final MyBossBar plugin;

    @Getter
    private final Map<LivingEntity, BossBarShower> trackedEntities = new ConcurrentHashMap<>();

    public BossBarManager(MyBossBar plugin) {
        this.plugin = plugin;
    }

    public void startTracking(LivingEntity livingEntity) {
        // Check if entity is already being tracked
        if (isTracking(livingEntity)) {
            return;
        }
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

    public void stopTrackingAll() {
        for (LivingEntity livingEntity : trackedEntities.keySet()) {
            stopTracking(livingEntity);
        }
    }

    public boolean isTracking(LivingEntity livingEntity) {
        for (Map.Entry<LivingEntity, BossBarShower> entry : trackedEntities.entrySet()) {
            if (entry.getKey().getUniqueId().equals(livingEntity.getUniqueId())) {
                return true;
            }
        }
        return false;
    }
}
