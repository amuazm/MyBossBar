package me.amuazm.myBossBar.listeners;

import me.amuazm.myBossBar.MyBossBar;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityDamagedByEntityListener implements Listener {
    private final MyBossBar plugin;

    public EntityDamagedByEntityListener(MyBossBar plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof LivingEntity livingEntity) {
            if (livingEntity.getScoreboardTags().contains("bossBarHealth")) {
                plugin.getBossBarManager().startTracking(livingEntity);
            }
        }

        if (event.getDamager() instanceof LivingEntity livingEntity) {
            if (livingEntity.getScoreboardTags().contains("bossBarHealth")) {
                plugin.getBossBarManager().startTracking(livingEntity);
            }
        }
    }
}
