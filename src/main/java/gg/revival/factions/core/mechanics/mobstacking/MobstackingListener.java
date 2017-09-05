package gg.revival.factions.core.mechanics.mobstacking;

import gg.revival.factions.core.FC;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MobstackingListener implements Listener
{

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event)
    {
        Entity entity = event.getEntity();

        if(!(entity instanceof LivingEntity)) return;

        LivingEntity livingEntity = (LivingEntity)entity;

        if(livingEntity instanceof Player) return;

        Mobstacker.subtractFromStack(entity);

        if(Mobstacker.getProtectedEntities().contains(entity.getUniqueId()))
            Mobstacker.getProtectedEntities().remove(entity.getUniqueId());
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent event)
    {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        Entity entity = event.getRightClicked();

        if(!(entity instanceof LivingEntity)) return;
        if(entity instanceof Player) return;
        if(!Mobstacker.isStack(entity)) return;
        if(Mobstacker.getProtectedEntities().contains(entity.getUniqueId())) return;

        List<EntityType> types;

        if(Mobstacker.getSplitCooldowns().containsKey(player.getUniqueId()))
        {
            if(Mobstacker.getSplitCooldowns().get(player.getUniqueId()).contains(entity.getType()))
            {
                player.sendMessage(ChatColor.RED + "Try again in a few minutes");
                event.setCancelled(true);
                return;
            }

            types = Mobstacker.getSplitCooldowns().get(player.getUniqueId());
        }

        else
        {
            types = new ArrayList<>();
        }

        types.add(entity.getType());

        Mobstacker.getSplitCooldowns().put(player.getUniqueId(), types);
        Mobstacker.splitStack(entity);

        new BukkitRunnable()
        {
            public void run()
            {
                if(Mobstacker.getSplitCooldowns().containsKey(uuid))
                {
                    Mobstacker.getSplitCooldowns().get(uuid).remove(entity.getType());
                }
            }
        }.runTaskLater(FC.getFactionsCore(), 300 * 20L);
    }

}
