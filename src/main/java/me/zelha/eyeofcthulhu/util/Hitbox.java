package me.zelha.eyeofcthulhu.util;

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
        hitbox = (Slime) location.getWorld().spawnEntity(l, EntityType.SLIME);
        EntitySlime nmsSlime = ((CraftSlime) hitbox).getHandle();
        NBTTagCompound tag = nmsSlime.getNBTTag();

        if (bossBar) {
            this.bar = new BossBar(location, displayName, maxHealth);
        }

        if (tag == null) {
            tag = new NBTTagCompound();
        }

        nmsSlime.c(tag);
        tag.setInt("NoAI", 1);
        tag.setInt("PersistenceRequired", 1);
        tag.setInt("Silent", 1);
        tag.setDouble("Size", size);
        nmsSlime.f(tag);
        hitbox.setMaxHealth(maxHealth);
        hitbox.setHealth(maxHealth);
        hitbox.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, false, false));
        hitbox.setCustomName(displayName);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (hitbox == null) {
                    cancel();

                    return;
                }

                hitbox.teleport(l.zero().add(location).add(0, -(size / 3.5), 0));

                if (bar != null) {
                    bar.setHealth(hitbox.getHealth());
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 1);

        HitboxListener.registerHitbox(this);
    }

    public void remove(boolean causedByDisable) {
        enemy.onDeath(!causedByDisable);
        hitbox.remove();
        HitboxListener.unregisterHitbox(this);

        if (bar != null) {
            bar.remove();
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
