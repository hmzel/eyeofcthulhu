package me.zelha.eyeofcthulhu.enemies;

import hm.zelha.particlesfx.particles.ParticleDustColored;
import hm.zelha.particlesfx.particles.ParticleExplosionEmitter;
import hm.zelha.particlesfx.particles.ParticleNull;
import hm.zelha.particlesfx.particles.parents.Particle;
import hm.zelha.particlesfx.shapers.ParticleLine;
import hm.zelha.particlesfx.shapers.ParticleSphere;
import hm.zelha.particlesfx.shapers.ParticleSphereCSA;
import hm.zelha.particlesfx.shapers.parents.Shape;
import hm.zelha.particlesfx.util.Color;
import hm.zelha.particlesfx.util.*;
import me.zelha.eyeofcthulhu.Main;
import me.zelha.eyeofcthulhu.util.Hitbox;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.*;

public class EyeOfCthulhu extends ParticleEnemy {

    private static final Particle WHITE = new ParticleDustColored(Color.WHITE, 100, 0.1, 0.1, 0.1, 1).setPureColor(true);
    private static final Particle DIRTY_WHITE = new ParticleDustColored(new Color(255, 255, 200), 75);
    private static final Particle BLACK = new ParticleDustColored(Color.BLACK, 100, 0.1, 0.1, 0.1, 2);
    private static final Particle GRAY = new ParticleDustColored(Color.GRAY, 35, 0.1, 0.1, 0.1, 2);
    private static final Particle RED = new ParticleDustColored(Color.RED, 85, 0.1, 0.1, 0.1, 1);
    private static final Particle BLUE = new ParticleDustColored(Color.BLUE, 100, 0.1, 0.1, 0.1, 1);
    private static final Particle OLIVE = new ParticleDustColored(Color.OLIVE, 100, 0.1, 0.1, 0.1, 2);
    private static final Particle NONE = new ParticleNull();
    private static final Particle EXPLOSION = new ParticleExplosionEmitter();
    private final Map<UUID, Double> damageMap = new HashMap<>();
    private final Location locationHelper;
    private final Vector vectorHelper = new Vector(0, 0, 0);
    private BukkitTask currentAI = null;
    private boolean phaseTwo = false;
    private int servantCount = 0;

    public EyeOfCthulhu(Location location) {
        World world = location.getWorld();
        Particle tendrilRed = new ParticleDustColored(Color.RED, 75);
        ParticleShapeCompound tendrils = new ParticleShapeCompound();
        LocationSafe center = new LocationSafe(world, 0, 0, 0);

        center.add(location);
        center.add(rng.nextInt(100) - 50, 150, rng.nextInt(100) - 50);

        ParticleSphere body = new ParticleSphere(WHITE, center, 3, 3, 3, 20, 750);
        this.locationHelper = center.cloneToLocation();
        double damage;
        double maxHealth;

        if (world.getDifficulty() == Difficulty.EASY) {
            damage = 6;
            maxHealth = 1200;
        } else if (world.getDifficulty() == Difficulty.NORMAL) {
            damage = 12;
            maxHealth = 1560;
        } else {
            damage = 18;
            maxHealth = 1989;
        }

        super.hitbox = new Hitbox(this, center, 7.5, damage, maxHealth, "Eye of Cthulhu", true);

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

        hitbox.setDefense(2.5);
        model.addShape(body);
        findTarget(200);

        for (int i = 0; i < 15; i++) {
            ParticleLine tendril;

            if (i < 10) {
                tendril = new ParticleLine(tendrilRed, 30,
                        new LocationSafe(world, center.getX(), center.getY() + 3, center.getZ()),
                        new LocationSafe(world, center.getX(), center.getY() + 6.5, center.getZ())
                );
            } else {
                tendril = new ParticleLine(tendrilRed, 10,
                        new LocationSafe(world, center.getX(), center.getY() + 3, center.getZ()),
                        new LocationSafe(world, center.getX(), center.getY() + 4, center.getZ())
                );
            }

            tendrils.addShape(tendril);

            if (i < 5) {
                tendril.rotateAroundLocation(center, 30, 72 * i, 0);
                tendril.rotate(30, 72 * i, 0);
            } else if (i < 10) {
                tendril.rotateAroundLocation(center, 15, 180 + (72 * (i - 5)), 0);
                tendril.rotate(15, 180 + (72 * (i - 5)), 0);
            } else {
                tendril.rotateAroundLocation(center, 30, 180 + (72 * (i - 10)), 0);
                tendril.rotate(30, 180 + (72 * (i - 10)), 0);
            }

            tendril.addMechanic(ShapeDisplayMechanic.Phase.BEFORE_ROTATION, ((particle, current, addition, count) -> {
                current.add(rng.nextDouble(0.4) - 0.2, rng.nextDouble(0.4) - 0.2, rng.nextDouble(0.4) - 0.2);
            }));
        }

        model.addShape(tendrils);
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

        //it'd be REALLY cool to make something like the terraria death animation here, but the code would be incredibly god-awful
        //and really tedious to make, so im not going to do it (for now)

        if (!animate) return;

        roar(0.5);

        new BukkitRunnable() {

            private final Location location = ((ParticleSphere) model.getShape(0)).getCenter();
            int i = 0;

            @Override
            public void run() {
                if (i == 120) {
                    cancel();
                    return;
                }

                locationHelper.zero().add(location);
                vectorHelper.setX(rng.nextDouble(10) - 5);
                vectorHelper.setY(rng.nextDouble(10) - 5);
                vectorHelper.setZ(rng.nextDouble(10) - 5);
                locationHelper.add(vectorHelper);

                ExperienceOrb orb = (ExperienceOrb) location.getWorld().spawnEntity(locationHelper, EntityType.EXPERIENCE_ORB);

                orb.setExperience(rng.nextInt(20) + 10);
                vectorHelper.setX(rng.nextDouble() - 0.5).setY(rng.nextDouble() - 0.5).setZ(rng.nextDouble() - 0.5);
                orb.setVelocity(vectorHelper);
                EXPLOSION.display(location);

                i++;
            }
        }.runTaskTimer(Main.getInstance(), 0, 1);
    }

