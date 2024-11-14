package me.amuazm.myBossBar.listeners;

import me.amuazm.myBossBar.MyBossBar;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class CreatureSpawnListener implements Listener {
    private final MyBossBar plugin;

    public CreatureSpawnListener(MyBossBar plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        // Check if it has tags
        if (event.getEntity().getScoreboardTags().contains("bossBarHealth")) {
            plugin.getBossBarManager().startTracking(event.getEntity());
        }
    }
}
