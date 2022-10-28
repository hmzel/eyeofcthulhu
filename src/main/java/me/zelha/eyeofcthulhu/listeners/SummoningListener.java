package me.zelha.eyeofcthulhu.listeners;

import me.zelha.eyeofcthulhu.Main;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

public class SummoningListener implements Listener {

    private final ThreadLocalRandom rng = ThreadLocalRandom.current();

    @EventHandler
    public void onChestLoad(ChunkPopulateEvent e) {
        for (BlockState block : e.getChunk().getTileEntities()) {
            if (block.getType() != Material.CHEST) continue;

            addSummoningItem((Chest) block);
        }

        for (Entity entity : e.getChunk().getEntities()) {
            if (!(entity instanceof StorageMinecart)) continue;

            addSummoningItem((StorageMinecart) entity);
        }
    }

    private void addSummoningItem(InventoryHolder invHolder) {
        if (rng.nextInt(5) != 1) return;

        Inventory inv = invHolder.getInventory();

        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);

            if (item == null || item.getType() == Material.AIR) {
                inv.setItem(i, Main.getSummonItem());
                break;
            }
        }
    }
}














