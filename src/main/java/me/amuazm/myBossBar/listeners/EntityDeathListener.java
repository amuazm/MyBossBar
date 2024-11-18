package me.amuazm.myBossBar.listeners;

import me.amuazm.myBossBar.MyBossBar;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class EntityDeathListener implements Listener {
    private final MyBossBar plugin;

    public EntityDeathListener(MyBossBar plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        plugin.getBossBarManager().stopTracking(event.getEntity());
    }
}
