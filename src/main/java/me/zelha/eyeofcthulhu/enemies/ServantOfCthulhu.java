package me.zelha.eyeofcthulhu.enemies;

import hm.zelha.particlesfx.particles.ParticleDust;
import hm.zelha.particlesfx.particles.parents.Particle;
import hm.zelha.particlesfx.shapers.ParticleLine;
import hm.zelha.particlesfx.shapers.ParticleSphere;
import hm.zelha.particlesfx.util.LVMath;
import hm.zelha.particlesfx.util.LocationSafe;
import hm.zelha.particlesfx.util.ParticleShapeCompound;
import me.zelha.eyeofcthulhu.Main;
import me.zelha.eyeofcthulhu.listeners.HitboxListener;
import me.zelha.eyeofcthulhu.util.Hitbox;
import net.minecraft.server.v1_8_R3.EntityLiving;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class ServantOfCthulhu extends ParticleEnemy {

    private static final Particle WHITE = new ParticleDust(Color.WHITE).setPureColor(true);
    private static final Particle BLACK = new ParticleDust(Color.BLACK);
    private static final Particle RED = new ParticleDust(Color.RED, 85);
    private static final Particle PURPLE = new ParticleDust(Color.PURPLE);
    private final EyeOfCthulhu owner;

    static {
        WHITE.setRadius(32);
        BLACK.setRadius(32);
        RED.setRadius(32);
        PURPLE.setRadius(32);
    }

    public ServantOfCthulhu(Location location, EyeOfCthulhu owner) {
        this.owner = owner;
        World world = location.getWorld();
        LocationSafe center = new LocationSafe(world, location.getX(), location.getY(), location.getZ());
        Particle tendrilRed = new ParticleDust(Color.RED, 75);
        ParticleShapeCompound tendrils = new ParticleShapeCompound();
        ParticleSphere body = new ParticleSphere(RED, center, 0.5, 0.5, 0.5, 7, 50);
        double damage;
        double maxHealth;

        if (world.getDifficulty() == Difficulty.EASY) {
            damage = 1;
            maxHealth = 10;
        } else if (world.getDifficulty() == Difficulty.NORMAL) {
            damage = 2;
            maxHealth = 15;
        } else {
            damage = 3;
            maxHealth = 20;
        }

        super.hitbox = new Hitbox(this, center, 1, damage, maxHealth, "Servant of Cthulhu", false);

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
    public void onDeath(boolean animate) {
        super.onDeath(animate);
        owner.onServantDeath();
    }

    @Override
    public void onHit(Entity attacker, double damage) {
        hitSound();

        if (attacker instanceof Player && ((Player) attacker).getGameMode() != GameMode.SURVIVAL) {
            return;
        }

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
            private final Vector vHelper = new Vector(0, 0, 0);
            private final Vector vHelper2 = new Vector(0, 0, 0);

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

                for (Entity e : center.getWorld().getNearbyEntities(center, body.getxRadius() * 2, body.getxRadius() * 2, body.getxRadius() * 2)) {
                    if (!(e instanceof Slime)) continue;
                    if (e.getCustomName() == null) continue;
                    if (!e.getCustomName().equals(hitbox.getSlime().getCustomName())) continue;
                    if (!((Slime) e).hasPotionEffect(PotionEffectType.INVISIBILITY)) continue;

                    ParticleEnemy enemy = null;

                    for (Hitbox box : HitboxListener.getHitboxes()) {
                        if (box == hitbox) continue;

                        if (box.sameEntity(e)) {
                            enemy = box.getEnemy();
                            break;
                        }
                    }

                    if (enemy == null) continue;

                    Location l = enemy.getLocation();

                    vHelper2.setX(center.getX()).setY(center.getY()).setZ(center.getZ());
                    vHelper2.add(vHelper);

                    if (!vHelper2.isInSphere(l.toVector(), body.getxRadius() * 2)) continue;

                    lHelper.zero().add(vHelper2);
                    LVMath.subtractToVector(vHelper2, l, lHelper);
                    vHelper2.normalize();
                    vHelper2.multiply((body.getxRadius() * 2) - l.distance(lHelper));
                    vHelper.subtract(vHelper2);
                }

                model.move(vHelper);
                damageNearby(center, 1);
            }
        }.runTaskTimer(Main.getInstance(), 0, 1);
    }
}





