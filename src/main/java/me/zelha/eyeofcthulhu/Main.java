package me.zelha.eyeofcthulhu;

import hm.zelha.particlesfx.particles.ParticleDust;
import hm.zelha.particlesfx.particles.ParticleNull;
import hm.zelha.particlesfx.particles.parents.Particle;
import hm.zelha.particlesfx.shapers.ParticleLine;
import hm.zelha.particlesfx.shapers.ParticleSphere;
import hm.zelha.particlesfx.util.LocationSafe;
import hm.zelha.particlesfx.util.ParticleShapeCompound;
import me.zelha.eyeofcthulhu.commands.AddCommand;
import me.zelha.eyeofcthulhu.commands.RemoveCommand;
import me.zelha.eyeofcthulhu.commands.SoutCommand;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ThreadLocalRandom;

public final class Main extends JavaPlugin {

    public static ParticleShapeCompound eoc = new ParticleShapeCompound();
    private final ThreadLocalRandom rng = ThreadLocalRandom.current();
    private final Particle white = new ParticleDust(Color.WHITE).setPureColor(true);
    private final Particle dirtyWhite = new ParticleDust(Color.WHITE);
    private final Particle black = new ParticleDust(Color.BLACK);
    private final Particle grey = new ParticleDust(Color.GRAY, 35);
    private final Particle red = new ParticleDust(Color.RED, 85);
    private final Particle blue = new ParticleDust(Color.BLUE);
    private final Particle olive = new ParticleDust(Color.OLIVE);
    private final Particle none = new ParticleNull();

    @Override
    public void onEnable() {
        World world = Bukkit.getWorld("world");
        LocationSafe center = new LocationSafe(world, 0, 100, 0);
        Particle red = new ParticleDust(Color.RED, 75);
        ParticleSphere body = new ParticleSphere(new ParticleDust(Color.WHITE).setPureColor(true), center, 3, 3, 3, 20, 750);

        eoc.addShape(body);

        for (int i = 0; i < 10; i++) {
            eoc.addShape(new ParticleLine(red, 30, new LocationSafe(world, 0, 103, 0), new LocationSafe(world, 0, 106.5, 0)));
        }

        for (int i = 0; i < 5; i++) {
            eoc.addShape(new ParticleLine(red, 10, new LocationSafe(world, 0, 103, 0), new LocationSafe(world, 0, 104, 0)));
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

            tendril.setMechanic((particle, location, vector) -> location.add(rng.nextDouble(0.4) - 0.2, rng.nextDouble(0.4) - 0.2, rng.nextDouble(0.4) - 0.2));
        }

        getCommand("add").setExecutor(new AddCommand());
        getCommand("remove").setExecutor(new RemoveCommand());
        getCommand("sout").setExecutor(new SoutCommand());
    }
    
