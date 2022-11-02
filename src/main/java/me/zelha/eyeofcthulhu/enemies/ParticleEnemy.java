package me.zelha.eyeofcthulhu.enemies;

import hm.zelha.particlesfx.shapers.ParticleSphere;
import hm.zelha.particlesfx.shapers.parents.Shape;
import hm.zelha.particlesfx.util.LVMath;
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
import org.bukkit.util.Vector;

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

    public void onDeath(boolean animate) {
        model.stop();
        despawner.cancel();
    }

    public abstract void onHit(Entity attacker, double damage);

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
                    hitbox.remove(true);
                }
            }
        }.runTaskTimer(Main.getInstance(), 400, 20);
    }

    protected void findTarget(double radius) {
        Location center = ((ParticleSphere) model.getShape(0)).getCenter();
        Entity target = null;

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!center.getWorld().equals(p.getWorld())) continue;
            //if (p.getGameMode() != GameMode.SURVIVAL) continue;

            double distance = p.getLocation().distance(center);

            if (distance > radius) continue;

            if (target == null) {
                target = p;

                continue;
            }

            if (distance < target.getLocation().distance(center)) {
                target = p;
            }
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

    protected BukkitTask runAway() {
        return new BukkitRunnable() {

            private final Location locationHelper = ((ParticleSphere) model.getShape(0)).getCenter().clone();
            private final Vector vectorHelper = new Vector(0, 0, 0);
            private boolean init = false;

            @Override
            public void run() {
                if (!init) {
                    init = true;

                    locationHelper.zero();
                    locationHelper.add(getLocation());
                    locationHelper.add(rng.nextInt(100) - 50, 150, rng.nextInt(100) - 50);
                    LVMath.subtractToVector(vectorHelper, locationHelper, getLocation());
                    vectorHelper.normalize().multiply(0.25);
                }

                locationHelper.add(vectorHelper);
                model.move(vectorHelper);
                faceAroundBody(locationHelper);
            }
        }.runTaskTimer(Main.getInstance(), 0, 1);
    }

    protected void faceAroundBody(Location l) {
        ParticleSphere body = (ParticleSphere) model.getShape(0);
        Shape tendrils = model.getShape(1);
        double[] direction = ParticleSFX.getDirection(l, body.getCenter());
        double pitchInc, yawInc;
        double increase = 15;
        double pitch = body.getPitch();
        double yaw = body.getYaw();
        double wantedPitch = direction[0];
        double wantedYaw = direction[1];
        double difference = Math.abs(yaw - wantedYaw);

        if (pitch + increase <= wantedPitch) {
            pitchInc = increase;
        } else if (pitch - increase >= wantedPitch) {
            pitchInc = -increase;
        } else {
            pitchInc = wantedPitch - pitch;
        }

        if (yaw + (360 - wantedYaw) < difference) {
            yawInc = -increase;
        } else if (wantedYaw + (360 - yaw) < difference) {
            yawInc = increase;
        } else if (yaw + increase <= wantedYaw) {
            yawInc = increase;
        } else if (yaw - increase >= wantedYaw) {
            yawInc = -increase;
        } else {
            yawInc = wantedYaw - yaw;
        }

        if (yaw + yawInc > 360) {
            yawInc = yawInc - 360;
        }

        if (yaw + yawInc < 0) {
            yawInc = yawInc + 360;
        }

        body.rotate(pitchInc, yawInc, 0);
        tendrils.setAroundRotation(body.getCenter(), body.getPitch(), body.getYaw(), 0);
        tendrils.setRotation(body.getPitch(), body.getYaw(), 0);
    }

    protected void damageNearby(Location location, double radius) {
        for (Entity e : location.getWorld().getNearbyEntities(location, radius, radius, radius)) {
            if (e instanceof Player) continue;

            if (!e.getUniqueId().equals(target.getUniqueID())) {
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

    public Location getLocation() {
        return ((ParticleSphere) model.getShape(0)).getCenter();
    }
}
