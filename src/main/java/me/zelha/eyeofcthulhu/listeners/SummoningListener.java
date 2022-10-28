package me.zelha.eyeofcthulhu.listeners;

import me.zelha.eyeofcthulhu.Main;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

public class SummoningListener implements Listener {

    private final ThreadLocalRandom rng = ThreadLocalRandom.current();

    @EventHandler
    public void onChestLoad(ChunkLoadEvent e) {
        if (!e.isNewChunk()) return;

        for (BlockState block : e.getChunk().getTileEntities()) {
            if (block.getType() != Material.CHEST) continue;
            if (rng.nextInt(5) != 1) continue;

            Chest chest = (Chest) block.getBlock();

            for (int i = 0; i < chest.getInventory().getSize(); i++) {
                ItemStack item = chest.getInventory().getItem(i);

                if (item == null || item.getType() == Material.AIR) {
                    chest.getInventory().setItem(i, Main.getSummonItem());
                    break;
                }
            }
        }
    }
}
