package me.zelha.eyeofcthulhu.util;

import me.zelha.eyeofcthulhu.Main;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Wither;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class BossBar {

    private final Wither wither;
    private final BukkitTask task;
    private double health;

    public BossBar(Location location, String name, double maxHealth) {
        health = maxHealth;
        wither = (Wither) location.getWorld().spawnEntity(location, EntityType.WITHER);

        wither.setCustomName(name);
        wither.setAI(false);
        wither.setInvulnerabilityTicks(879);
        wither.setPersistent(true);
        wither.setSilent(true);

        task = new BukkitRunnable() {
            @Override
            public void run() {
                if (health <= 0) {
                    wither.remove();
                    cancel();

                    return;
                }

                wither.teleport(location);
                wither.setHealth(wither.getMaxHealth() * (health / maxHealth));
            }
        }.runTaskTimer(Main.getInstance(), 0, 1);
    }

    public void remove() {
        task.cancel();
        wither.remove();
    }

    public void setHealth(double health) {
        this.health = health;
    }
}
