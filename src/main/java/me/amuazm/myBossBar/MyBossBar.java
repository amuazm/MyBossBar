package me.amuazm.myBossBar;

import lombok.Getter;
import me.amuazm.myBossBar.bossbar.BossBarManager;
import me.amuazm.myBossBar.commands.MyBossBarCommand;
import me.amuazm.myBossBar.commands.MyBossBarTabCompleter;
import me.amuazm.myBossBar.listeners.CreatureSpawnListener;
import me.amuazm.myBossBar.listeners.PlayerQuitListener;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class MyBossBar extends JavaPlugin {
    private BossBarManager bossBarManager;

    @Override
    public void onEnable() {
        initializeManagers();
        initializeCommands();
        registerListeners();
    }

    @Override
    public void onDisable() {
        bossBarManager.stopTrackingAll();
    }

    private void initializeManagers() {
        bossBarManager = new BossBarManager(this);
    }

    private void initializeCommands() {
        getCommand("mybossbar").setExecutor(new MyBossBarCommand(this));
        getCommand("mybossbar").setTabCompleter(new MyBossBarTabCompleter(this));
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new CreatureSpawnListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), this);
    }
}
