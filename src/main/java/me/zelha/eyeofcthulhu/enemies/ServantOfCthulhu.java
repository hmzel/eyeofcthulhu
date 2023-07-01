package me.zelha.eyeofcthulhu.enemies;

import hm.zelha.particlesfx.particles.ParticleDustColored;
import hm.zelha.particlesfx.particles.parents.Particle;
import hm.zelha.particlesfx.shapers.ParticleLine;
import hm.zelha.particlesfx.shapers.ParticleSphere;
import hm.zelha.particlesfx.shapers.ParticleSphereCSA;
import hm.zelha.particlesfx.util.*;
import me.zelha.eyeofcthulhu.Main;
import me.zelha.eyeofcthulhu.listeners.HitboxListener;
import me.zelha.eyeofcthulhu.util.Hitbox;
import net.minecraft.server.v1_8_R3.EntityLiving;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class ServantOfCthulhu extends ParticleEnemy {

    private static final Particle WHITE = new ParticleDustColored(Color.WHITE).setPureColor(true).setRadius(32);
    private static final Particle BLACK = new ParticleDustColored(Color.BLACK).setRadius(32);
    private static final Particle RED = new ParticleDustColored(Color.RED, 85).setRadius(32);
    private static final Particle PURPLE = new ParticleDustColored(Color.PURPLE).setRadius(32);
    private final EyeOfCthulhu owner;

    public ServantOfCthulhu(Location location, EyeOfCthulhu owner) {
        this.owner = owner;
        LocationSafe center = new LocationSafe(location);
        ParticleSphereCSA body = new ParticleSphereCSA(RED, center, 0.5, 7, 50);
        ParticleShapeCompound tendrils = new ParticleShapeCompound();
        hitbox = new Hitbox(this, center, 1, center.getWorld().getDifficulty().ordinal(), 10 + (5 * (center.getWorld().getDifficulty().ordinal() - 1)), "Servant of Cthulhu", false);

        for (int i = 0; i < 3; i++) {
            ParticleLine tendril = new ParticleLine(new ParticleDustColored(Color.RED, 75).setRadius(32), 4,
                    new LocationSafe(center).add(0, 1, 0),
                    new LocationSafe(center).add(0, 0.5, 0)
            );

            tendril.rotateAroundLocation(center, 20, 120 * i, 0);
            tendril.face(center);
            tendrils.addShape(tendril);
            tendril.addMechanic(ShapeDisplayMechanic.Phase.BEFORE_ROTATION, ((particle, current, addition, count) -> {
                current.add(rng.nextDouble(0.4) - 0.2, rng.nextDouble(0.4) - 0.2, rng.nextDouble(0.4) - 0.2);
            }));
        }

        body.addParticle(WHITE, 9);
        body.addParticle(RED, 12);
        body.addParticle(WHITE, 13);
        body.addParticle(RED, 15);
        body.addParticle(WHITE, 16);
        body.addParticle(RED, 20);
        body.addParticle(WHITE, 21);
        body.addParticle(RED, 23);
        body.addParticle(WHITE, 24);
        body.addParticle(RED, 28);
        body.addParticle(WHITE, 29);
        body.addParticle(PURPLE, 29);
        body.addParticle(BLACK, 39);
        model.addShape(body);
        model.addShape(tendrils);
        startAI();
        startDespawner(center);
    }

    @Override
    public void onDeath(boolean animate) {
        super.onDeath(animate);
        owner.onServantDeath();
    }

    @Override
    public void onHit(Entity attacker, double damage) {
        hitSound();

        target = (EntityLiving) ((CraftEntity) attacker).getHandle();
    }

    @Override
    protected void startAI() {
        findTarget(25);
        hitSound();

        new BukkitRunnable() {

            private final ParticleSphere body = (ParticleSphere) model.getShape(0);
            private final Location center = body.getCenter();
            private final Location lHelper = center.clone();
            private final Vector vHelper = new Vector();
            private final Vector vHelper2 = new Vector();

            @Override
            public void run() {
                if (!body.isRunning()) {
                    cancel();

                    return;
                }

                if (center.getWorld().getTime() < 12300 || center.getWorld().getTime() > 23850) {
                    cancel();
                    runAway();

                    return;
                }

                if (target == null || !target.valid || !target.isAlive() || target.getHealth() <= 0) {
                    findTarget(25);

                    if (target == null) {
                        cancel();
                        runAway();
                    }

                    return;
                }

                lHelper.zero().add(target.locX, target.locY + (target.length / 2), target.locZ);
                faceAroundBody(lHelper);
                LVMath.subtractToVector(vHelper, lHelper, center);
                vHelper.normalize().multiply(0.2);

                for (Hitbox box : HitboxListener.getHitboxes()) {
                    Location l = box.getEnemy().getLocation();

                    if (box == hitbox) continue;
                    if (!box.getSlime().getCustomName().equals(hitbox.getSlime().getCustomName())) continue;
                    if (lHelper.zero().add(center).add(vHelper).distanceSquared(l) > 1) continue;

                    LVMath.subtractToVector(vHelper2, l, lHelper);
                    vHelper2.normalize();
                    vHelper2.multiply(1 - l.distance(lHelper));
                    vHelper.subtract(vHelper2);
                }

                model.move(vHelper);
                damageNearby(center, 1);
            }
        }.runTaskTimer(Main.getInstance(), 0, 1);
    }
}





