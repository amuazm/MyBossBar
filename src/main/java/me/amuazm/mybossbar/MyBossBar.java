package me.amuazm.mybossbar;

import lombok.Getter;
import me.amuazm.mybossbar.commands.MyBossBarCommand;
import me.amuazm.mybossbar.managers.BossBarManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class MyBossBar extends JavaPlugin {
  @Getter private BossBarManager bossBarManager;

  @Override
  public void onEnable() {
    // Plugin startup logic
    initializeManagers();

    getCommand("mybossbar").setExecutor(new MyBossBarCommand(this));
  }

  @Override
  public void onDisable() {
    // Plugin shutdown logic
  }

  private void initializeManagers() {
    bossBarManager = new BossBarManager(this);
  }
}
