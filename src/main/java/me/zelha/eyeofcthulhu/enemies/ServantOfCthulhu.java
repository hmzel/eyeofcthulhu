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
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.concurrent.ThreadLocalRandom;

public class ServantOfCthulhu {

    private static final Particle WHITE = new ParticleDust(Color.WHITE).setPureColor(true);
    private static final Particle BLACK = new ParticleDust(Color.BLACK);
    private static final Particle RED = new ParticleDust(Color.RED, 85);
    private static final Particle PURPLE = new ParticleDust(Color.PURPLE);
    private final ThreadLocalRandom rng = ThreadLocalRandom.current();
    private final ParticleShapeCompound servant = new ParticleShapeCompound();
    private final Hitbox hitbox;
    private EntityLiving target = null;

    public ServantOfCthulhu(Location location) {
        World world = location.getWorld();
        LocationSafe center = new LocationSafe(world, location.getX(), location.getY(), location.getZ());
        Particle tendrilRed = new ParticleDust(Color.RED, 75);
        ParticleSphere body = new ParticleSphere(RED, center, 0.5, 0.5, 0.5, 7, 50);
        this.hitbox = new Hitbox(servant, 1, 1, center, 10, null);

        servant.addShape(body);

        for (int i = 0; i < 3; i++) {
            ParticleLine tendril = new ParticleLine(tendrilRed, 4,
                    new LocationSafe(world, center.getX(), center.getY() + 0.5, center.getZ()),
                    new LocationSafe(world, center.getX(), center.getY() + 1, center.getZ())
            );

            servant.addShape(tendril);
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

        startAI();
    }

    public void startAI() {
        findTarget();

        new BukkitRunnable() {

            private final ParticleSphere body = (ParticleSphere) servant.getShape(0);
            private final Location l = body.getCenter().clone();
            private final Vector vHelper = new Vector(0, 0, 0);

            @Override
            public void run() {
                if (!body.isRunning()) {
                    cancel();
                    return;
                }

                if (target == null || !target.valid || !target.isAlive()) {
                    findTarget();
                    return;
                }

                l.zero().add(target.locX, target.locY + 1.5, target.locZ);
                LVMath.subtractToVector(vHelper, l, body.getCenter());
                vHelper.normalize().multiply(0.2);
                servant.move(vHelper);
                servant.face(l);

                if (!(target instanceof EntityPlayer)) {
                    for (Entity e : l.getWorld().getNearbyEntities(body.getCenter(), 1, 1, 1)) {
                        if (e.equals(target.getBukkitEntity())) {
                            ((LivingEntity) e).damage(hitbox.getDamage(), hitbox.getSlime());
                        }
                    }
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 1);
    }

    private void findTarget() {
        Location center = ((ParticleSphere) servant.getShape(0)).getCenter();
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

        if (target != null && target.getLocation().distance(center) > 25) target = null;

        if (target == null) {
            for (Entity e : center.getWorld().getNearbyEntities(center, 50, 50, 50)) {
                if (e instanceof Player) continue;
                if (e instanceof Slime) continue;
                if (e instanceof Monster) continue;
                if (!(e instanceof LivingEntity)) continue;

                if (target == null) {
                    target = e;

                    continue;
                }

                if (e.getLocation().distance(center) < target.getLocation().distance(center)) {
                    target = e;
                }
            }
        }

        if (target == null) return;

        this.target = (EntityLiving) ((CraftEntity) target).getHandle();
    }

    public void setTarget(Entity target) {
        this.target = (EntityLiving) ((CraftEntity) target).getHandle();
    }
}





