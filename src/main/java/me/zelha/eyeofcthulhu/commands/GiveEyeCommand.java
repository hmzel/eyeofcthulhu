package me.zelha.eyeofcthulhu.commands;

import me.zelha.eyeofcthulhu.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GiveEyeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        if (!sender.hasPermission("eyeofcthulhu.giveeye")) {
            sender.sendMessage("§cI'm sorry, but you do not have permission to perform this command. Please contact the server administrators if you believe that this is in error.");
            return true;
        }

        ((Player) sender).getInventory().addItem(Main.getSummonItem());

        return true;
    }
}
