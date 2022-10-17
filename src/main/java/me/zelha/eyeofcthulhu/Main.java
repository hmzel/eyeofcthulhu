package me.zelha.eyeofcthulhu;

import me.zelha.eyeofcthulhu.enemies.ServantOfCthulhu;
import me.zelha.eyeofcthulhu.listeners.HitboxListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    public static Main instance;

    @Override
    public void onEnable() {
        instance = this;

        Bukkit.getPluginManager().registerEvents(new HitboxListener(), this);

        new ServantOfCthulhu(Bukkit.getPlayer("hmzel").getLocation());
    }

    @Override
    public void onDisable() {
        HitboxListener.onDisable();
    }

    public static Main getInstance() {
        return instance;
    }
}
