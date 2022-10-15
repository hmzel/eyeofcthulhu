package me.zelha.eyeofcthulhu.commands;

import hm.zelha.particlesfx.particles.parents.ColorableParticle;
import hm.zelha.particlesfx.particles.parents.Particle;
import hm.zelha.particlesfx.shapers.ParticleSphere;
import me.zelha.eyeofcthulhu.Main;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SoutCommand implements CommandExecutor {

    private final ParticleSphere body = (ParticleSphere) Main.getEoc().getShape(0);

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        for (int i = 0; i < body.getSecondaryParticleAmount(); i++) {
            Pair<Particle, Integer> pair = body.getSecondaryParticle(i);

            if (pair.getKey() instanceof ColorableParticle) {
                System.out.println(((ColorableParticle) pair.getKey()).getColor().asRGB() + " " + pair.getValue());
            } else {
                System.out.println(pair.getKey() + " " + pair.getValue());
            }
        }

        return true;
    }
}
