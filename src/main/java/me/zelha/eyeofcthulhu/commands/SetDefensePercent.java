package me.zelha.eyeofcthulhu.commands;

import me.zelha.eyeofcthulhu.util.Hitbox;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SetDefensePercent implements CommandExecutor {

    private final Hitbox box;

    public SetDefensePercent(Hitbox box) {
        this.box = box;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        box.setDefensePercent(Double.parseDouble(args[0]));

        return true;
    }
}
