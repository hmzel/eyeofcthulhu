package me.zelha.eyeofcthulhu.util;

import me.zelha.eyeofcthulhu.Main;
import me.zelha.eyeofcthulhu.enemies.ParticleEnemy;
import me.zelha.eyeofcthulhu.listeners.HitboxListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.scheduler.BukkitRunnable;

public class Hitbox {

    private final ParticleEnemy enemy;
    private final Location l;
    private double damage;
    private BossBar bar;
    private Slime hitbox;
    private double defense = 0;
    private double defensePercent = 0;

    public Hitbox(ParticleEnemy enemy, Location location, double size, double damage, double maxHealth, String displayName, boolean bossBar) {
        this.enemy = enemy;
        this.damage = damage;
        l = location.clone().add(0, -(size / 3.5), 0);

        createHitBox(location, size, maxHealth, displayName);

        if (bossBar) {
            this.bar = Bukkit.createBossBar(displayName, BarColor.PURPLE, BarStyle.SEGMENTED_10);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (hitbox == null) {
                    cancel();

                    return;
                }

                if (!hitbox.isValid()) {
                    createHitBox(location, size, maxHealth, displayName);
                }

                hitbox.teleport(l.zero().add(location).add(0, -(size / 3.5), 0));

                if (bar != null) {
                    bar.setProgress(hitbox.getHealth() / maxHealth);

                    for (Player p : hitbox.getWorld().getPlayers()) {
                        if (p.getLocation().distanceSquared(l) < 10000) {
                            bar.addPlayer(p);
                        } else {
                            bar.removePlayer(p);
                        }
                    }
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 1);

        HitboxListener.registerHitbox(this);
    }

    private void createHitBox(Location location, double size, double maxHealth, String displayName) {
        hitbox = (Slime) location.getWorld().spawnEntity(l, EntityType.SLIME);

        hitbox.setGravity(false);
        hitbox.setPersistent(true);
        hitbox.setSilent(true);
        hitbox.setSize((int) size);
        hitbox.setMaxHealth(maxHealth);
        hitbox.setHealth(maxHealth);
        hitbox.setInvisible(true);
        hitbox.setCustomName(displayName);
    }

    public void remove(boolean causedByDisable) {
        enemy.onDeath(!causedByDisable);
        hitbox.remove();
        HitboxListener.unregisterHitbox(this);

        if (bar != null) {
            bar.removeAll();
            bar.setVisible(false);
        }

        hitbox = null;
    }

    public boolean sameEntity(Entity e) {
        return hitbox.getUniqueId().equals(e.getUniqueId());
    }

    public void setDefense(double defense) {
        this.defense = defense;
    }

    public void setDefensePercent(double defensePercent) {
        this.defensePercent = defensePercent;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public double getDefense() {
        return defense;
    }

    public double getDefensePercent() {
        return defensePercent;
    }

    public double getDamage() {
        return damage;
    }

    public Slime getSlime() {
        return hitbox;
    }

    public ParticleEnemy getEnemy() {
        return enemy;
    }
}
