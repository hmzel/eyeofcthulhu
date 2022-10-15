package me.zelha.eyeofcthulhu;

import hm.zelha.particlesfx.particles.ParticleDust;
import hm.zelha.particlesfx.particles.parents.Particle;
import hm.zelha.particlesfx.shapers.ParticleLine;
import hm.zelha.particlesfx.shapers.ParticleSphere;
import hm.zelha.particlesfx.util.LocationSafe;
import hm.zelha.particlesfx.util.ParticleShapeCompound;
import me.zelha.eyeofcthulhu.commands.AddCommand;
import me.zelha.eyeofcthulhu.commands.RemoveCommand;
import me.zelha.eyeofcthulhu.commands.SoutCommand;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ThreadLocalRandom;

public final class Main extends JavaPlugin {

    public static ParticleShapeCompound eoc = new ParticleShapeCompound();
    private final ThreadLocalRandom rng = ThreadLocalRandom.current();

    @Override
    public void onEnable() {
        World world = Bukkit.getWorld("zelha");
        LocationSafe center = new LocationSafe(world, 0, 45, 0);
        Particle white = new ParticleDust(Color.WHITE);
        Particle red = new ParticleDust(Color.RED, 50);
        ParticleSphere body = new ParticleSphere(white, center, 3, 3, 3, 20, 750);

        eoc.addShape(body);

        for (int i = 0; i < 10; i++) {
            eoc.addShape(new ParticleLine(red, 30, new LocationSafe(world, 0, 48, 0), new LocationSafe(world, 0, 51.5, 0)));
        }

        for (int i = 0; i < 5; i++) {
            eoc.addShape(new ParticleLine(red, 10, new LocationSafe(world, 0, 48, 0), new LocationSafe(world, 0, 49, 0)));
        }

        for (int i = 0; i < 15; i++) {
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

        getCommand("add").setExecutor(new AddCommand());
        getCommand("remove").setExecutor(new RemoveCommand());
        getCommand("sout").setExecutor(new SoutCommand());
    }

    public static ParticleShapeCompound getEoc() {
        return eoc;
    }
}
