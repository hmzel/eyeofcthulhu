package me.zelha.eyeofcthulhu.enemies;

import hm.zelha.particlesfx.particles.ParticleDustColored;
import hm.zelha.particlesfx.particles.ParticleExplosionEmitter;
import hm.zelha.particlesfx.particles.ParticleNull;
import hm.zelha.particlesfx.particles.parents.Particle;
import hm.zelha.particlesfx.shapers.ParticleLine;
import hm.zelha.particlesfx.shapers.ParticleSphere;
import hm.zelha.particlesfx.shapers.ParticleSphereCSA;
import hm.zelha.particlesfx.shapers.parents.ParticleShaper;
import hm.zelha.particlesfx.shapers.parents.Shape;
import hm.zelha.particlesfx.util.Color;
import hm.zelha.particlesfx.util.*;
import me.zelha.eyeofcthulhu.Main;
import me.zelha.eyeofcthulhu.util.Hitbox;
import net.minecraft.server.v1_8_R3.EntityLiving;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class EyeOfCthulhu extends ParticleEnemy {

    private static final Map<String, Particle> particleMap = new HashMap<>();
    private final Map<UUID, Double> damageMap = new HashMap<>();
    private final Location locationHelper;
    private final Vector vectorHelper = new Vector(0, 0, 0);
    private BukkitTask currentAI = null;
    private boolean phaseTwo = false;
    private int servantCount = 0;

    static {
        particleMap.put("RED", new ParticleDustColored(Color.RED, 85, 0.1, 0.1, 0.1, 1));
        particleMap.put("BLUE", new ParticleDustColored(Color.BLUE, 100, 0.1, 0.1, 0.1, 1));
        particleMap.put("WHITE", new ParticleDustColored(Color.WHITE, 100, 0.1, 0.1, 0.1, 1).setPureColor(true));
        particleMap.put("DIRTY_WHITE", new ParticleDustColored(new Color(255, 255, 200), 75));
        particleMap.put("BLACK", new ParticleDustColored(Color.BLACK, 100, 0.1, 0.1, 0.1, 2));
        particleMap.put("GRAY", new ParticleDustColored(Color.GRAY, 35, 0.1, 0.1, 0.1, 2));
        particleMap.put("OLIVE", new ParticleDustColored(Color.OLIVE, 100, 0.1, 0.1, 0.1, 2));
        particleMap.put("NONE", new ParticleNull());
    }

    public EyeOfCthulhu(Location location) {
        World world = location.getWorld();
        ParticleShapeCompound tendrils = new ParticleShapeCompound();
        LocationSafe center = new LocationSafe(location).add(rng.nextInt(100) - 50, 150, rng.nextInt(100) - 50);
        ParticleSphereCSA body = new ParticleSphereCSA(particleMap.get("WHITE"), center, 3, 20, 750);
        hitbox = new Hitbox(this, center, 7.5, 7 + (5 * (world.getDifficulty().ordinal() - 1)), 1200 + (394 * (world.getDifficulty().ordinal() - 1)), "Eye of Cthulhu", true);
        locationHelper = center.clone();

        if (world.getDifficulty() != Difficulty.EASY) {
            List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());

            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getLocation().distance(location) <= 200) continue;
                if (p.getGameMode() == GameMode.SURVIVAL || p.getGameMode() == GameMode.ADVENTURE) continue;

                players.remove(p);
            }

            //these numbers simulate what the health would be using terraria's calculations
            //using defense % instead because in most servers health cannot go above 2048
            //it would be genuinely insane to try and come up with some weird math for this, and probably take ages if its even possible,
            //so im just going to hard-code it. more comprehensible that way anyways
            switch (players.size()) {
                case 0:
                case 1:
                    break;
                case 2:
                    hitbox.setDefensePercent(27);
                    break;
                case 3:
                    hitbox.setDefensePercent(41.11);
                    break;
                case 4:
                    hitbox.setDefensePercent(50.055);
                    break;
                case 5:
                    hitbox.setDefensePercent(55.946);
                    break;
                case 6:
                    hitbox.setDefensePercent(60.015);
                    break;
                case 7:
                    hitbox.setDefensePercent(62.945);
                    break;
                case 8:
                    hitbox.setDefensePercent(65.133);
                    break;
                case 9:
                    hitbox.setDefensePercent(66.822);
                    break;
                case 10:
                    hitbox.setDefensePercent(67.702);
                    break;
                case 11:
                    hitbox.setDefensePercent(68.508);
                    break;
                case 12:
                    hitbox.setDefensePercent(69.21);
                    break;
                case 13:
                    hitbox.setDefensePercent(69.827);
                    break;
                case 14:
                    hitbox.setDefensePercent(70.372);
                    break;
                default:
                    hitbox.setDefensePercent(70.8585);
            }
        }

        for (int i = 0; i < 15; i++) {
            ParticleLine tendril = new ParticleLine(new ParticleDustColored(Color.RED, 75), 30,
                    new LocationSafe(center).add(0, 6.5, 0),
                    new LocationSafe(center).add(0, 3, 0)
            );

            if (i >= 10) {
                tendril.getLocation(0).subtract(0, 2.5, 0);
                tendril.setParticleFrequency(10);
            }

            if (i < 5) {
                tendril.rotateAroundLocation(center, 30, 72 * i, 0);
            } else if (i < 10) {
                tendril.rotateAroundLocation(center, 15, 180 + (72 * i), 0);
            } else {
                tendril.rotateAroundLocation(center, 30, 180 + (72 * i), 0);
            }

            tendril.face(center);
            tendrils.addShape(tendril);
            tendril.addMechanic(ShapeDisplayMechanic.Phase.BEFORE_ROTATION, ((particle, current, addition, count) -> {
                current.add(rng.nextDouble(0.4) - 0.2, rng.nextDouble(0.4) - 0.2, rng.nextDouble(0.4) - 0.2);
            }));
        }

        body.setAxisYaw(47); //i messed up while making phase 2's color and im Not Going To Redo It.
        hitbox.setDefense(2.5);
        model.addShape(body);
        model.addShape(tendrils);
        findTarget(200);
        startAI();
        startDespawner(center);
    }

    public void onServantDeath() {
        servantCount--;
    }

    @Override
    public void onDeath(boolean animate) {
        super.onDeath(animate);
        currentAI.cancel();

        if (!animate) return;

        roar(0.5);

        new BukkitRunnable() {

            private final Location location = getLocation();
            private final Particle explosion = new ParticleExplosionEmitter();
            int i = 0;

            @Override
            public void run() {
                vectorHelper.setX(rng.nextDouble(10) - 5);
                vectorHelper.setY(rng.nextDouble(10) - 5);
                vectorHelper.setZ(rng.nextDouble(10) - 5);
                locationHelper.zero().add(location).add(vectorHelper);

                ExperienceOrb orb = (ExperienceOrb) location.getWorld().spawnEntity(locationHelper, EntityType.EXPERIENCE_ORB);

                orb.setExperience(rng.nextInt(20) + 10);
                vectorHelper.setX(rng.nextDouble() - 0.5).setY(rng.nextDouble() - 0.5).setZ(rng.nextDouble() - 0.5);
                orb.setVelocity(vectorHelper);
                explosion.display(location);

                if (++i == 120) cancel();
            }
        }.runTaskTimer(Main.getInstance(), 0, 1);
    }

    @Override
    protected void startAI() {
        loadColorFile("PhaseOneColor");
        hoverAI(200);
    }

    @Override
    public void onHit(Entity attacker, double damage) {
        Difficulty difficulty = attacker.getWorld().getDifficulty();
        double health = hitbox.getSlime().getHealth() - damage;
        double maxHealth = hitbox.getSlime().getMaxHealth();

        hitSound();

        if (phaseTwo) {
            if (health <= maxHealth * 0.04) {
                if (difficulty == Difficulty.NORMAL) {
                    hitbox.setDamage(16);
                } else if (difficulty == Difficulty.HARD) {
                    hitbox.setDamage(24);
                }
            }
        } else {
            if ((difficulty == Difficulty.EASY && health <= maxHealth / 2) || (difficulty != Difficulty.EASY && health <= maxHealth * 0.65)) {
                switchPhase();
            }
        }

        if (!(attacker instanceof Player)) return;

        damageMap.put(attacker.getUniqueId(), damage + damageMap.getOrDefault(attacker.getUniqueId(), 0D));

        for (UUID uuid : damageMap.keySet()) {
            if (Bukkit.getPlayer(uuid) == null) {
                damageMap.remove(uuid);

                continue;
            }

            if (damageMap.get(uuid) > 100) {

                target = (EntityLiving) ((CraftEntity) Bukkit.getPlayer(uuid)).getHandle();
                damageMap.clear();
            }
        }
    }

    private void hoverAI(int time) {
        if (currentAI != null) currentAI.cancel();

        currentAI = new BukkitRunnable() {

            private final Location location = getLocation();
            private final int servantSpawn = (time - 10) / (3 + rng.nextInt(2) + Math.max(location.getWorld().getDifficulty().ordinal() - 2, 0));
            private int i = 0;

            @Override
            public void run() {
                dayCheck();

                if (target == null || !target.valid || !target.isAlive() || target.getHealth() <= 0) {
                    findTarget(50);

                    if (target == null) {
                        currentAI.cancel();

                        currentAI = runAway();
                    }

                    return;
                }

                locationHelper.zero().add(target.locX, target.locY, target.locZ);

                if (i % servantSpawn == 0 && !phaseTwo && locationHelper.distance(location) < 25 && servantCount <= 10) {
                    new ServantOfCthulhu(location, EyeOfCthulhu.this);

                    servantCount++;
                }

                locationHelper.add(0, target.length + 7.5, 0);
                LVMath.subtractToVector(vectorHelper, locationHelper, location);
                vectorHelper.normalize().multiply(0.25);
                model.move(vectorHelper);
                locationHelper.zero().add(target.locX, target.locY + (target.length / 2), target.locZ);
                faceAroundBody(locationHelper);
                damageNearby(location, 3);

                if (i == time) {
                    if (phaseTwo && location.getWorld().getDifficulty() != Difficulty.EASY) {
                        int anger = (int) ((hitbox.getSlime().getHealth() / hitbox.getSlime().getMaxHealth()) * 10) + 1;

                        if (anger <= 4 && rng.nextInt(anger) <= 1) {
                            rushAI(50, 2, 0);
                        }
                    }

                    rushAI(200, 3, 20);
                }

                i++;
            }
        }.runTaskTimer(Main.getInstance(), 0, 1);
    }

    private void rushAI(int time, double dashes, int wait) {
        if (currentAI != null) currentAI.cancel();

        currentAI = new BukkitRunnable() {

            private final Location location = getLocation();
            private final int dashTime = (int) Math.ceil(((time - (wait * dashes)) / dashes) + 1);
            private final int waitTime = wait + 1;
            private int ticks = 0;
            private int timer = 1;
            private boolean waiting = true;

            @Override
            public void run() {
                dayCheck();

                if (target == null || !target.valid || !target.isAlive() || target.getHealth() <= 0) {
                    findTarget(50);

                    if (target == null) {
                        currentAI.cancel();

                        currentAI = runAway();
                    }

                    return;
                }

                if (timer % dashTime == 0 && !waiting) {
                    timer = 1;
                    waiting = true;
                }

                if (waiting) {
                    locationHelper.zero().add(target.locX, target.locY + (target.length / 2), target.locZ);
                    LVMath.subtractToVector(vectorHelper, locationHelper, location);
                    vectorHelper.normalize().multiply(20D / dashTime * 2);
                    faceAroundBody(locationHelper);

                    if (wait == 0) {
                        for (int i = 0; i < 11; i++) {
                            faceAroundBody(locationHelper);
                        }
                    }

                    if (ticks == 0 || timer % waitTime == 0) {
                        timer = 1;
                        waiting = false;
                    }
                }

                if (!waiting) {
                    model.move(vectorHelper);
                    vectorHelper.multiply(0.95);

                    if (timer == 1 && phaseTwo) {
                        if (wait == 0) {
                            roar(2);
                        } else {
                            roar(1.5);
                        }
                    }
                }

                damageNearby(location, 3);

                ticks++;
                timer++;

                if (ticks == time) {
                    if (phaseTwo && location.getWorld().getDifficulty() != Difficulty.EASY) {
                        int anger = (int) ((hitbox.getSlime().getHealth() / hitbox.getSlime().getMaxHealth()) * 10) + 1;
                        int extraDash = 1;

                        if (anger <= 2) {
                            extraDash--;
                        }

                        if ((rng.nextInt(anger) <= 3 && wait != 0) || (wait == 0 && rng.nextInt(anger) <= extraDash)) {
                            rushAI(50, 2, 0);
                        } else if (anger <= 3) {
                            hoverAI(30);
                        } else {
                            hoverAI(60);
                        }
                    } else if (phaseTwo) {
                        hoverAI(60);
                    } else {
                        hoverAI(200);
                    }
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 1);
    }

    private void switchPhase() {
        if (currentAI != null) currentAI.cancel();

        phaseTwo = true;
        currentAI = new BukkitRunnable() {

            private final ParticleSphere body = (ParticleSphere) model.getShape(0);
            private final Location center = body.getCenter();
            private final Shape tendrils = model.getShape(1);
            private double rotation = 0.75;
            private int ticks = 1;

            @Override
            public void run() {
                tendrils.rotateAroundLocation(center, rotation, 0, 0);
                tendrils.rotate(rotation, 0, 0);
                body.rotate(rotation, 0, 0);

                if (ticks == 50) {
                    int removeIndex = body.getSecondaryParticleAmount() - 6;

                    for (int i = removeIndex; i < removeIndex + 6; i++) {
                        body.removeParticle(removeIndex);
                    }

                    loadColorFile("PhaseTwoColor");
                    hitbox.setDefense(0);
                    hitbox.setDamage(9 + (6 * (center.getWorld().getDifficulty().ordinal() - 1)));

                    if (center.getWorld().getDifficulty() != Difficulty.EASY) {
                        int servantAmount = 4 + rng.nextInt(4);

                        for (int i = 0; i < servantAmount && servantCount <= 10; i++, servantCount++) {
                            new ServantOfCthulhu(locationHelper.zero().add(center).add(rng.nextDouble() - 0.5, rng.nextDouble() - 0.5, rng.nextDouble() - 0.5), EyeOfCthulhu.this);
                        }
                    }

                    roar(1.5);
                }

                if (ticks == 100) {
                    //so that faceAroundBody doesn't get messed up
                    tendrils.setAroundPitch(center, tendrils.getAroundPitch() % 90);
                    tendrils.setPitch(tendrils.getPitch() % 90);
                    body.setPitch(body.getPitch() % 90);
                    hoverAI(60);
                }

                ticks++;

                if (ticks <= 50) {
                    rotation += 0.75;
                } else {
                    rotation -= 0.75;
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 1);
    }

    private void loadColorFile(String name) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(Main.getInstance().getResource(name)));

        for (String s : reader.lines().collect(Collectors.toList())) {
            String[] strings = s.split(", ");

            ((ParticleShaper) model.getShape(0)).addParticle(particleMap.get(strings[0]), Integer.parseInt(strings[1]));
        }

        try {
            reader.close();
        } catch (IOException err) {
            err.printStackTrace();
        }
    }

    private void dayCheck() {
        if (locationHelper.getWorld().getTime() >= 12300 && locationHelper.getWorld().getTime() <= 23850) return;

        currentAI.cancel();

        currentAI = runAway();
    }

    private void roar(double pitch) {
        Location center = getLocation();

        for (Entity e : center.getWorld().getNearbyEntities(center, 25, 25, 25)) {
            if (!(e instanceof Player)) continue;

            ((Player) e).playSound(center, Sound.ENDERDRAGON_GROWL, 3, (float) pitch);
        }
    }
}
