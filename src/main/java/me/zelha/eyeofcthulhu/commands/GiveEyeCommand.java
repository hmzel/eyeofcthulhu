package me.zelha.eyeofcthulhu.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;

public class GiveEyeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        if (!sender.hasPermission("eyeofcthulhu.giveeye")) {
            sender.sendMessage("§cI'm sorry, but you do not have permission to perform this command. Please contact the server administrators if you believe that this is in error.");
            return true;
        }

        ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
        SkullMeta meta = (SkullMeta) item.getItemMeta();

        meta.setOwner("HalfCourt93");
        meta.setDisplayName("§fSuspicious Looking Eye");
        meta.setLore(Arrays.asList(
                "§9Consumable",
                "§9Summons the Eye of Cthulhu"
        ));
        item.setItemMeta(meta);

        ((Player) sender).getInventory().addItem(item);

        return true;
    }
}
