package me.zelha.eyeofcthulhu.util;

import me.zelha.eyeofcthulhu.Main;
import net.minecraft.server.v1_8_R3.EntityWither;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftWither;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Wither;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class BossBar {

    private final Wither wither;
    private final BukkitTask task;
    private String display;
    private double health;

    public BossBar(Location location, String display, double maxHealth) {
        this.display = display;
        this.health = maxHealth;
        this.wither = (Wither) location.getWorld().spawnEntity(location, EntityType.WITHER);
        EntityWither nmsWither = ((CraftWither) wither).getHandle();
        NBTTagCompound tag = nmsWither.getNBTTag();

        if (tag == null) {
            tag = new NBTTagCompound();
        }

        wither.setCustomName(display);
        nmsWither.c(tag);
        tag.setInt("NoAI", 1);
        tag.setInt("Invul", 879);
        tag.setInt("PersistenceRequired", 1);
        tag.setInt("Silent", 1);
        nmsWither.f(tag);

        task = new BukkitRunnable() {
            @Override
            public void run() {
                if (health <= 0) {
                    wither.remove();
                    cancel();

                    return;
                }

                wither.teleport(location);
                wither.setHealth(wither.getMaxHealth() * (health / maxHealth));
            }
        }.runTaskTimer(Main.getInstance(), 0, 1);
    }

    public void remove() {
        task.cancel();
        wither.remove();
    }

    public void setDisplay(String display) {
        this.display = display;

        wither.setCustomName(display);
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
