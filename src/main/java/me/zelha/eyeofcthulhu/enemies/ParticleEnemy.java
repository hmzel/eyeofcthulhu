package me.zelha.eyeofcthulhu.enemies;

import hm.zelha.particlesfx.shapers.ParticleSphere;
import hm.zelha.particlesfx.shapers.parents.Shape;
import hm.zelha.particlesfx.util.LVMath;
import hm.zelha.particlesfx.util.ParticleSFX;
import hm.zelha.particlesfx.util.ParticleShapeCompound;
import me.zelha.eyeofcthulhu.Main;
import me.zelha.eyeofcthulhu.util.Hitbox;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
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
    protected LivingEntity target = null;
    protected Hitbox hitbox;
    private BukkitTask despawner;

    protected ParticleEnemy() {
    }

    public void onDeath(boolean animate) {
        model.stop();
        despawner.cancel();
    }

    protected abstract void startAI();

    public abstract void onHit(Entity attacker, double damage);

    protected void startDespawner(Location location) {
        despawner = new BukkitRunnable() {

            private final Location l = location.clone();
            private boolean doDistanceCheck = false;
            private int i = 0;

            @Override
            public void run() {
                i++;

                if (i == 400) {
                    doDistanceCheck = true;
                }

                if (l.getWorld().getDifficulty() == Difficulty.PEACEFUL) {
                    hitbox.remove(true);
                }

                if (!doDistanceCheck) return;
                if (i % 20 != 0) return;

                double closest = Double.MAX_VALUE;

                for (Player p : l.getWorld().getPlayers()) {
                    if (p.getLocation(l).distanceSquared(location) < closest) {
                        closest = l.distanceSquared(location);
                    }
                }

                if (closest > 10000) {
                    hitbox.remove(true);
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 1);
    }

    protected void findTarget(double radius) {
        Location center = getLocation();
        Entity target = null;
        this.target = null;

        for (Player p : center.getWorld().getPlayers()) {
            if (p.getGameMode() != GameMode.SURVIVAL && p.getGameMode() != GameMode.ADVENTURE) continue;

            double distance = p.getLocation().distance(center);

            if (distance <= radius && (target == null || (distance < target.getLocation().distance(center)))) {
                target = p;
            }
        }

        if (target == null) {
            List<Entity> nearbyEntities = (ArrayList<Entity>) center.getWorld().getNearbyEntities(center, radius, radius, radius);

            for (int i = 0; i < nearbyEntities.size(); i++) {
                Entity e = nearbyEntities.get(rng.nextInt(nearbyEntities.size()));

                if (e instanceof Player) continue;
                if (e instanceof Slime) continue;
                if (e instanceof Monster) continue;
                if (!(e instanceof LivingEntity)) continue;

                target = e;

                break;
            }
        }

        if (target == null) return;

        this.target = (LivingEntity) target;
    }

    protected BukkitTask runAway() {
        return new BukkitRunnable() {

            private final Location locationHelper = getLocation().clone().add(rng.nextInt(100) - 50, 150, rng.nextInt(100) - 50);
            private final Vector vectorHelper = LVMath.subtractToVector(new Vector(), locationHelper, getLocation()).normalize().multiply(0.25);

            @Override
            public void run() {
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
        double pitch = body.getPitch();
        double yaw = body.getYaw();
        double wantedPitch = direction[0];
        double wantedYaw = direction[1];

        if (pitch + 15 <= wantedPitch) {
            pitchInc = 15;
        } else if (pitch - 15 >= wantedPitch) {
            pitchInc = -15;
        } else {
            pitchInc = wantedPitch - pitch;
        }

        if (yaw + (360 - wantedYaw) < Math.abs(yaw - wantedYaw)) {
            yawInc = -15;
        } else if (wantedYaw + (360 - yaw) < Math.abs(yaw - wantedYaw)) {
            yawInc = 15;
        } else if (yaw + 15 <= wantedYaw) {
            yawInc = 15;
        } else if (yaw - 15 >= wantedYaw) {
            yawInc = -15;
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
        for (Entity e : location.getWorld().getEntities()) {
            if (location.distanceSquared(e.getLocation()) > Math.pow(radius, 2)) continue;

            if (!e.getUniqueId().equals(target.getUniqueId())) {
                if (e instanceof Monster) continue;
                if (e instanceof Slime) continue;
                if (!(e instanceof LivingEntity)) continue;
            }

            ((LivingEntity) e).damage(hitbox.getDamage(), hitbox.getSlime());
        }
    }

    protected void hitSound() {
        Location center = getLocation();

        for (Player p : center.getWorld().getPlayers()) {
            if (p.getLocation().distanceSquared(center) > 625) continue;

            p.playSound(center, Sound.ENTITY_SLIME_ATTACK, 3, 1.5f);
        }
    }

    public Location getLocation() {
        return ((ParticleSphere) model.getShape(0)).getCenter();
    }
}
