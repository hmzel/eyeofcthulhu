package me.zelha.eyeofcthulhu.listeners;

import hm.zelha.particlesfx.particles.ParticleCloud;
import hm.zelha.particlesfx.particles.parents.Particle;
import me.zelha.eyeofcthulhu.Main;
import me.zelha.eyeofcthulhu.util.Hitbox;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

import static org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import static org.bukkit.event.entity.EntityDamageEvent.DamageModifier.ARMOR;

public class HitboxListener implements Listener {

    private static final List<Hitbox> hitboxes = new ArrayList<>();
    private final Particle deadCloud = new ParticleCloud(0.2, 0.2, 0.2, 25);

    @EventHandler(ignoreCancelled = true)
    public void correctDamage(EntityDamageByEntityEvent e) {
        Entity attacker = e.getDamager();
        Entity damaged = e.getEntity();

        for (Hitbox box : hitboxes) {
            if (box.sameEntity(attacker)) {
                e.setDamage(box.getDamage());
                return;
            }

            if (box.sameEntity(damaged)) {
                e.setDamage(ARMOR, -box.getDefense());
                e.setDamage(ARMOR, e.getDamage(ARMOR) - (e.getDamage() * (box.getDefensePercent() / 100)));

                if (attacker instanceof Projectile && ((Projectile) attacker).getShooter() instanceof Entity) {
                    box.getEnemy().onHit((Entity) ((Projectile) attacker).getShooter(), e.getFinalDamage());
                    return;
                }

                box.getEnemy().onHit(attacker, e.getFinalDamage());
                return;
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void noNormalDamage(EntityDamageEvent e) {
        Entity entity = e.getEntity();

        for (Hitbox box : hitboxes.toArray(new Hitbox[0])) {
            if (!box.sameEntity(entity)) continue;

            if (e.getCause() == DamageCause.SUFFOCATION || e.getCause() == DamageCause.DROWNING) {
                e.setCancelled(true);
            }

            if (((Slime) entity).getHealth() - e.getFinalDamage() <= 0) {
                e.setCancelled(true);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        ExperienceOrb orb = (ExperienceOrb) entity.getWorld().spawnEntity(entity.getLocation(), EntityType.EXPERIENCE_ORB);

                        deadCloud.display(entity.getLocation());
                        orb.setExperience(5);
                    }
                }.runTaskLater(Main.getInstance(), 10);

                box.remove(false);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void bypassImmunityFrames(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Slime)) return;

        boolean isHitbox = false;

        for (Hitbox box : hitboxes) {
            if (box.sameEntity(e.getEntity())) {
                isHitbox = true;
                break;
            }
        }

        if (!isHitbox) return;

        LivingEntity entity = (LivingEntity) e.getEntity();

        entity.setMaximumNoDamageTicks(0);

        if (!(entity.getLastDamageCause() instanceof EntityDamageByEntityEvent)) return;

        Entity attacker = ((EntityDamageByEntityEvent) entity.getLastDamageCause()).getDamager();

        if (attacker instanceof Projectile && ((Projectile) attacker).getShooter() instanceof LivingEntity) {
            attacker = (Entity) ((Projectile) attacker).getShooter();
        }

        if (!(attacker instanceof LivingEntity)) return;
        if (!attacker.equals(e.getDamager())) return;

        if (((CraftLivingEntity) entity).getHandle().hurtTicks > 0) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void preventIFrameBypass(EntityDamageEvent e) {
        if (e instanceof EntityDamageByEntityEvent) return;
        if (!(e.getEntity() instanceof Slime)) return;

        boolean isHitbox = false;

        for (Hitbox box : hitboxes) {
            if (box.sameEntity(e.getEntity())) {
                isHitbox = true;
                break;
            }
        }

        if (!isHitbox) return;

        if (((CraftLivingEntity) e.getEntity()).getHandle().hurtTicks > 0) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        for (Hitbox box : hitboxes) {
            if (!box.sameEntity(e.getEntity())) continue;

            e.getDrops().clear();
        }
    }

    public static void registerHitbox(Hitbox box) {
        hitboxes.add(box);
    }

    public static void unregisterHitbox(Hitbox box) {
        hitboxes.remove(box);
    }

    public static List<Hitbox> getHitboxes() {
        return hitboxes;
    }

    public static void onDisable() {
        for (Hitbox box : hitboxes.toArray(new Hitbox[0])) {
            box.remove(true);
        }
    }
}
