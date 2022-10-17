package me.zelha.eyeofcthulhu.listeners;

import hm.zelha.particlesfx.particles.ParticleCloud;
import hm.zelha.particlesfx.particles.parents.Particle;
import me.zelha.eyeofcthulhu.Main;
import me.zelha.eyeofcthulhu.util.Hitbox;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class HitboxListener implements Listener {

    private static final List<Hitbox> hitboxes = new ArrayList<>();
    private static final Particle deadCloud = new ParticleCloud(0.2, 0.2, 0.2, 25);

    @EventHandler
    public void correctDamage(EntityDamageByEntityEvent e) {
        for (Hitbox box : hitboxes) {
            if (box.sameEntity(e.getDamager())) {
                e.setDamage(box.getDamage());
            }
        }
    }

    @EventHandler
    public void noNormalDamage(EntityDamageEvent e) {
        for (Hitbox box : hitboxes) {
            if (box.sameEntity(e.getEntity())) {
                if (e.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
                    e.setCancelled(true);
                }

                if (((Slime) e.getEntity()).getHealth() - e.getFinalDamage() <= 0) {
                    e.setCancelled(true);

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            ExperienceOrb orb = (ExperienceOrb) box.getSlime().getWorld().spawnEntity(box.getSlime().getLocation(), EntityType.EXPERIENCE_ORB);

                            deadCloud.display(box.getSlime().getLocation());
                            orb.setExperience(5);
                        }
                    }.runTaskLater(Main.getInstance(), 10);

                    box.remove();
                }
            }
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        for (Hitbox box : hitboxes) {
            if (box.sameEntity(e.getEntity())) {
                e.getDrops().clear();

                if (box.getBar() != null) e.setDroppedExp(500);
            }
        }
    }

    public static void registerHitbox(Hitbox box) {
        hitboxes.add(box);
    }

    public static void unregisterHitbox(Hitbox box) {
        hitboxes.remove(box);
    }

    public static void onDisable() {
        for (Hitbox box : hitboxes) {
            box.remove();
        }
    }
}