    @Override
    public void onHit(Entity attacker, double damage) {
        hitSound();

        Difficulty difficulty = attacker.getWorld().getDifficulty();
        double health = hitbox.getSlime().getHealth() - damage;
        double maxHealth = hitbox.getSlime().getMaxHealth();

        if (phaseTwo) {
            if (health <= maxHealth * 0.04) {
                if (difficulty == Difficulty.NORMAL) {
                    hitbox.setDamage(16);
                } else {
                    hitbox.setDamage(24);
                }
            }
        } else {
            if ((difficulty == Difficulty.EASY && health <= maxHealth / 2) || (difficulty != Difficulty.EASY && health - damage <= maxHealth * 0.65)) {
                switchPhase();
            }
        }

        if (!(attacker instanceof Player)) return;

        damage += damageMap.getOrDefault(attacker.getUniqueId(), 0D);

        damageMap.put(attacker.getUniqueId(), damage);

        for (UUID uuid : damageMap.keySet()) {
            if (Bukkit.getPlayer(uuid) == null) {
                damageMap.remove(uuid);
                continue;
            }

            if (damageMap.get(uuid) > 100) {
                target = ((CraftLivingEntity) Bukkit.getPlayer(uuid)).getHandle();

                damageMap.clear();
            }
        }
    }

    @Override
    protected void startAI() {
        colorizePhaseOne();
        hoverAI(200);
    }

