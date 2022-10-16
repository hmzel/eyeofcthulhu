package me.zelha.eyeofcthulhu;

import net.minecraft.server.v1_8_R3.EntityWither;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftWither;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Wither;
import org.bukkit.scheduler.BukkitRunnable;

public class BossBar {

    private String display;
    private double health;

    public BossBar(Location location, String display, double health) {
        this.display = display;
        this.health = health;

        Wither wither = (Wither) location.getWorld().spawnEntity(location, EntityType.WITHER);
        EntityWither nmsWither = ((CraftWither) wither).getHandle();
        NBTTagCompound tag = nmsWither.getNBTTag();

        if (tag == null) tag = new NBTTagCompound();

        wither.setMaxHealth(health);
        wither.setCustomName(display);
        nmsWither.c(tag);
        tag.setInt("NoAI", 1);
        tag.setInt("Invul", 879);
        tag.setBoolean("Silent", true);
        nmsWither.f(tag);

        new BukkitRunnable() {
            @Override
            public void run() {
                wither.teleport(location);
                wither.setHealth(health);
            }
        }.runTaskTimer(Main.getInstance(), 0, 1);
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    public String getDisplay() {
        return display;
    }

    public double getHealth() {
        return health;
    }
}
