package gg.revival.factions.core.classes.listener;

import gg.revival.factions.core.FC;
import gg.revival.factions.core.classes.ClassProfile;
import gg.revival.factions.core.classes.ClassType;
import gg.revival.factions.core.classes.Classes;
import gg.revival.factions.core.tools.armorevents.ArmorEquipEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public class ArmorEventsListener implements Listener {

    @EventHandler
    public void onArmorEquip(ArmorEquipEvent event) {
        Player player = event.getPlayer();
        ClassProfile classProfile = Classes.getClassProfile(player.getUniqueId());

        new BukkitRunnable() {
            public void run() {
                if(player == null) return;

                ClassType foundClassType = Classes.getClassByArmor(
                        player.getInventory().getHelmet(), player.getInventory().getChestplate(),
                        player.getInventory().getLeggings(), player.getInventory().getBoots());

                if(classProfile == null && foundClassType != null) {
                    Classes.createClassProfile(player.getUniqueId(), foundClassType);
                    return;
                }

                if(classProfile != null && foundClassType == null) {
                    Classes.removeFromClass(player.getUniqueId());
                }
            }
        }.runTaskLater(FC.getFactionsCore(), 1L);
    }

}
