package me.amuazm.mybossbar;

import lombok.Getter;
import me.amuazm.mybossbar.commands.MyBossbarCommand;
import me.amuazm.mybossbar.managers.BossbarManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class MyBossbar extends JavaPlugin {
  @Getter private BossbarManager bossbarManager;

  @Override
  public void onEnable() {
    // Plugin startup logic
    initializeManagers();

    getCommand("mybossbar").setExecutor(new MyBossbarCommand(this));
  }

  @Override
  public void onDisable() {
    // Plugin shutdown logic
  }

  private void initializeManagers() {
    bossbarManager = new BossbarManager(this);
  }
}
