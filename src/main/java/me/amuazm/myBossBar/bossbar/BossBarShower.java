package me.amuazm.myBossBar.bossbar;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import me.amuazm.myBossBar.MyBossBar;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Consumer;

public class BossBarShower {
    private final MyBossBar plugin;
    private final BossBarManager bossBarManager;
    private final LivingEntity trackedEntity;
    private final BossBar bossBar;
    private final ScheduledTask bossBarTask;

    public BossBarShower(MyBossBar plugin, LivingEntity trackedEntity) {
        this.plugin = plugin;
        this.bossBarManager = plugin.getBossBarManager();
        this.trackedEntity = trackedEntity;

        Component customName = trackedEntity.customName();

        // Initialize boss bar.
        bossBar = BossBar.bossBar(
                customName != null ? customName : trackedEntity.name(),
                (float) trackedEntity.getHealth()
                        / (float) trackedEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(),
                BossBar.Color.RED,
                BossBar.Overlay.PROGRESS);

        Consumer<ScheduledTask> task = (scheduledTask) -> {
            Audience allAudience = Audience.audience(Bukkit.getOnlinePlayers());

            if (!trackedEntity.getChunk().isLoaded()) {
                allAudience.hideBossBar(bossBar);
                return;
            }

            Collection<UUID> nearbyPlayerUuids = trackedEntity.getWorld()
                    .getNearbyPlayers(trackedEntity.getLocation(), 50)
                    .stream()
                    .map(Player::getUniqueId)
                    .toList();

            Audience nearbyAudience = allAudience.filterAudience(audience -> {
                return audience instanceof Player player
                        && nearbyPlayerUuids.contains(player.getUniqueId());
            });

            Audience otherAudience = allAudience.filterAudience(audience -> {
                return audience instanceof Player
                        && !nearbyPlayerUuids.contains(((Player) audience).getUniqueId());
            });

            // Ensure players out of range don't see the boss bar.
            otherAudience.hideBossBar(bossBar);

            // Show health of entity in bossbar to nearby players.
            bossBar.progress((float) trackedEntity.getHealth()
                    / (float) trackedEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());

            nearbyAudience.showBossBar(bossBar);
        };

        bossBarTask = trackedEntity.getScheduler().runAtFixedRate(plugin, task, null, 1, 1);
    }

    public void stop() {
        Audience.audience(Bukkit.getOnlinePlayers()).hideBossBar(bossBar);
        bossBarTask.cancel();
    }
}
