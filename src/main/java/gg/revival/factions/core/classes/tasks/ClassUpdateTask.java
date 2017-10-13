package gg.revival.factions.core.classes.tasks;

import gg.revival.factions.core.FC;
import gg.revival.factions.core.classes.ClassProfile;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ClassUpdateTask implements Runnable {

    @Getter private FC core;

    public ClassUpdateTask(FC core) {
        this.core = core;
    }

    @Override
    public void run() {
        for(ClassProfile classProfiles : core.getClasses().getActiveClasses()) {
            if(Bukkit.getPlayer(classProfiles.getUuid()) == null) continue;

            Player player = Bukkit.getPlayer(classProfiles.getUuid());

            if(core.getClasses().getClassByArmor(player.getInventory().getHelmet(),
                    player.getInventory().getChestplate(),
                    player.getInventory().getLeggings(),
                    player.getInventory().getBoots()) == null) {

                core.getClasses().removeFromClass(player.getUniqueId());
                core.getClasses().removeClassProfile(player.getUniqueId());
            }
        }
    }
}
