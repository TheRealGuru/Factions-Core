package gg.revival.factions.core.deathbans;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.projectiles.ProjectileSource;

public class DeathMessages {

    /**
     * Standard prefix used for all death notifications
     */
    @Getter  public static final String prefix = ChatColor.RED + "RIP: ";

    /**
     * Returns a String of the formatted Death message based on PvP
     * @param killed
     * @param killer
     * @return
     */
    public static String getDeathMessage(Player killed, Entity killer) {
        if(killer instanceof Player) {
            Player playerKiller = (Player)killer;
            String hand = "Fist";

            if(playerKiller.getItemInHand() != null)
                hand = StringUtils.capitalize(playerKiller.getItemInHand().getType().toString().replace("_", " ").toLowerCase());

            return prefix + ChatColor.GOLD + killed.getName() + ChatColor.RED + " slain by " + ChatColor.GOLD + playerKiller.getName() + ChatColor.RED + " using " + ChatColor.YELLOW + hand;
        }

        if(killer instanceof Projectile) {
            Projectile projectile = (Projectile)killer;
            ProjectileSource shooter = projectile.getShooter();

            if(shooter instanceof Player) {
                Player playerKiller = (Player)shooter;
                double distance = Math.round(killer.getLocation().distance(killed.getLocation()));
                return prefix + ChatColor.GOLD + killed.getName() + ChatColor.RED + " shot by " + ChatColor.GOLD + playerKiller.getName() + ChatColor.RED + " from a distance of " + ChatColor.BLUE + distance + ChatColor.RED + " blocks";
            } else {
                return prefix + ChatColor.GOLD + killed.getName() + ChatColor.RED + " shot by a " + ChatColor.GOLD + StringUtils.capitalize(killer.getType().toString().replace("_", " ").toLowerCase());
            }
        }

        return prefix + ChatColor.GOLD + killed.getName() + ChatColor.RED + " slain by a " + ChatColor.GOLD + StringUtils.capitalize(killer.getType().toString().replace("_", " ").toLowerCase());
    }

    /**
     * Returns a String of the formatted Death message based on PvE
     * @param killed
     * @return
     */
    public static String getDeathMessage(Player killed) {
        EntityDamageEvent.DamageCause cause = killed.getLastDamageCause().getCause();

        if(cause.equals(EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) || cause.equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION))
            return prefix + ChatColor.GOLD + killed.getName() + ChatColor.RED + " blew up";

        if(cause.equals(EntityDamageEvent.DamageCause.CONTACT))
            return prefix + ChatColor.GOLD + killed.getName() + ChatColor.RED + " walked in to a cactus";

        if(cause.equals(EntityDamageEvent.DamageCause.DROWNING))
            return prefix + ChatColor.GOLD + killed.getName() + ChatColor.RED + " drowned";

        if(cause.equals(EntityDamageEvent.DamageCause.FALL))
            return prefix + ChatColor.GOLD + killed.getName() + ChatColor.RED + " fell " + ChatColor.BLUE + Math.round(killed.getFallDistance()) + ChatColor.RED + " blocks to their death";

        if(cause.equals(EntityDamageEvent.DamageCause.FIRE) || cause.equals(EntityDamageEvent.DamageCause.FIRE_TICK) || cause.equals(EntityDamageEvent.DamageCause.MELTING) || cause.equals(EntityDamageEvent.DamageCause.LAVA))
            return prefix + ChatColor.GOLD + killed.getName() + ChatColor.RED + " burned to death";

        if(cause.equals(EntityDamageEvent.DamageCause.STARVATION))
            return prefix + ChatColor.GOLD + killed.getName() + ChatColor.RED + " failed to outperform a caveman";

        if(cause.equals(EntityDamageEvent.DamageCause.SUFFOCATION))
            return prefix + ChatColor.GOLD + killed.getName() + ChatColor.RED + " suffocated in a block";

        if(cause.equals(EntityDamageEvent.DamageCause.VOID))
            return prefix + ChatColor.GOLD + killed.getName() + ChatColor.RED + " slipped and fell in to the void";

        if(cause.equals(EntityDamageEvent.DamageCause.WITHER))
            return prefix + ChatColor.GOLD + killed.getName() + ChatColor.RED + " withered away";

        return null;
    }

}
