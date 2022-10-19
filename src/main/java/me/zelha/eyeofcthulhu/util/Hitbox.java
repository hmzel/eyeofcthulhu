package me.zelha.eyeofcthulhu.util;

import hm.zelha.particlesfx.shapers.ParticleSphere;
import me.zelha.eyeofcthulhu.Main;
import me.zelha.eyeofcthulhu.enemies.ParticleEnemy;
import me.zelha.eyeofcthulhu.listeners.HitboxListener;
import net.minecraft.server.v1_8_R3.EntitySlime;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftSlime;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Slime;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;

public class Hitbox {

    private final ParticleEnemy enemy;
    private final Location l;
    private double damage;
    private BossBar bar;
    private Slime hitbox;

    public Hitbox(ParticleEnemy enemy, double size, double damage, double maxHealth, @Nullable BossBar bar) {
        this.enemy = enemy;
        Location location = ((ParticleSphere) enemy.getModel().getShape(0)).getCenter();
        this.l = location.clone().add(0, -(size / 3.5), 0);
        this.damage = damage;
        this.bar = bar;
        this.hitbox = (Slime) location.getWorld().spawnEntity(l, EntityType.SLIME);
        EntitySlime nmsSlime = ((CraftSlime) hitbox).getHandle();
        NBTTagCompound tag = nmsSlime.getNBTTag();

        if (tag == null) tag = new NBTTagCompound();
        if (bar != null) bar.setHealth(maxHealth);

        nmsSlime.c(tag);
        tag.setInt("NoAI", 1);
        tag.setInt("PersistenceRequired", 1);
        tag.setInt("Silent", 1);
        tag.setDouble("Size", size);
        nmsSlime.f(tag);
        hitbox.setMaxHealth(maxHealth);
        hitbox.setHealth(maxHealth);
        hitbox.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, false, false));

        new BukkitRunnable() {
            @Override
            public void run() {
                if (hitbox == null) {
                    cancel();
                    return;
                }

                hitbox.teleport(l.zero().add(location).add(0, -(size / 3.5), 0));

                if (bar != null) bar.setHealth(hitbox.getHealth());
            }
        }.runTaskTimer(Main.getInstance(), 0, 1);

        HitboxListener.registerHitbox(this);
    }

    public void remove() {
        enemy.getModel().stop();
        hitbox.remove();
        HitboxListener.unregisterHitbox(this);

        if (bar != null) bar.remove();

        hitbox = null;
    }

    public boolean sameEntity(Entity e) {
        return hitbox.getUniqueId().equals(e.getUniqueId());
    }

    public void setHealth(int health) {
        hitbox.setHealth(health);

        if (bar != null) bar.setHealth(health);
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public double getBarHealth() {
        return bar.getHealth();
    }

    public double getDamage() {
        return damage;
    }

    public BossBar getBar() {
        return bar;
    }

    public Slime getSlime() {
        return hitbox;
    }
}
