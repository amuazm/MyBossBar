package me.amuazm.myBossBar.listeners;

import me.amuazm.myBossBar.MyBossBar;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {
    private final MyBossBar plugin;

    public PlayerQuitListener(MyBossBar plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getBossBarManager().stopTracking(event.getPlayer());
    }
}
