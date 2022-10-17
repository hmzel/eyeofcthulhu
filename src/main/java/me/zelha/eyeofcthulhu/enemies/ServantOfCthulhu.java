package me.zelha.eyeofcthulhu.enemies;

import hm.zelha.particlesfx.particles.ParticleDust;
import hm.zelha.particlesfx.particles.parents.Particle;
import hm.zelha.particlesfx.shapers.ParticleLine;
import hm.zelha.particlesfx.shapers.ParticleSphere;
import hm.zelha.particlesfx.util.LocationSafe;
import hm.zelha.particlesfx.util.ParticleShapeCompound;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.concurrent.ThreadLocalRandom;

public class ServantOfCthulhu {

    private static final Particle WHITE = new ParticleDust(Color.WHITE).setPureColor(true);
    private static final Particle BLACK = new ParticleDust(Color.BLACK);
    private static final Particle RED = new ParticleDust(Color.RED, 85);
    private static final Particle PURPLE = new ParticleDust(Color.PURPLE);
    private final ThreadLocalRandom rng = ThreadLocalRandom.current();
    private final ParticleShapeCompound servant = new ParticleShapeCompound();

    public ServantOfCthulhu(Location location) {
        World world = location.getWorld();
        LocationSafe center = new LocationSafe(world, location.getX(), location.getY(), location.getZ());
        Particle tendrilRed = new ParticleDust(Color.RED, 75);
        ParticleSphere body = new ParticleSphere(RED, center, 0.5, 0.5, 0.5, 7, 50);

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
    }
}
