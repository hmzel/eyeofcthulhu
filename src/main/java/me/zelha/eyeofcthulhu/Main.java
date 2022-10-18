package me.zelha.eyeofcthulhu;

import me.zelha.eyeofcthulhu.enemies.ServantOfCthulhu;
import me.zelha.eyeofcthulhu.listeners.HitboxListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class Main extends JavaPlugin {

    public static Main instance;

    @Override
    public void onEnable() {
        instance = this;

        Bukkit.getPluginManager().registerEvents(new HitboxListener(), this);

        new BukkitRunnable() {

            int i = 0;

            @Override
            public void run() {
                i++;

                new ServantOfCthulhu(Bukkit.getPlayer("hmzel").getLocation());

                if (i == 10) cancel();
            }
        }.runTaskTimer(this, 0, 3);
    }

    @Override
    public void onDisable() {
        HitboxListener.onDisable();
    }

    public static Main getInstance() {
        return instance;
    }
}
