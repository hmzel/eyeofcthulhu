package me.zelha.eyeofcthulhu;

import hm.zelha.particlesfx.particles.ParticleDust;
import hm.zelha.particlesfx.particles.ParticleSwirl;
import hm.zelha.particlesfx.shapers.ParticleLine;
import hm.zelha.particlesfx.shapers.ParticleSphere;
import hm.zelha.particlesfx.util.LocationS;
import hm.zelha.particlesfx.util.ParticleShapeCompound;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ThreadLocalRandom;

public final class Main extends JavaPlugin {

    private final ThreadLocalRandom rng = ThreadLocalRandom.current();

    @Override
    public void onEnable() {
        World world = Bukkit.getWorld("zelha");
        ParticleShapeCompound eoc = new ParticleShapeCompound();
        LocationS center = new LocationS(world, 0, 45, 0);

        eoc.addShape(new ParticleSphere(new ParticleSwirl(Color.WHITE), center, 3, 3, 3, 20, 750));

        for (int i = 0; i < 10; i++) {
            eoc.addShape(new ParticleLine(new ParticleDust(Color.RED, 50), 30, new LocationS(world, 0, 48, 0), new LocationS(world, 0, 51.5, 0)));
        }

        for (int i = 0; i < 5; i++) {
            eoc.addShape(new ParticleLine(new ParticleDust(Color.RED, 50), 10, new LocationS(world, 0, 48, 0), new LocationS(world, 0, 49, 0)));
        }

        for (int i = 0; i < 16; i++) {
            ParticleLine tendril = (ParticleLine) eoc.getShape(i + 1);

            if (i < 5) {
                tendril.rotateAroundLocation(center, 30, 72 * i, 0);
                tendril.rotate(30, 72 * i, 0);
            } else if (i < 10) {
                tendril.rotateAroundLocation(center, 15, 180 + (72 * (i - 5)), 0);
                tendril.rotate(15, 180 + (72 * (i - 5)), 0);
            } else {
                tendril.rotateAroundLocation(center, 30, 180 + (72 * (i - 10)), 0);
                tendril.rotate(30, 180 + (72 * (i - 10)), 0);
            }

            tendril.setMechanic((particle, location, vector) -> location.add(rng.nextDouble(0.4) - 0.2, rng.nextDouble(0.4) - 0.2, rng.nextDouble(0.4) - 0.2));
        }
    }
}