    private void colorizePhase1() {
        ParticleSphere body = (ParticleSphere) eoc.getShape(0);

        body.addParticle(red, 0);
        body.addParticle(white, 102);
        body.addParticle(red, 107);
        body.addParticle(white, 108);
        body.addParticle(red, 114);
        body.addParticle(white, 116);
        body.addParticle(red, 124);
        body.addParticle(white, 127);
        body.addParticle(red, 135);
        body.addParticle(white, 137);
        body.addParticle(red, 144);
        body.addParticle(white, 146);
        body.addParticle(red, 152);
        body.addParticle(white, 155);
        body.addParticle(red, 160);
        body.addParticle(white, 162);
        body.addParticle(red, 163);
        body.addParticle(white, 164);
        body.addParticle(red, 172);
        body.addParticle(white, 175);
        body.addParticle(red, 184);
        body.addParticle(white, 186);
        body.addParticle(red, 197);
        body.addParticle(white, 199);
        body.addParticle(red, 204);
        body.addParticle(white, 205);
        body.addParticle(red, 206);
        body.addParticle(white, 208);
        body.addParticle(red, 212);
        body.addParticle(white, 215);
        body.addParticle(red, 216);
        body.addParticle(white, 218);
        body.addParticle(red, 225);
        body.addParticle(white, 226);
        body.addParticle(red, 227);
        body.addParticle(white, 229);
        body.addParticle(red, 239);
        body.addParticle(white, 241);
        body.addParticle(red, 252);
        body.addParticle(white, 255);
        body.addParticle(red, 260);
        body.addParticle(white, 261);
        body.addParticle(red, 262);
        body.addParticle(white, 264);
        body.addParticle(red, 268);
        body.addParticle(white, 271);
        body.addParticle(red, 274);
        body.addParticle(white, 276);
        body.addParticle(red, 282);
        body.addParticle(white, 284);
        body.addParticle(red, 285);
        body.addParticle(white, 287);
        body.addParticle(red, 297);
        body.addParticle(white, 300);
        body.addParticle(red, 311);
        body.addParticle(white, 314);
        body.addParticle(red, 315);
        body.addParticle(white, 316);
        body.addParticle(red, 319);
        body.addParticle(white, 320);
        body.addParticle(red, 323);
        body.addParticle(white, 325);
        body.addParticle(red, 329);
        body.addParticle(white, 332);
        body.addParticle(red, 335);
        body.addParticle(white, 337);
        body.addParticle(red, 342);
        body.addParticle(white, 344);
        body.addParticle(red, 346);
        body.addParticle(white, 348);
        body.addParticle(red, 357);
        body.addParticle(white, 359);
        body.addParticle(red, 360);
        body.addParticle(white, 362);
        body.addParticle(red, 371);
        body.addParticle(white, 374);
        body.addParticle(red, 376);
        body.addParticle(white, 377);
        body.addParticle(red, 380);
        body.addParticle(white, 381);
        body.addParticle(red, 385);
        body.addParticle(white, 387);
        body.addParticle(red, 391);
        body.addParticle(white, 394);
        body.addParticle(red, 397);
        body.addParticle(white, 399);
        body.addParticle(red, 402);
        body.addParticle(white, 405);
        body.addParticle(red, 408);
        body.addParticle(white, 410);
        body.addParticle(red, 417);
        body.addParticle(white, 419);
        body.addParticle(red, 421);
        body.addParticle(white, 424);
        body.addParticle(red, 431);
        body.addParticle(white, 435);
        body.addParticle(red, 437);
        body.addParticle(white, 439);
        body.addParticle(red, 441);
        body.addParticle(white, 443);
        body.addParticle(red, 447);
        body.addParticle(white, 449);
        body.addParticle(red, 451);
        body.addParticle(white, 453);
        body.addParticle(red, 454);
        body.addParticle(white, 456);
        body.addParticle(red, 458);
        body.addParticle(white, 460);
        body.addParticle(red, 462);
        body.addParticle(white, 465);
        body.addParticle(red, 468);
        body.addParticle(white, 470);
        body.addParticle(red, 471);
        body.addParticle(white, 472);
        body.addParticle(red, 477);
        body.addParticle(white, 479);
        body.addParticle(red, 482);
        body.addParticle(white, 485);
        body.addParticle(red, 490);
        body.addParticle(white, 493);
        body.addParticle(red, 494);
        body.addParticle(white, 496);
        body.addParticle(red, 498);
        body.addParticle(white, 500);
        body.addParticle(red, 502);
        body.addParticle(white, 504);
        body.addParticle(red, 506);
        body.addParticle(white, 508);
        body.addParticle(red, 510);
        body.addParticle(white, 512);
        body.addParticle(red, 514);
        body.addParticle(white, 516);
        body.addParticle(red, 518);
        body.addParticle(white, 520);
        body.addParticle(red, 521);
        body.addParticle(white, 524);
        body.addParticle(red, 526);
        body.addParticle(white, 528);
        body.addParticle(red, 530);
        body.addParticle(white, 531);
        body.addParticle(red, 534);
        body.addParticle(white, 536);
        body.addParticle(red, 540);
        body.addParticle(white, 543);
        body.addParticle(red, 546);
        body.addParticle(white, 549);
        body.addParticle(red, 551);
        body.addParticle(white, 553);
        body.addParticle(red, 554);
        body.addParticle(white, 556);
        body.addParticle(red, 558);
        body.addParticle(blue, 611);
        body.addParticle(olive, 694);
        body.addParticle(grey, 724);
        body.addParticle(black, 745);
    }
    
