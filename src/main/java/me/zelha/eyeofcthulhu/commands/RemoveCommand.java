package me.zelha.eyeofcthulhu.commands;

import hm.zelha.particlesfx.shapers.ParticleSphere;
import me.zelha.eyeofcthulhu.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class RemoveCommand implements CommandExecutor {

    private final ParticleSphere body = (ParticleSphere) Main.getEoc().getShape(0);

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        body.removeParticle(body.getSecondaryParticleAmount() - 1);

        return true;
    }
}
