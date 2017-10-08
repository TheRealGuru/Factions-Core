package gg.revival.factions.core.mechanics.mobstacking;

import gg.revival.factions.core.FC;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MobstackingListener implements Listener {

    @Getter private FC core;

    public MobstackingListener(FC core) {
        this.core = core;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if(!core.getConfiguration().mobstackingEnabled) return;

        LivingEntity entity = event.getEntity();

        if(entity instanceof Player) return;

        if(core.getMechanics().getMobstacker().isStack(entity))
            core.getMechanics().getMobstacker().subtractFromStack(entity);

        if(core.getMechanics().getMobstacker().getProtectedEntities().contains(entity.getUniqueId()))
            core.getMechanics().getMobstacker().getProtectedEntities().remove(entity.getUniqueId());
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        if(!core.getConfiguration().mobstackingEnabled) return;

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        Entity entity = event.getRightClicked();

        if(!(entity instanceof LivingEntity) || entity instanceof Player || entity instanceof Monster) return;
        if(!core.getMechanics().getMobstacker().isStack(entity)) return;
        if(core.getMechanics().getMobstacker().getProtectedEntities().contains(entity.getUniqueId())) return;

        List<EntityType> types;

        if(core.getMechanics().getMobstacker().getSplitCooldowns().containsKey(player.getUniqueId())) {
            if(core.getMechanics().getMobstacker().getSplitCooldowns().get(player.getUniqueId()).contains(entity.getType())) {
                player.sendMessage(ChatColor.RED + "Try again in a few minutes");
                event.setCancelled(true);
                return;
            }

            types = core.getMechanics().getMobstacker().getSplitCooldowns().get(player.getUniqueId());
        }

        else {
            types = new ArrayList<>();
        }

        types.add(entity.getType());

        core.getMechanics().getMobstacker().getSplitCooldowns().put(player.getUniqueId(), types);
        core.getMechanics().getMobstacker().splitStack(entity);

        new BukkitRunnable() {
            public void run() {
                if(core.getMechanics().getMobstacker().getSplitCooldowns().containsKey(uuid))
                    core.getMechanics().getMobstacker().getSplitCooldowns().get(uuid).remove(entity.getType());
            }
        }.runTaskLater(core, 300 * 20L);
    }

}
