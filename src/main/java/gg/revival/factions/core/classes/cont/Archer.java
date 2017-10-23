package gg.revival.factions.core.classes.cont;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import gg.revival.factions.core.classes.ClassType;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Archer extends RClass {

    public Archer() {
        super(Lists.newArrayList(), Maps.newHashMap(), ClassType.ARCHER);

        PotionEffect passiveSpeed = new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2);

        PotionEffect activeSpeed = new PotionEffect(PotionEffectType.SPEED, 20 * 8, 3);
        PotionEffect activeJumpboost = new PotionEffect(PotionEffectType.JUMP, 20 * 8, 6);

        getPassives().add(passiveSpeed);
        getActives().put(Material.SUGAR, activeSpeed);
        getActives().put(Material.FEATHER, activeJumpboost);
    }

}
