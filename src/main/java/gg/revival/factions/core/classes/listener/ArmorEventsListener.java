package gg.revival.factions.core.classes.listener;

import gg.revival.factions.core.FC;
import gg.revival.factions.core.classes.ClassProfile;
import gg.revival.factions.core.classes.ClassType;
import gg.revival.factions.core.tools.armorevents.ArmorEquipEvent;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public class ArmorEventsListener implements Listener {

    @Getter private FC core;

    public ArmorEventsListener(FC core) {
        this.core = core;
    }

    @EventHandler
    public void onArmorEquip(ArmorEquipEvent event) {
        Player player = event.getPlayer();
        ClassProfile classProfile = core.getClasses().getClassProfile(player.getUniqueId());

        new BukkitRunnable() {
            public void run() {
                if(!player.isOnline()) return;

                ClassType foundClassType = core.getClasses().getClassByArmor(
                        player.getInventory().getHelmet(), player.getInventory().getChestplate(),
                        player.getInventory().getLeggings(), player.getInventory().getBoots());

                if(classProfile == null && foundClassType != null) {
                    core.getClasses().createClassProfile(player.getUniqueId(), foundClassType);
                    return;
                }

                if(classProfile != null && foundClassType == null) {
                    core.getClasses().removeFromClass(player.getUniqueId());
                }
            }
        }.runTaskLater(core, 1L);
    }

}
