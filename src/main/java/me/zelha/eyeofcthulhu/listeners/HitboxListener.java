package me.zelha.eyeofcthulhu.listeners;

import me.zelha.eyeofcthulhu.util.Hitbox;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.ArrayList;
import java.util.List;

public class HitboxListener implements Listener {

    private static final List<Hitbox> hitboxes = new ArrayList<>();

    @EventHandler
    public void correctDamage(EntityDamageByEntityEvent e) {
        for (Hitbox box : hitboxes) {
            if (box.sameEntity(e.getDamager())) {
                e.setDamage(box.getDamage());
            }
        }
    }

    public static void registerHitbox(Hitbox box) {
        hitboxes.add(box);
    }

    public static void unregisterHitbox(Hitbox box) {
        hitboxes.remove(box);
    }

    public static void onDisable() {
        for (Hitbox box : hitboxes) {
            box.remove();
        }
    }
}
