package gg.revival.factions.core.classes.cont;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import gg.revival.factions.core.classes.ClassType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Miner extends RClass {

    public Miner() {
        super(Lists.newArrayList(), Maps.newHashMap(), ClassType.MINER);

        PotionEffect passiveInvis = new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0);
        PotionEffect passiveNightvision = new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0);
        PotionEffect passiveHaste = new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 1);

        getPassives().add(passiveInvis);
        getPassives().add(passiveNightvision);
        getPassives().add(passiveHaste);
    }

}
