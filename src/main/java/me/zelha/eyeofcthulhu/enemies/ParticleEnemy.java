package me.zelha.eyeofcthulhu.enemies;

import hm.zelha.particlesfx.shapers.ParticleSphere;
import hm.zelha.particlesfx.shapers.parents.Shape;
import hm.zelha.particlesfx.util.ParticleSFX;
import hm.zelha.particlesfx.util.ParticleShapeCompound;
import me.zelha.eyeofcthulhu.Main;
import me.zelha.eyeofcthulhu.util.Hitbox;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public abstract class ParticleEnemy {

    protected static final ThreadLocalRandom rng = ThreadLocalRandom.current();
    protected final ParticleShapeCompound model = new ParticleShapeCompound();
    protected Hitbox hitbox;
    protected EntityLiving target = null;
    private BukkitTask despawner;

    protected ParticleEnemy() {
    }

    public void target(Entity e) {
        target = (EntityLiving) ((CraftEntity) e).getHandle();
    }

    public void onDeath() {
        model.stop();
        despawner.cancel();
    }

    public abstract void onHit(Entity attacker);

    protected abstract void startAI();

    protected void startDespawner(Location location) {
        despawner = new BukkitRunnable() {

            private final Location l = location.clone();

            @Override
            public void run() {
                double closest = 9999;

                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (!p.getWorld().equals(location.getWorld())) continue;

                    EntityPlayer nmsP = ((CraftPlayer) p).getHandle();

                    l.zero().add(nmsP.locX, nmsP.locY, nmsP.locZ);

                    if (l.distance(location) < closest) {
                        closest = l.distance(location);
                    }
                }

                if (closest > 100) {
                    hitbox.remove();
                }
            }
        }.runTaskTimer(Main.getInstance(), 400, 20);
    }

    protected void findTarget(double radius) {
        Location center = ((ParticleSphere) model.getShape(0)).getCenter();
        Entity target = null;

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!center.getWorld().equals(p.getWorld())) continue;
            if (p.getGameMode() != GameMode.SURVIVAL) continue;

            if (target == null) {
                target = p;

                continue;
            }

            if (p.getLocation().distance(center) < target.getLocation().distance(center)) {
                target = p;
            }
        }

        if (target != null && target.getLocation().distance(center) > radius) {
            target = null;
        }

        if (target == null) {
            List<Entity> nearbyEntities = (ArrayList<Entity>) center.getWorld().getNearbyEntities(center, radius, radius, radius);

            if (!nearbyEntities.isEmpty()) {
                for (int i = 0; i < nearbyEntities.size(); i++) {
                    Entity e;

                    if (nearbyEntities.size() > 1) {
                        e = nearbyEntities.get(rng.nextInt(nearbyEntities.size() - 1));
                    } else {
                        e = nearbyEntities.get(0);
                    }

                    if (e instanceof Player) continue;
                    if (e instanceof Slime) continue;
                    if (e instanceof Monster) continue;
                    if (!(e instanceof LivingEntity)) continue;

                    target = e;
                    break;
                }
            }
        }

        if (target == null) return;

        this.target = (EntityLiving) ((CraftEntity) target).getHandle();
    }

    protected void faceAroundBody(Location l) {
        ParticleSphere body = (ParticleSphere) model.getShape(0);
        Shape tendrils = model.getShape(1);
        double[] direction = ParticleSFX.getDirection(l, body.getCenter());
        double pitchInc, yawInc;
        double increase = 15;

        if (body.getPitch() + increase <= direction[0]) {
            pitchInc = increase;
        } else if (body.getPitch() - increase >= direction[0]) {
            pitchInc = -increase;
        } else {
            pitchInc = direction[0] - body.getPitch();
        }

        if (body.getYaw() + increase <= direction[1]) {
            yawInc = increase;
        } else if (body.getYaw() - increase >= direction[1]) {
            yawInc = -increase;
        } else {
            yawInc = direction[1] - body.getYaw();
        }

        body.rotate(pitchInc, yawInc, 0);
        tendrils.setAroundRotation(body.getCenter(), body.getPitch(), body.getYaw(), 0);
        tendrils.setRotation(body.getPitch(), body.getYaw(), 0);
    }

    protected void damageNearby(Location location, double radius) {
        for (Entity e : location.getWorld().getNearbyEntities(location, radius, radius, radius)) {
            if (!e.getUniqueId().equals(target.getUniqueID())) {
                if (e instanceof Player) continue;
                if (e instanceof Monster) continue;
                if (e instanceof Slime) continue;
                if (!(e instanceof LivingEntity)) continue;
            }

            ((LivingEntity) e).damage(hitbox.getDamage(), hitbox.getSlime());
        }
    }

    public ParticleShapeCompound getModel() {
        return model;
    }
}
