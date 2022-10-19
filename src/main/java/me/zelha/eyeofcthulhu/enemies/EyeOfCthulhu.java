package me.zelha.eyeofcthulhu.enemies;

import hm.zelha.particlesfx.particles.ParticleDust;
import hm.zelha.particlesfx.particles.ParticleNull;
import hm.zelha.particlesfx.particles.parents.Particle;
import hm.zelha.particlesfx.shapers.ParticleLine;
import hm.zelha.particlesfx.shapers.ParticleSphere;
import hm.zelha.particlesfx.util.LocationSafe;
import hm.zelha.particlesfx.util.ParticleShapeCompound;
import me.zelha.eyeofcthulhu.util.BossBar;
import me.zelha.eyeofcthulhu.util.Hitbox;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.concurrent.ThreadLocalRandom;

public class EyeOfCthulhu {

    private static final Particle WHITE = new ParticleDust(Color.WHITE, 100, 0.25, 0.25, 0.25, 1).setPureColor(true);
    private static final Particle DIRTY_WHITE = new ParticleDust(Color.fromRGB(255, 255, 200), 75);
    private static final Particle BLACK = new ParticleDust(Color.BLACK, 100, 0.25, 0.25, 0.25, 2);
    private static final Particle GRAY = new ParticleDust(Color.GRAY, 35, 0.25, 0.25, 0.25, 2);
    private static final Particle RED = new ParticleDust(Color.RED, 85, 0.25, 0.25, 0.25, 1);
    private static final Particle BLUE = new ParticleDust(Color.BLUE, 100, 0.25, 0.25, 0.25, 2);
    private static final Particle OLIVE = new ParticleDust(Color.OLIVE, 100, 0.25, 0.25, 0.25, 2);
    private static final Particle NONE = new ParticleNull();
    private final ThreadLocalRandom rng = ThreadLocalRandom.current();
    private final ParticleShapeCompound eoc = new ParticleShapeCompound();
    private final Hitbox hitbox;

    public EyeOfCthulhu(Location location) {
        World world = location.getWorld();
        LocationSafe center = new LocationSafe(world, 0, 0, 0);
        Particle tendrilRed = new ParticleDust(Color.RED, 75);
        this.hitbox = new Hitbox(eoc, 7.5, 6, center, 1000, new BossBar(center, "Eye of Cthulhu", 1000));

        center.add(location);
        //center.add(rng.nextInt(100) - 50, 500, rng.nextInt(100) - 50);
        eoc.addShape(new ParticleSphere(WHITE, center, 3, 3, 3, 20, 750));

        for (int i = 0; i < 10; i++) {
            eoc.addShape(new ParticleLine(tendrilRed, 30,
                    new LocationSafe(world, center.getX(), center.getY() + 3, center.getZ()),
                    new LocationSafe(world, center.getX(), center.getY() + 6.5, center.getZ()))
            );
        }

        for (int i = 0; i < 5; i++) {
            eoc.addShape(new ParticleLine(tendrilRed, 10,
                    new LocationSafe(world, center.getX(), center.getY() + 3, center.getZ()),
                    new LocationSafe(world, center.getX(), center.getY() + 4, center.getZ()))
            );
        }

        for (int i = 0; i < 15; i++) {
            ParticleLine tendril = (ParticleLine) eoc.getShape(i + 1);

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
    }

    private void colorizePhaseOne() {
        ParticleSphere body = (ParticleSphere) eoc.getShape(0);

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
        ParticleSphere body = (ParticleSphere) eoc.getShape(0);
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