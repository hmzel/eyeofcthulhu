package me.zelha.eyeofcthulhu.listeners;

import me.zelha.eyeofcthulhu.util.Hitbox;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

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

            if (box.sameEntity(e.getEntity()) && ((Slime) e.getEntity()).getHealth() - e.getFinalDamage() <= 0) {
                box.remove();
            }
        }
    }

    @EventHandler
    public void noNormalDamage(EntityDamageEvent e) {
        for (Hitbox box : hitboxes) {
            if (box.sameEntity(e.getEntity()) && e.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        for (Hitbox box : hitboxes) {
            if (box.sameEntity(e.getEntity())) {
                e.getDrops().clear();

                if (box.getBar() != null) e.setDroppedExp(500);
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
