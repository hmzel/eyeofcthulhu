package me.zelha.eyeofcthulhu.enemies;

import hm.zelha.particlesfx.particles.ParticleDust;
import hm.zelha.particlesfx.particles.parents.Particle;
import hm.zelha.particlesfx.shapers.ParticleLine;
import hm.zelha.particlesfx.shapers.ParticleSphere;
import hm.zelha.particlesfx.util.LVMath;
import hm.zelha.particlesfx.util.LocationSafe;
import hm.zelha.particlesfx.util.ParticleShapeCompound;
import me.zelha.eyeofcthulhu.Main;
import me.zelha.eyeofcthulhu.util.Hitbox;
import net.minecraft.server.v1_8_R3.EntityLiving;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class ServantOfCthulhu extends ParticleEnemy {

    private static final Particle WHITE = new ParticleDust(Color.WHITE).setPureColor(true);
    private static final Particle BLACK = new ParticleDust(Color.BLACK);
    private static final Particle RED = new ParticleDust(Color.RED, 85);
    private static final Particle PURPLE = new ParticleDust(Color.PURPLE);

    static {
        WHITE.setRadius(32);
        BLACK.setRadius(32);
        RED.setRadius(32);
        PURPLE.setRadius(32);
    }

    public ServantOfCthulhu(Location location) {
        World world = location.getWorld();
        LocationSafe center = new LocationSafe(world, location.getX(), location.getY(), location.getZ());
        Particle tendrilRed = new ParticleDust(Color.RED, 75);
        ParticleShapeCompound tendrils = new ParticleShapeCompound();
        ParticleSphere body = new ParticleSphere(RED, center, 0.5, 0.5, 0.5, 7, 50);
        super.hitbox = new Hitbox(this, center, 1, 1, 10, "Servant of Cthulhu", false);

        model.addShape(body);
        tendrilRed.setRadius(32);

        for (int i = 0; i < 3; i++) {
            ParticleLine tendril = new ParticleLine(tendrilRed, 4,
                    new LocationSafe(world, center.getX(), center.getY() + 0.5, center.getZ()),
                    new LocationSafe(world, center.getX(), center.getY() + 1, center.getZ())
            );

            tendrils.addShape(tendril);
            tendril.rotateAroundLocation(center, 20, 120 * i, 0);
            tendril.rotate(20, 120 * i, 0);
            tendril.setMechanic((particle, l, vector) ->
                    l.add(rng.nextDouble(0.4) - 0.2, rng.nextDouble(0.4) - 0.2, rng.nextDouble(0.4) - 0.2)
            );
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
        body.addParticle(PURPLE, 34);
        body.addParticle(BLACK, 46);

        model.addShape(tendrils);
        startAI();
        startDespawner(center);
    }

    @Override
    public void onHit(Entity attacker, double damage) {
        if (attacker instanceof Player && ((Player) attacker).getGameMode() != GameMode.SURVIVAL) {
            return;
        }

        target = (EntityLiving) ((CraftEntity) attacker).getHandle();
    }

    @Override
    protected void startAI() {
        findTarget(25);

        new BukkitRunnable() {

            private final ParticleSphere body = (ParticleSphere) model.getShape(0);
            private final Location l = body.getCenter().clone();
            private final Vector vHelper = new Vector(0, 0, 0);

            @Override
            public void run() {
                if (!body.isRunning()) {
                    cancel();
                    return;
                }

                if (target == null || !target.valid || !target.isAlive()) {
                    findTarget(25);
                    return;
                }

                l.zero().add(target.locX, target.locY + (target.length / 2), target.locZ);
                LVMath.subtractToVector(vHelper, l, body.getCenter());
                vHelper.normalize().multiply(0.2);
                model.move(vHelper);
                faceAroundBody(l);
                damageNearby(body.getCenter(), 1);
            }
        }.runTaskTimer(Main.getInstance(), 0, 1);
    }
}





