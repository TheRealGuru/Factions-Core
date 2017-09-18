package gg.revival.factions.core.classes.cont;

import gg.revival.factions.core.classes.ClassType;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RClass {

    @Getter List<PotionEffect> passives = new ArrayList<>();
    @Getter Map<Material, PotionEffect> actives = new HashMap<>();

    @Getter ClassType type;

    public RClass(List<PotionEffect> passives, Map<Material, PotionEffect> actives, ClassType type) {
        this.passives = passives;
        this.actives = actives;
        this.type = type;
    }
}
