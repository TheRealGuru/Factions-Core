package gg.revival.factions.core.classes.cont;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import gg.revival.factions.core.classes.ClassType;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Scout extends RClass {

    public Scout() {
        super(Lists.newArrayList(), Maps.newHashMap(), ClassType.SCOUT);

        // TODO: Get values from config
        PotionEffect passiveSpeed = new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2);
        PotionEffect passiveJumpboost = new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 0);

        PotionEffect activeSpeed = new PotionEffect(PotionEffectType.SPEED, 20 * 8, 4);
        PotionEffect activeJumpboost = new PotionEffect(PotionEffectType.JUMP, 20 * 8, 3);

        getPassives().add(passiveSpeed);
        getPassives().add(passiveJumpboost);
        getActives().put(Material.SUGAR, activeSpeed);
        getActives().put(Material.FEATHER, activeJumpboost);
    }

}
