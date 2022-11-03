package me.zelha.eyeofcthulhu.listeners;

import me.zelha.eyeofcthulhu.Main;
import org.bukkit.Difficulty;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.concurrent.ThreadLocalRandom;

public class LootTableListener implements Listener {

    private final ThreadLocalRandom rng = ThreadLocalRandom.current();

    @EventHandler
    public void onChestLoad(ChunkPopulateEvent e) {
        if (e.getChunk().getWorld().getDifficulty() == Difficulty.PEACEFUL) return;

        for (BlockState block : e.getChunk().getTileEntities()) {
            if (block.getType() != Material.CHEST) continue;
            if (rng.nextInt(5) != 1) return;

            block.setMetadata("WillHaveEye", new FixedMetadataValue(Main.getInstance(), true));
        }

        for (Entity entity : e.getChunk().getEntities()) {
            if (!(entity instanceof StorageMinecart)) continue;
            if (rng.nextInt(5) != 1) return;

            addSummoningItem((StorageMinecart) entity);
        }
    }

    @EventHandler
    public void onChestOpen(InventoryOpenEvent e) {
        if (e.getPlayer().getWorld().getDifficulty() == Difficulty.PEACEFUL) return;

        InventoryHolder holder = e.getInventory().getHolder();

        if (!(holder instanceof Chest)) return;
        if (!((Chest) holder).getMetadata("WillHaveEye").get(0).asBoolean()) return;

        addSummoningItem(holder);

        ((Chest) holder).removeMetadata("WillHaveEye", Main.getInstance());
    }

    private void addSummoningItem(InventoryHolder invHolder) {
        Inventory inv = invHolder.getInventory();

        //assumes the chest isn't full because that never happens in naturally genned chests
        while (true) {
            int i = rng.nextInt(inv.getSize());
            ItemStack item = inv.getItem(i);

            if (item == null || item.getType() == Material.AIR) {
                inv.setItem(i, Main.getSummonItem());
                break;
            }
        }
    }
}