    private void hoverAI(int time) {
        if (currentAI != null) currentAI.cancel();

        int servants;

        if (getLocation().getWorld().getDifficulty() != Difficulty.EASY) {
            servants = 4 + rng.nextInt(2);
        } else {
            servants = 3 + rng.nextInt(2);
        }

        currentAI = new BukkitRunnable() {

            private final Location location = ((ParticleSphere) model.getShape(0)).getCenter();
            private final int servantSpawn = (time - 10) / servants;
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
                damageNearby(location, 1);

                if (i == time) {
                    if (phaseTwo && location.getWorld().getDifficulty() != Difficulty.EASY) {
                        //idk what else to call this variable, basically determines how urgent the current situation is for
                        //the EOC
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

            private final Location location = ((ParticleSphere) model.getShape(0)).getCenter();
            private final int dashTime = (int) Math.ceil(((time - (wait * dashes)) / dashes) + 1);
            private final int waitTime = wait + 1;
            private int i = 0;
            private int i2 = 1;
            private boolean waiting = false;

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

                if (i == 0 || (i2 % dashTime == 0 && !waiting)) {
                    locationHelper.zero().add(target.locX, target.locY + (target.length / 2), target.locZ);
                    LVMath.subtractToVector(vectorHelper, locationHelper, location);
                    vectorHelper.normalize().multiply(20D / dashTime * 2);
                    faceAroundBody(locationHelper);

                    if (wait == 0) {
                        for (int i = 0; i < 11; i++) {
                            faceAroundBody(locationHelper);
                        }
                    }

                    if (i != 0) {
                        i2 = 1;
                        waiting = true;
                    }
                } else if (waiting && i2 % waitTime == 0) {
                    i2 = 1;
                    waiting = false;
                }

                if (i2 == 1 && !waiting && phaseTwo) {
                    if (wait == 0) {
                        roar(2);
                    } else {
                        roar(1.5);
                    }
                }

                if (waiting) {
                    locationHelper.zero().add(target.locX, target.locY + (target.length / 2), target.locZ);
                    faceAroundBody(locationHelper);
                } else {
                    model.move(vectorHelper);
                    vectorHelper.multiply(0.95);
                }

                damageNearby(location, 3);

                if (i == time) {
                    if (phaseTwo && location.getWorld().getDifficulty() != Difficulty.EASY) {
                        //idk what else to call this variable, basically determines how urgent the current situation is for
                        //the EOC
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

                i++;
                i2++;
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
            private int i = 1;
            private double inc = 0.75;

            @Override
            public void run() {
                tendrils.rotateAroundLocation(center, inc, 0, 0);
                tendrils.rotate(inc, 0, 0);
                body.rotate(inc, 0, 0);

                if (i == 50) {
                    Location l = center.clone();

                    colorizePhaseTwo();
                    hitbox.setDefense(0);

                    if (center.getWorld().getDifficulty() == Difficulty.EASY) {
                        hitbox.setDamage(9);
                    } else if (center.getWorld().getDifficulty() == Difficulty.NORMAL) {
                        hitbox.setDamage(14);
                    } else {
                        hitbox.setDamage(21);
                    }

                    if (center.getWorld().getDifficulty() != Difficulty.EASY) {
                        for (int i = 0; i < 4 + rng.nextInt(4); i++) {
                            if (servantCount > 10) break;

                            l.zero().add(center).add(rng.nextDouble() - 0.5, rng.nextDouble() - 0.5, rng.nextDouble() - 0.5);

                            new ServantOfCthulhu(l, EyeOfCthulhu.this);
                        }
                    }

                    roar(1.5);
                }

                if (i == 100) {
                    //so that faceAroundBody doesn't get messed up
                    while (body.getPitch() > 90) {
                        tendrils.rotateAroundLocation(center, -90, 0, 0);
                        tendrils.rotate(-90, 0, 0);
                        body.rotate(-90, 0, 0);
                    }

                    hoverAI(60);
                }

                i++;

                if (i <= 50) {
                    inc += 0.75;
                } else {
                    inc -= 0.75;
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 1);
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

    private void colorizePhaseOne() {
        ParticleSphere body = (ParticleSphere) model.getShape(0);

        //before you judge me, its either this or incomprehensible math that would take me days to come up with.
        //pick your poison
        body.addParticle(RED, 0);
        body.addParticle(WHITE, 96);
        body.addParticle(RED, 103);
        body.addParticle(WHITE, 106);
        body.addParticle(RED, 114);
        body.addParticle(WHITE, 116);
        body.addParticle(RED, 123);
        body.addParticle(WHITE, 125);
        body.addParticle(RED, 131);
        body.addParticle(WHITE, 134);
        body.addParticle(RED, 139);
        body.addParticle(WHITE, 141);
        body.addParticle(RED, 142);
        body.addParticle(WHITE, 143);
        body.addParticle(RED, 151);
        body.addParticle(WHITE, 154);
        body.addParticle(RED, 163);
        body.addParticle(WHITE, 165);
        body.addParticle(RED, 176);
        body.addParticle(WHITE, 178);
        body.addParticle(RED, 183);
        body.addParticle(WHITE, 184);
        body.addParticle(RED, 185);
        body.addParticle(WHITE, 187);
        body.addParticle(RED, 191);
        body.addParticle(WHITE, 194);
        body.addParticle(RED, 195);
        body.addParticle(WHITE, 197);
        body.addParticle(RED, 204);
        body.addParticle(WHITE, 205);
        body.addParticle(RED, 206);
        body.addParticle(WHITE, 208);
        body.addParticle(RED, 218);
        body.addParticle(WHITE, 220);
        body.addParticle(RED, 231);
        body.addParticle(WHITE, 234);
        body.addParticle(RED, 239);
        body.addParticle(WHITE, 240);
        body.addParticle(RED, 241);
        body.addParticle(WHITE, 243);
        body.addParticle(RED, 247);
        body.addParticle(WHITE, 250);
        body.addParticle(RED, 253);
        body.addParticle(WHITE, 255);
        body.addParticle(RED, 261);
        body.addParticle(WHITE, 263);
        body.addParticle(RED, 264);
        body.addParticle(WHITE, 266);
        body.addParticle(RED, 276);
        body.addParticle(WHITE, 279);
        body.addParticle(RED, 290);
        body.addParticle(WHITE, 293);
        body.addParticle(RED, 294);
        body.addParticle(WHITE, 295);
        body.addParticle(RED, 298);
        body.addParticle(WHITE, 299);
        body.addParticle(RED, 302);
        body.addParticle(WHITE, 304);
        body.addParticle(RED, 308);
        body.addParticle(WHITE, 311);
        body.addParticle(RED, 313);
        body.addParticle(WHITE, 316);
        body.addParticle(RED, 321);
        body.addParticle(WHITE, 323);
        body.addParticle(RED, 325);
        body.addParticle(WHITE, 327);
        body.addParticle(RED, 336);
        body.addParticle(WHITE, 338);
        body.addParticle(RED, 339);
        body.addParticle(WHITE, 341);
        body.addParticle(RED, 350);
        body.addParticle(WHITE, 353);
        body.addParticle(RED, 355);
        body.addParticle(WHITE, 356);
        body.addParticle(RED, 359);
        body.addParticle(WHITE, 360);
        body.addParticle(RED, 364);
        body.addParticle(WHITE, 366);
        body.addParticle(RED, 370);
        body.addParticle(WHITE, 373);
        body.addParticle(RED, 376);
        body.addParticle(WHITE, 378);
        body.addParticle(RED, 381);
        body.addParticle(WHITE, 384);
        body.addParticle(RED, 387);
        body.addParticle(WHITE, 389);
        body.addParticle(RED, 396);
        body.addParticle(WHITE, 398);
        body.addParticle(RED, 400);
        body.addParticle(WHITE, 403);
        body.addParticle(RED, 410);
        body.addParticle(WHITE, 414);
        body.addParticle(RED, 416);
        body.addParticle(WHITE, 418);
        body.addParticle(RED, 420);
        body.addParticle(WHITE, 422);
        body.addParticle(RED, 426);
        body.addParticle(WHITE, 428);
        body.addParticle(RED, 430);
        body.addParticle(WHITE, 432);
        body.addParticle(RED, 433);
        body.addParticle(WHITE, 435);
        body.addParticle(RED, 437);
        body.addParticle(WHITE, 439);
        body.addParticle(RED, 441);
        body.addParticle(WHITE, 444);
        body.addParticle(RED, 447);
        body.addParticle(WHITE, 450);
        body.addParticle(RED, 450);
        body.addParticle(WHITE, 451);
        body.addParticle(RED, 456);
        body.addParticle(WHITE, 458);
        body.addParticle(RED, 461);
        body.addParticle(WHITE, 464);
        body.addParticle(RED, 469);
        body.addParticle(WHITE, 472);
        body.addParticle(RED, 473);
        body.addParticle(WHITE, 475);
        body.addParticle(RED, 477);
        body.addParticle(WHITE, 480);
        body.addParticle(RED, 481);
        body.addParticle(WHITE, 483);
        body.addParticle(RED, 485);
        body.addParticle(WHITE, 487);
        body.addParticle(RED, 489);
        body.addParticle(WHITE, 491);
        body.addParticle(RED, 493);
        body.addParticle(WHITE, 495);
        body.addParticle(RED, 497);
        body.addParticle(WHITE, 499);
        body.addParticle(RED, 500);
        body.addParticle(WHITE, 503);
        body.addParticle(RED, 505);
        body.addParticle(WHITE, 507);
        body.addParticle(RED, 509);
        body.addParticle(WHITE, 510);
        body.addParticle(RED, 513);
        body.addParticle(WHITE, 515);
        body.addParticle(RED, 519);
        body.addParticle(WHITE, 522);
        body.addParticle(RED, 525);
        body.addParticle(WHITE, 528);
        body.addParticle(RED, 530);
        body.addParticle(WHITE, 532);
        body.addParticle(RED, 533);
        body.addParticle(WHITE, 535);
        body.addParticle(RED, 544);
        body.addParticle(BLUE, 595);
        body.addParticle(OLIVE, 677);
        body.addParticle(GRAY, 706);
        body.addParticle(BLACK, 725);
    }

    //assumes colorizePhaseOne has already been ran
    private void colorizePhaseTwo() {
        ParticleSphere body = (ParticleSphere) model.getShape(0);
        int removeIndex = body.getSecondaryParticleAmount() - 6;
        int amount = body.getSecondaryParticleAmount();

        for (int i = removeIndex; i < amount; i++) {
            body.removeParticle(removeIndex);
        }

        body.addParticle(NONE, 537);
        body.addParticle(RED, 541);
        body.addParticle(DIRTY_WHITE, 542);
        body.addParticle(RED, 544);
        body.addParticle(DIRTY_WHITE, 546);
        body.addParticle(RED, 548);
        body.addParticle(DIRTY_WHITE, 550);
        body.addParticle(RED, 552);
        body.addParticle(DIRTY_WHITE, 554);
        body.addParticle(RED, 556);
        body.addParticle(DIRTY_WHITE, 558);
        body.addParticle(RED, 560);
        body.addParticle(NONE, 561);
        body.addParticle(RED, 567);
        body.addParticle(DIRTY_WHITE, 568);
        body.addParticle(RED, 570);
        body.addParticle(DIRTY_WHITE, 572);
        body.addParticle(RED, 574);
        body.addParticle(DIRTY_WHITE, 576);
        body.addParticle(RED, 578);
        body.addParticle(DIRTY_WHITE, 580);
        body.addParticle(RED, 582);
        body.addParticle(DIRTY_WHITE, 584);
        body.addParticle(RED, 586);
        body.addParticle(NONE, 587);
        body.addParticle(DIRTY_WHITE, 593);
        body.addParticle(RED, 595);
        body.addParticle(DIRTY_WHITE, 597);
        body.addParticle(RED, 599);
        body.addParticle(DIRTY_WHITE, 601);
        body.addParticle(RED, 603);
        body.addParticle(DIRTY_WHITE, 604);
        body.addParticle(RED, 606);
        body.addParticle(DIRTY_WHITE, 607);
        body.addParticle(NONE, 609);
        body.addParticle(DIRTY_WHITE, 616);
        body.addParticle(RED, 618);
        body.addParticle(DIRTY_WHITE, 619);
        body.addParticle(RED, 621);
        body.addParticle(DIRTY_WHITE, 623);
        body.addParticle(RED, 625);
        body.addParticle(DIRTY_WHITE, 626);
        body.addParticle(RED, 628);
        body.addParticle(DIRTY_WHITE, 630);
        body.addParticle(NONE, 632);
        body.addParticle(DIRTY_WHITE, 637);
        body.addParticle(NONE, 639);
        body.addParticle(DIRTY_WHITE, 641);
        body.addParticle(RED, 643);
        body.addParticle(DIRTY_WHITE, 644);
        body.addParticle(NONE, 646);
        body.addParticle(DIRTY_WHITE, 647);
        body.addParticle(NONE, 649);
        body.addParticle(DIRTY_WHITE, 650);
        body.addParticle(NONE, 652);
        body.addParticle(DIRTY_WHITE, 656);
        body.addParticle(NONE, 658);
        body.addParticle(DIRTY_WHITE, 659);
        body.addParticle(RED, 661);
        body.addParticle(DIRTY_WHITE, 663);
        body.addParticle(RED, 665);
        body.addParticle(DIRTY_WHITE, 666);
        body.addParticle(NONE, 668);
        body.addParticle(DIRTY_WHITE, 669);
        body.addParticle(NONE, 671);
        body.addParticle(DIRTY_WHITE, 674);
        body.addParticle(NONE, 676);
        body.addParticle(DIRTY_WHITE, 678);
        body.addParticle(NONE, 679);
        body.addParticle(DIRTY_WHITE, 681);
        body.addParticle(NONE, 682);
        body.addParticle(DIRTY_WHITE, 685);
        body.addParticle(NONE, 686);
        body.addParticle(DIRTY_WHITE, 689);
        body.addParticle(NONE, 690);
        body.addParticle(DIRTY_WHITE, 692);
        body.addParticle(NONE, 693);
        body.addParticle(DIRTY_WHITE, 695);
        body.addParticle(NONE, 696);
        body.addParticle(DIRTY_WHITE, 697);
        body.addParticle(NONE, 698);
        body.addParticle(DIRTY_WHITE, 700);
        body.addParticle(NONE, 701);
    }
}
