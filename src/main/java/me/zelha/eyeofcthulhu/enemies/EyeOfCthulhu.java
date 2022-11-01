package me.zelha.eyeofcthulhu.enemies;

import hm.zelha.particlesfx.particles.ParticleDust;
import hm.zelha.particlesfx.particles.ParticleExplosionHuge;
import hm.zelha.particlesfx.particles.ParticleNull;
import hm.zelha.particlesfx.particles.parents.Particle;
import hm.zelha.particlesfx.shapers.ParticleLine;
import hm.zelha.particlesfx.shapers.ParticleSphere;
import hm.zelha.particlesfx.shapers.parents.Shape;
import hm.zelha.particlesfx.util.LVMath;
import hm.zelha.particlesfx.util.LocationSafe;
import hm.zelha.particlesfx.util.ParticleShapeCompound;
import hm.zelha.particlesfx.util.Rotation;
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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EyeOfCthulhu extends ParticleEnemy {

    //TODO implement difficulty-based changes
    // EOC gradually speeds up as it dies
    // in hard mode the boss gains more health depending on the amount of players nearby when summoned

    private static final Particle WHITE = new ParticleDust(Color.WHITE, 100, 0.2, 0.2, 0.2, 1).setPureColor(true);
    private static final Particle DIRTY_WHITE = new ParticleDust(Color.fromRGB(255, 255, 200), 75);
    private static final Particle BLACK = new ParticleDust(Color.BLACK, 100, 0.2, 0.2, 0.2, 2);
    private static final Particle GRAY = new ParticleDust(Color.GRAY, 35, 0.2, 0.2, 0.2, 2);
    private static final Particle RED = new ParticleDust(Color.RED, 85, 0.2, 0.2, 0.2, 1);
    private static final Particle BLUE = new ParticleDust(Color.BLUE, 100, 0.2, 0.2, 0.2, 1);
    private static final Particle OLIVE = new ParticleDust(Color.OLIVE, 100, 0.2, 0.2, 0.2, 2);
    private static final Particle NONE = new ParticleNull();
    private static final Particle EXPLOSION = new ParticleExplosionHuge();
    private final Map<UUID, Double> damageMap = new HashMap<>();
    //i messed up while making phase 2's color and im Not Going To Redo It.
    private final Rotation teethFixer = new Rotation(0, 83, 0);
    private final Location locationHelper;
    private final Vector vectorHelper = new Vector(0, 0, 0);
    private BukkitTask currentAI = null;
    private boolean phaseTwo = false;
    private int servantCount = 0;

    public EyeOfCthulhu(Location location) {
        World world = location.getWorld();
        Particle tendrilRed = new ParticleDust(Color.RED, 75);
        ParticleShapeCompound tendrils = new ParticleShapeCompound();
        LocationSafe center = new LocationSafe(world, 0, 0, 0);

        center.add(location);
        center.add(rng.nextInt(100) - 50, 150, rng.nextInt(100) - 50);

        ParticleSphere body = new ParticleSphere(WHITE, center, 3, 3, 3, 20, 750);
        super.hitbox = new Hitbox(this, center, 7.5, 6, 1000, "Eye of Cthulhu", true);
        this.locationHelper = center.cloneToLocation();

        hitbox.setDefense(6);
        model.addShape(body);
        body.setMechanic((particle, location1, vector) -> teethFixer.apply(vector));
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

            tendril.setMechanic((particle, l, vector) ->
                l.add(rng.nextDouble(0.4) - 0.2, rng.nextDouble(0.4) - 0.2, rng.nextDouble(0.4) - 0.2)
            );
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
        if (hitbox.getSlime().getHealth() - damage <= hitbox.getSlime().getMaxHealth() / 2 && !phaseTwo) {
            switchPhase();
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
        findTarget(50);
        hoverAI(200);
    }

    private void hoverAI(int time) {
        if (currentAI != null) currentAI.cancel();

        currentAI = new BukkitRunnable() {

            private final Location location = ((ParticleSphere) model.getShape(0)).getCenter();
            private final int servantSpawn = ((time - 10) / (3 + rng.nextInt(1)));
            private int i = 0;

            @Override
            public void run() {
                if (target == null) {
                    findTarget(50);
                    return;
                }

                locationHelper.zero().add(target.locX, target.locY, target.locZ);

                if (i % servantSpawn == 0 && !phaseTwo && locationHelper.distance(location) < 25 && servantCount <= 15) {
                    new ServantOfCthulhu(location, EyeOfCthulhu.this);
                }

                locationHelper.add(0, target.length + 7.5, 0);
                LVMath.subtractToVector(vectorHelper, locationHelper, location);
                vectorHelper.normalize().multiply(0.25);
                model.move(vectorHelper);
                locationHelper.zero().add(target.locX, target.locY + (target.length / 2), target.locZ);
                faceAroundBody(locationHelper);
                damageNearby(location, 1);

                if (i == time) {
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
                if (target == null || !target.valid || !target.isAlive()) {
                    findTarget(50);
                    return;
                }

                if (i == 0 || (i2 % dashTime == 0 && !waiting)) {
                    locationHelper.zero().add(target.locX, target.locY + (target.length / 2), target.locZ);
                    LVMath.subtractToVector(vectorHelper, locationHelper, location);
                    vectorHelper.normalize().multiply(20D / dashTime * 2);
                    faceAroundBody(locationHelper);

                    if (i != 0) {
                        i2 = 1;
                        waiting = true;
                    }
                } else if (waiting && i2 % waitTime == 0) {
                    i2 = 1;
                    waiting = false;
                }

                if (i2 == 1 && !waiting && phaseTwo) {
                    roar(1.5);
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
                    if (phaseTwo) {
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
            private final Shape tendrils = model.getShape(1);
            private int i = 1;
            private double inc = 0.75;

            @Override
            public void run() {
                tendrils.rotateAroundLocation(body.getCenter(), inc, 0, 0);
                tendrils.rotate(inc, 0, 0);
                body.rotate(inc, 0, 0);

                if (i == 50) {
                    colorizePhaseTwo();
                    hitbox.setDefense(0);
                    hitbox.setDamage(9);
                    roar(1.5);
                }

                if (i == 100) {
                    //so that faceAroundBody doesn't get messed up
                    while (body.getPitch() > 90) {
                        tendrils.rotateAroundLocation(body.getCenter(), -90, 0, 0);
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
        body.addParticle(WHITE, 102);
        body.addParticle(RED, 107);
        body.addParticle(WHITE, 108);
        body.addParticle(RED, 114);
        body.addParticle(WHITE, 116);
        body.addParticle(RED, 124);
        body.addParticle(WHITE, 127);
        body.addParticle(RED, 135);
        body.addParticle(WHITE, 137);
        body.addParticle(RED, 144);
        body.addParticle(WHITE, 146);
        body.addParticle(RED, 152);
        body.addParticle(WHITE, 155);
        body.addParticle(RED, 160);
        body.addParticle(WHITE, 162);
        body.addParticle(RED, 163);
        body.addParticle(WHITE, 164);
        body.addParticle(RED, 172);
        body.addParticle(WHITE, 175);
        body.addParticle(RED, 184);
        body.addParticle(WHITE, 186);
        body.addParticle(RED, 197);
        body.addParticle(WHITE, 199);
        body.addParticle(RED, 204);
        body.addParticle(WHITE, 205);
        body.addParticle(RED, 206);
        body.addParticle(WHITE, 208);
        body.addParticle(RED, 212);
        body.addParticle(WHITE, 215);
        body.addParticle(RED, 216);
        body.addParticle(WHITE, 218);
        body.addParticle(RED, 225);
        body.addParticle(WHITE, 226);
        body.addParticle(RED, 227);
        body.addParticle(WHITE, 229);
        body.addParticle(RED, 239);
        body.addParticle(WHITE, 241);
        body.addParticle(RED, 252);
        body.addParticle(WHITE, 255);
        body.addParticle(RED, 260);
        body.addParticle(WHITE, 261);
        body.addParticle(RED, 262);
        body.addParticle(WHITE, 264);
        body.addParticle(RED, 268);
        body.addParticle(WHITE, 271);
        body.addParticle(RED, 274);
        body.addParticle(WHITE, 276);
        body.addParticle(RED, 282);
        body.addParticle(WHITE, 284);
        body.addParticle(RED, 285);
        body.addParticle(WHITE, 287);
        body.addParticle(RED, 297);
        body.addParticle(WHITE, 300);
        body.addParticle(RED, 311);
        body.addParticle(WHITE, 314);
        body.addParticle(RED, 315);
        body.addParticle(WHITE, 316);
        body.addParticle(RED, 319);
        body.addParticle(WHITE, 320);
        body.addParticle(RED, 323);
        body.addParticle(WHITE, 325);
        body.addParticle(RED, 329);
        body.addParticle(WHITE, 332);
        body.addParticle(RED, 335);
        body.addParticle(WHITE, 337);
        body.addParticle(RED, 342);
        body.addParticle(WHITE, 344);
        body.addParticle(RED, 346);
        body.addParticle(WHITE, 348);
        body.addParticle(RED, 357);
        body.addParticle(WHITE, 359);
        body.addParticle(RED, 360);
        body.addParticle(WHITE, 362);
        body.addParticle(RED, 371);
        body.addParticle(WHITE, 374);
        body.addParticle(RED, 376);
        body.addParticle(WHITE, 377);
        body.addParticle(RED, 380);
        body.addParticle(WHITE, 381);
        body.addParticle(RED, 385);
        body.addParticle(WHITE, 387);
        body.addParticle(RED, 391);
        body.addParticle(WHITE, 394);
        body.addParticle(RED, 397);
        body.addParticle(WHITE, 399);
        body.addParticle(RED, 402);
        body.addParticle(WHITE, 405);
        body.addParticle(RED, 408);
        body.addParticle(WHITE, 410);
        body.addParticle(RED, 417);
        body.addParticle(WHITE, 419);
        body.addParticle(RED, 421);
        body.addParticle(WHITE, 424);
        body.addParticle(RED, 431);
        body.addParticle(WHITE, 435);
        body.addParticle(RED, 437);
        body.addParticle(WHITE, 439);
        body.addParticle(RED, 441);
        body.addParticle(WHITE, 443);
        body.addParticle(RED, 447);
        body.addParticle(WHITE, 449);
        body.addParticle(RED, 451);
        body.addParticle(WHITE, 453);
        body.addParticle(RED, 454);
        body.addParticle(WHITE, 456);
        body.addParticle(RED, 458);
        body.addParticle(WHITE, 460);
        body.addParticle(RED, 462);
        body.addParticle(WHITE, 465);
        body.addParticle(RED, 468);
        body.addParticle(WHITE, 470);
        body.addParticle(RED, 471);
        body.addParticle(WHITE, 472);
        body.addParticle(RED, 477);
        body.addParticle(WHITE, 479);
        body.addParticle(RED, 482);
        body.addParticle(WHITE, 485);
        body.addParticle(RED, 490);
        body.addParticle(WHITE, 493);
        body.addParticle(RED, 494);
        body.addParticle(WHITE, 496);
        body.addParticle(RED, 498);
        body.addParticle(WHITE, 500);
        body.addParticle(RED, 502);
        body.addParticle(WHITE, 504);
        body.addParticle(RED, 506);
        body.addParticle(WHITE, 508);
        body.addParticle(RED, 510);
        body.addParticle(WHITE, 512);
        body.addParticle(RED, 514);
        body.addParticle(WHITE, 516);
        body.addParticle(RED, 518);
        body.addParticle(WHITE, 520);
        body.addParticle(RED, 521);
        body.addParticle(WHITE, 524);
        body.addParticle(RED, 526);
        body.addParticle(WHITE, 528);
        body.addParticle(RED, 530);
        body.addParticle(WHITE, 531);
        body.addParticle(RED, 534);
        body.addParticle(WHITE, 536);
        body.addParticle(RED, 540);
        body.addParticle(WHITE, 543);
        body.addParticle(RED, 546);
        body.addParticle(WHITE, 549);
        body.addParticle(RED, 551);
        body.addParticle(WHITE, 553);
        body.addParticle(RED, 554);
        body.addParticle(WHITE, 556);
        body.addParticle(RED, 558);
        body.addParticle(BLUE, 611);
        body.addParticle(OLIVE, 694);
        body.addParticle(GRAY, 724);
        body.addParticle(BLACK, 745);
    }

    //assumes colorizePhaseOne has already been ran
    private void colorizePhaseTwo() {
        ParticleSphere body = (ParticleSphere) model.getShape(0);
        int removeIndex = body.getSecondaryParticleAmount() - 6;
        int amount = body.getSecondaryParticleAmount();

        for (int i = removeIndex; i < amount; i++) {
            body.removeParticle(removeIndex);
        }

        body.addParticle(NONE, 558);
        body.addParticle(RED, 562);
        body.addParticle(DIRTY_WHITE, 563);
        body.addParticle(RED, 565);
        body.addParticle(DIRTY_WHITE, 567);
        body.addParticle(RED, 569);
        body.addParticle(DIRTY_WHITE, 571);
        body.addParticle(RED, 573);
        body.addParticle(DIRTY_WHITE, 575);
        body.addParticle(RED, 577);
        body.addParticle(DIRTY_WHITE, 579);
        body.addParticle(RED, 581);
        body.addParticle(NONE, 582);
        body.addParticle(RED, 588);
        body.addParticle(DIRTY_WHITE, 589);
        body.addParticle(RED, 591);
        body.addParticle(DIRTY_WHITE, 593);
        body.addParticle(RED, 595);
        body.addParticle(DIRTY_WHITE, 597);
        body.addParticle(RED, 599);
        body.addParticle(DIRTY_WHITE, 601);
        body.addParticle(RED, 603);
        body.addParticle(DIRTY_WHITE, 605);
        body.addParticle(RED, 607);
        body.addParticle(NONE, 608);
        body.addParticle(DIRTY_WHITE, 614);
        body.addParticle(RED, 616);
        body.addParticle(DIRTY_WHITE, 618);
        body.addParticle(RED, 620);
        body.addParticle(DIRTY_WHITE, 622);
        body.addParticle(RED, 624);
        body.addParticle(DIRTY_WHITE, 625);
        body.addParticle(RED, 627);
        body.addParticle(DIRTY_WHITE, 628);
        body.addParticle(NONE, 630);
        body.addParticle(DIRTY_WHITE, 637);
        body.addParticle(RED, 639);
        body.addParticle(DIRTY_WHITE, 640);
        body.addParticle(RED, 642);
        body.addParticle(DIRTY_WHITE, 644);
        body.addParticle(RED, 646);
        body.addParticle(DIRTY_WHITE, 647);
        body.addParticle(RED, 649);
        body.addParticle(DIRTY_WHITE, 651);
        body.addParticle(NONE, 653);
        body.addParticle(DIRTY_WHITE, 658);
        body.addParticle(NONE, 660);
        body.addParticle(DIRTY_WHITE, 662);
        body.addParticle(RED, 664);
        body.addParticle(DIRTY_WHITE, 665);
        body.addParticle(NONE, 667);
        body.addParticle(DIRTY_WHITE, 668);
        body.addParticle(NONE, 670);
        body.addParticle(DIRTY_WHITE, 671);
        body.addParticle(NONE, 673);
        body.addParticle(DIRTY_WHITE, 677);
        body.addParticle(NONE, 679);
        body.addParticle(DIRTY_WHITE, 680);
        body.addParticle(RED, 682);
        body.addParticle(DIRTY_WHITE, 684);
        body.addParticle(RED, 686);
        body.addParticle(DIRTY_WHITE, 687);
        body.addParticle(NONE, 689);
        body.addParticle(DIRTY_WHITE, 690);
        body.addParticle(NONE, 692);
        body.addParticle(DIRTY_WHITE, 695);
        body.addParticle(NONE, 696);
        body.addParticle(DIRTY_WHITE, 699);
        body.addParticle(NONE, 700);
        body.addParticle(DIRTY_WHITE, 702);
        body.addParticle(NONE, 703);
        body.addParticle(DIRTY_WHITE, 706);
        body.addParticle(NONE, 707);
        body.addParticle(DIRTY_WHITE, 710);
        body.addParticle(NONE, 711);
        body.addParticle(DIRTY_WHITE, 713);
        body.addParticle(NONE, 714);
        body.addParticle(DIRTY_WHITE, 716);
        body.addParticle(NONE, 717);
        body.addParticle(DIRTY_WHITE, 718);
        body.addParticle(NONE, 719);
        body.addParticle(DIRTY_WHITE, 721);
        body.addParticle(NONE, 722);
    }
}
