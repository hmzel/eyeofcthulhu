package me.zelha.eyeofcthulhu.listeners;

import me.zelha.eyeofcthulhu.Main;
import me.zelha.eyeofcthulhu.enemies.EyeOfCthulhu;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SummonItemListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        ItemStack item = e.getItem();

        if (e.getAction() != Action.RIGHT_CLICK_BLOCK && e.getAction() != Action.RIGHT_CLICK_AIR) return;
        if (item == null) return;

        ItemMeta meta = item.getItemMeta();

        //ItemStack#equals() doesn't work with skulls for some reason
        if (!meta.hasDisplayName()) return;
        if (!meta.hasLore()) return;
        if (!meta.getDisplayName().equals(Main.getSummonItem().getItemMeta().getDisplayName())) return;
        if (!meta.getLore().equals(Main.getSummonItem().getItemMeta().getLore())) return;

        e.setCancelled(true);

        if (p.getLocation().getY() <= 40) return;
        if (p.getWorld().getTime() < 12300 || p.getWorld().getTime() > 23850) return;

        Location l = p.getLocation();
        EyeOfCthulhu eye = new EyeOfCthulhu(l);

        for (Player player : Bukkit.getOnlinePlayers()) {
            double distance = l.distance(player.getLocation());

            if (distance > 100) continue;
            if (Double.isNaN(distance)) continue;

            player.sendMessage("§5Eye of Cthulhu has awoken!");
            player.playSound(eye.getLocation(), Sound.ENDERDRAGON_GROWL, 100, 1);
        }

        if (item.getAmount() == 1) {
            p.setItemInHand(new ItemStack(Material.AIR));
        } else {
            item.setAmount(item.getAmount() - 1);
        }
    }
}
