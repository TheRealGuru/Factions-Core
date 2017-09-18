package gg.revival.factions.core.mechanics.hardmode;

import org.bukkit.block.Dispenser;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

public class HardmodeListener implements Listener {

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();

        if(damager instanceof Player) return;

        if(damager instanceof Projectile) {
            ProjectileSource src = ((Projectile) damager).getShooter();

            if(src instanceof Player || src instanceof Dispenser) return;
        }

        event.setDamage(event.getDamage() * 1.5);
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        Entity entity = event.getEntity();
        LivingEntity livingEntity = (LivingEntity)entity;

        if(!(entity instanceof Monster) || entity instanceof Enderman) return;

        livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, false, false));
    }
}
