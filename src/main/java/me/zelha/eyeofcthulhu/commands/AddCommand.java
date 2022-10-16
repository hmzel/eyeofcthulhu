package me.zelha.eyeofcthulhu.commands;

import hm.zelha.particlesfx.particles.ParticleDust;
import hm.zelha.particlesfx.particles.ParticleNull;
import hm.zelha.particlesfx.particles.parents.Particle;
import hm.zelha.particlesfx.shapers.ParticleSphere;
import me.zelha.eyeofcthulhu.Main;
import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Field;

public class AddCommand implements CommandExecutor {

    private final ParticleSphere body = (ParticleSphere) Main.getEoc().getShape(0);
    private final Particle white = new ParticleDust(Color.WHITE).setPureColor(true);
    private final Particle black = new ParticleDust(Color.BLACK);
    private final Particle grey = new ParticleDust(Color.GRAY, 35);
    private final Particle red = new ParticleDust(Color.RED, 85);
    private final Particle blue = new ParticleDust(Color.BLUE);
    private final Particle olive = new ParticleDust(Color.OLIVE);
    private final Particle none = new ParticleNull();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) return false;
        if (!NumberUtils.isDigits(args[1])) return false;

        Particle p;

        try {
            Field f = this.getClass().getDeclaredField(args[0]);

            f.setAccessible(true);

            p = (Particle) f.get(this);
        } catch (NoSuchFieldException | IllegalAccessException | ClassCastException e) {
            e.printStackTrace();

            return false;
        }

        body.addParticle(p, Integer.parseInt(args[1]));

        return true;
    }
}
