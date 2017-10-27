package gg.revival.factions.core.classes.cont;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import gg.revival.factions.core.classes.ClassType;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Bard extends RClass {

    public Bard() {
        super(Lists.newArrayList(), Maps.newHashMap(), ClassType.BARD);

        PotionEffect passiveWeakness = new PotionEffect(PotionEffectType.WEAKNESS, Integer.MAX_VALUE, 3);
        PotionEffect passiveResistance = new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1);
        PotionEffect passiveSpeed = new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2);

        PotionEffect activeSpeed = new PotionEffect(PotionEffectType.SPEED, 20 * 8, 3);
        PotionEffect activeStrength = new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 4, 1);
        PotionEffect activeRegen = new PotionEffect(PotionEffectType.REGENERATION, 20 * 8, 1);
        PotionEffect activeJumpboost = new PotionEffect(PotionEffectType.JUMP, 20 * 8, 6);

        getPassives().add(passiveWeakness);
        getPassives().add(passiveResistance);
        getPassives().add(passiveSpeed);

        getActives().put(Material.SUGAR, activeSpeed);
        getActives().put(Material.BLAZE_POWDER, activeStrength);
        getActives().put(Material.GHAST_TEAR, activeRegen);
        getActives().put(Material.FEATHER, activeJumpboost);
    }

}
