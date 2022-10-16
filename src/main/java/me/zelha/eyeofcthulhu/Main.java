package me.zelha.eyeofcthulhu;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        new ServantOfCthulhu(Bukkit.getPlayer("hmzel").getLocation());
    }
}
