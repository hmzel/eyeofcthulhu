package me.zelha.eyeofcthulhu;

import hm.zelha.particlesfx.util.ParticleSFX;
import me.zelha.eyeofcthulhu.commands.GiveEyeCommand;
import me.zelha.eyeofcthulhu.listeners.HitboxListener;
import me.zelha.eyeofcthulhu.listeners.LootTableListener;
import me.zelha.eyeofcthulhu.listeners.SummonItemListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public final class Main extends JavaPlugin {

    private static final ItemStack summonItem;
    public static Main instance;

    static {
        summonItem = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
        SkullMeta meta = (SkullMeta) summonItem.getItemMeta();

        meta.setOwner("HalfCourt93");
        meta.setDisplayName("§fSuspicious Looking Eye");
        meta.setLore(Arrays.asList(
                "§9Consumable",
                "§9Summons the Eye of Cthulhu"
        ));
        summonItem.setItemMeta(meta);
    }

    @Override
    public void onEnable() {
        instance = this;

        ParticleSFX.setPlugin(this);
        Bukkit.getPluginManager().registerEvents(new HitboxListener(), this);
        Bukkit.getPluginManager().registerEvents(new LootTableListener(), this);
        Bukkit.getPluginManager().registerEvents(new SummonItemListener(), this);
        getCommand("giveeye").setExecutor(new GiveEyeCommand());
    }

    @Override
    public void onDisable() {
        HitboxListener.onDisable();
    }

    public static ItemStack getSummonItem() {
        return summonItem;
    }

    public static Main getInstance() {
        return instance;
    }
}