    public void colorizePhase2() {
        ParticleSphere body = (ParticleSphere) eoc.getShape(0);
        int removeIndex = body.getSecondaryParticleAmount() - 6;
        int amount = body.getSecondaryParticleAmount();

        for (int i = removeIndex; i < amount; i++) {
            body.removeParticle(removeIndex);
        }

        body.addParticle(none, 558);
        body.addParticle(red, 562);
        body.addParticle(dirtyWhite, 563);
        body.addParticle(red, 565);
        body.addParticle(dirtyWhite, 567);
        body.addParticle(red, 569);
        body.addParticle(dirtyWhite, 571);
        body.addParticle(red, 573);
        body.addParticle(dirtyWhite, 575);
        body.addParticle(red, 577);
        body.addParticle(dirtyWhite, 579);
        body.addParticle(red, 581);
        body.addParticle(none, 582);
        body.addParticle(red, 588);
        body.addParticle(dirtyWhite, 589);
        body.addParticle(red, 591);
        body.addParticle(dirtyWhite, 593);
        body.addParticle(red, 595);
        body.addParticle(dirtyWhite, 597);
        body.addParticle(red, 599);
        body.addParticle(dirtyWhite, 601);
        body.addParticle(red, 603);
        body.addParticle(dirtyWhite, 605);
        body.addParticle(red, 607);
        body.addParticle(none, 608);
        body.addParticle(dirtyWhite, 614);
        body.addParticle(red, 616);
        body.addParticle(dirtyWhite, 618);
        body.addParticle(red, 620);
        body.addParticle(dirtyWhite, 622);
        body.addParticle(red, 624);
        body.addParticle(dirtyWhite, 625);
        body.addParticle(red, 627);
        body.addParticle(dirtyWhite, 628);
        body.addParticle(none, 630);
        body.addParticle(dirtyWhite, 637);
        body.addParticle(red, 639);
        body.addParticle(dirtyWhite, 640);
        body.addParticle(red, 642);
        body.addParticle(dirtyWhite, 644);
        body.addParticle(red, 646);
        body.addParticle(dirtyWhite, 647);
        body.addParticle(red, 649);
        body.addParticle(dirtyWhite, 651);
        body.addParticle(none, 653);
        body.addParticle(dirtyWhite, 658);
        body.addParticle(none, 660);
        body.addParticle(dirtyWhite, 662);
        body.addParticle(red, 664);
        body.addParticle(dirtyWhite, 665);
        body.addParticle(none, 667);
        body.addParticle(dirtyWhite, 668);
        body.addParticle(none, 670);
        body.addParticle(dirtyWhite, 671);
        body.addParticle(none, 673);
        body.addParticle(dirtyWhite, 677);
        body.addParticle(none, 679);
        body.addParticle(dirtyWhite, 680);
        body.addParticle(red, 682);
        body.addParticle(dirtyWhite, 684);
        body.addParticle(red, 686);
        body.addParticle(dirtyWhite, 687);
        body.addParticle(none, 689);
        body.addParticle(dirtyWhite, 690);
        body.addParticle(none, 692);
        body.addParticle(dirtyWhite, 695);
        body.addParticle(none, 696);
        body.addParticle(dirtyWhite, 699);
        body.addParticle(none, 700);
        body.addParticle(dirtyWhite, 702);
        body.addParticle(none, 703);
        body.addParticle(dirtyWhite, 706);
        body.addParticle(none, 707);
        body.addParticle(dirtyWhite, 710);
        body.addParticle(none, 711);
        body.addParticle(dirtyWhite, 713);
        body.addParticle(none, 714);
        body.addParticle(dirtyWhite, 716);
        body.addParticle(none, 717);
        body.addParticle(dirtyWhite, 718);
        body.addParticle(none, 719);
        body.addParticle(dirtyWhite, 721);
        body.addParticle(none, 722);
    }

    public static ParticleShapeCompound getEoc() {
        return eoc;
    }
}
