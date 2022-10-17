package me.zelha.eyeofcthulhu.util;

import me.zelha.eyeofcthulhu.Main;
import net.minecraft.server.v1_8_R3.EntityWither;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftWither;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Wither;
import org.bukkit.scheduler.BukkitRunnable;

public class BossBar {

    private final Wither wither;
    private String display;
    private double health;

    public BossBar(Location location, String display, double maxHealth) {
        this.display = display;
        this.health = maxHealth;
        this.wither = (Wither) location.getWorld().spawnEntity(location, EntityType.WITHER);
        EntityWither nmsWither = ((CraftWither) wither).getHandle();
        NBTTagCompound tag = nmsWither.getNBTTag();

        if (tag == null) tag = new NBTTagCompound();

        wither.setMaxHealth(maxHealth);
        wither.setCustomName(display);
        nmsWither.c(tag);
        tag.setInt("NoAI", 1);
        tag.setInt("Invul", 879);
        tag.setInt("PersistenceRequired", 1);
        tag.setInt("Silent", 1);
        nmsWither.f(tag);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (health <= 0) {
                    wither.remove();
                    cancel();

                    return;
                }

                wither.teleport(location);
                wither.setHealth(health);
            }
        }.runTaskTimer(Main.getInstance(), 0, 1);
    }

    public void remove() {
        health = 0;

        wither.remove();
    }

    public void setDisplay(String display) {
        this.display = display;

        wither.setCustomName(display);
    }

    public void setHealth(double health) {
        if (health > wither.getMaxHealth()) wither.setMaxHealth(health);

        this.health = health;
    }

    public String getDisplay() {
        return display;
    }

    public double getHealth() {
        return health;
    }
}
