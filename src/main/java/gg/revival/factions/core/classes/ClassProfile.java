package gg.revival.factions.core.classes;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

import java.util.Map;
import java.util.UUID;

public class ClassProfile {

    @Getter UUID uuid;
    @Getter @Setter ClassType selectedClass;
    @Getter @Setter boolean active;

    @Getter Map<Material, Long> consumeCooldowns;

    ClassProfile(UUID uuid) {
        this.uuid = uuid;
        this.active = false;
        this.consumeCooldowns = Maps.newHashMap();
    }

}
