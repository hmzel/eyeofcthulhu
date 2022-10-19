package me.zelha.eyeofcthulhu.enemies;

import hm.zelha.particlesfx.shapers.ParticleSphere;
import hm.zelha.particlesfx.util.ParticleShapeCompound;
import me.zelha.eyeofcthulhu.util.Hitbox;
import net.minecraft.server.v1_8_R3.EntityLiving;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public abstract class ParticleEnemy {

    protected static final ThreadLocalRandom rng = ThreadLocalRandom.current();
    protected final ParticleShapeCompound model = new ParticleShapeCompound();
    protected final Hitbox box;
    protected EntityLiving target = null;

    protected ParticleEnemy(Hitbox box) {
        this.box = box;
    }

    public void target(Entity e) {
        target = (EntityLiving) ((CraftEntity) e).getHandle();
    }

    public abstract void onHit(Entity attacker);

    protected abstract void startAI();

    protected void findTarget() {
        Location center = ((ParticleSphere) model.getShape(0)).getCenter();
        Entity target = null;

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!center.getWorld().equals(p.getWorld())) continue;
            if (p.getGameMode() != GameMode.SURVIVAL) continue;

            if (target == null) {
                target = p;

                continue;
            }

            if (p.getLocation().distance(center) < target.getLocation().distance(center)) {
                target = p;
            }
        }

        if (target != null && target.getLocation().distance(center) > 25) {
            target = null;
        }

        if (target == null) {
            List<Entity> nearbyEntities = (ArrayList<Entity>) center.getWorld().getNearbyEntities(center, 50, 50, 50);

            for (int i = 0; i < nearbyEntities.size(); i++) {
                Entity e = nearbyEntities.get(rng.nextInt(nearbyEntities.size() - 1));

                if (e instanceof Player) continue;
                if (e instanceof Slime) continue;
                if (e instanceof Monster) continue;
                if (!(e instanceof LivingEntity)) continue;

                target = e;
                break;
            }
        }

        if (target == null) return;

        this.target = (EntityLiving) ((CraftEntity) target).getHandle();
    }

    public ParticleShapeCompound getModel() {
        return model;
    }
}
