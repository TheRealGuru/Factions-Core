package gg.revival.factions.core.classes.listener;

import gg.revival.factions.claims.ServerClaimType;
import gg.revival.factions.core.FC;
import gg.revival.factions.core.FactionManager;
import gg.revival.factions.core.PlayerManager;
import gg.revival.factions.core.classes.ClassProfile;
import gg.revival.factions.core.classes.ClassType;
import gg.revival.factions.core.classes.cont.RClass;
import gg.revival.factions.core.tools.TimeTools;
import gg.revival.factions.obj.FPlayer;
import gg.revival.factions.obj.Faction;
import gg.revival.factions.obj.PlayerFaction;
import gg.revival.factions.obj.ServerFaction;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.UUID;

public class ClassListener implements Listener {

    @Getter private FC core;

    public ClassListener(FC core) {
        this.core = core;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        new BukkitRunnable() {
            public void run() {
                ClassType foundClassType = core.getClasses().getClassByArmor(
                        player.getInventory().getHelmet(), player.getInventory().getChestplate(), player.getInventory().getLeggings(), player.getInventory().getBoots());

                if(foundClassType == null) return;

                RClass playerClass = core.getClasses().getClassByClassType(foundClassType);

                for(PotionEffect passives : playerClass.getPassives()) {
                    if(player.hasPotionEffect(passives.getType()))
                        player.removePotionEffect(passives.getType());
                }

                core.getClasses().createClassProfile(player.getUniqueId(), foundClassType);

            }
        }.runTaskLater(core, 1L);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        ClassProfile classProfile = core.getClasses().getClassProfile(player.getUniqueId());

        if(classProfile == null || !classProfile.getConsumeCooldowns().isEmpty()) return;

        core.getClasses().removeClassProfile(player.getUniqueId());
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damaged = event.getEntity();
        Entity damager = event.getDamager();

        if(!(damager instanceof Projectile)) return;

        Projectile projectile = (Projectile)damager;
        ProjectileSource source = projectile.getShooter();

        if(!(source instanceof Player)) return;

        Player shooter = (Player)source;

        if(damaged instanceof Player) {
            Player playerDamaged = (Player)damaged;

            if(playerDamaged.getUniqueId().equals(shooter.getUniqueId())) return;
        }

        ClassProfile classProfile = core.getClasses().getClassProfile(shooter.getUniqueId());

        if(classProfile == null || !classProfile.getSelectedClass().equals(ClassType.ARCHER) || !classProfile.isActive()) return;

        Location flatDamagedLoc = damaged.getLocation(), flatDamagerLoc = shooter.getLocation();
        flatDamagedLoc.setY(0); flatDamagerLoc.setY(0);

        double distance = flatDamagedLoc.distance(flatDamagerLoc);
        double multiplier = 1.0;

        for(int i = 0; i < distance; i++)
            multiplier += 0.05;

        if(multiplier> core.getConfiguration().maxArcherDamage)
            multiplier = core.getConfiguration().maxArcherDamage;

        DecimalFormat format = new DecimalFormat("#.00");

        shooter.sendMessage(ChatColor.YELLOW + "[" + ChatColor.BLUE + "Archer" + ChatColor.YELLOW + " with " + ChatColor.BLUE + "Range" + ChatColor.YELLOW + "(" + ChatColor.RED + Math.round(distance) +
                ChatColor.YELLOW + ")]: Damage Increase (" + ChatColor.RED + format.format(multiplier) + "x" + ChatColor.YELLOW + " -> " +
                ChatColor.BLUE + Math.round(event.getDamage() * multiplier) + ChatColor.YELLOW + ")");

        event.setDamage(event.getDamage() * multiplier);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        final UUID uuid = player.getUniqueId();

        if(!event.getAction().equals(Action.RIGHT_CLICK_AIR) && !event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        if(player.getItemInHand() == null) return;

        ItemStack hand = player.getItemInHand();
        ClassProfile classProfile = core.getClasses().getClassProfile(player.getUniqueId());

        if(classProfile == null || classProfile.getSelectedClass() == null || !classProfile.isActive()) return;

        RClass playerClass = core.getClasses().getClassByClassType(classProfile.getSelectedClass());

        // Class doesn't have any active effects to consume
        if(playerClass.getActives().isEmpty()) return;

        boolean isConsumeable = false;

        for(Material consumeables : playerClass.getActives().keySet()) {
            if(hand.getType().equals(consumeables))
                isConsumeable = true;
        }

        // No item to consume, no reason to go further
        if(!isConsumeable) return;

        FPlayer facPlayer = PlayerManager.getPlayer(uuid);

        // Player is in a safezone, don't consume anything
        if(facPlayer.getLocation().getCurrentClaim() != null && facPlayer.getLocation().getCurrentClaim().getClaimOwner() instanceof ServerFaction) {
            ServerFaction serverFaction = (ServerFaction)facPlayer.getLocation().getCurrentClaim().getClaimOwner();

            if(serverFaction.getType().equals(ServerClaimType.SAFEZONE)) {
                player.sendMessage(ChatColor.RED + "You can not consume active abilities in SazeZone claims");
                return;
            }
        }

        Material consumeable = hand.getType();
        int cooldown = 0;

        // Set the cooldown based on the item in hand
        if(consumeable.equals(Material.SUGAR))
            cooldown = core.getConfiguration().activeSpeedCooldown;
        else if(consumeable.equals(Material.FEATHER))
            cooldown = core.getConfiguration().activeJumpCooldown;
        else if(consumeable.equals(Material.GHAST_TEAR))
            cooldown = core.getConfiguration().activeRegenCooldown;
        else if(consumeable.equals(Material.BLAZE_POWDER))
            cooldown = core.getConfiguration().activeStrengthCooldown;

        // Check to make sure the player hasn't recently used this active effect, if they have cancel it and notify them
        if(classProfile.getConsumeCooldowns().containsKey(consumeable)) {
            long dur = classProfile.getConsumeCooldowns().get(consumeable) - System.currentTimeMillis();
            player.sendMessage(ChatColor.RED + "This active ability is locked for " + ChatColor.RED + "" + ChatColor.BOLD + TimeTools.getFormattedCooldown(true, dur) + ChatColor.RED + " seconds");
            return;
        }

        // Subtract the item in hand or completely remove it if they only had 1 left
        if(player.getItemInHand().getAmount() <= 1)
            player.setItemInHand(null);
        else
            player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);

        // Bard gives effects to nearby faction members, so this check needs to be done separate from the others
        if(classProfile.getSelectedClass().equals(ClassType.BARD)) {
            Faction faction = FactionManager.getFactionByPlayer(player.getUniqueId());

            // Only bother checking for nearby faction members IF they have a faction
            if(faction != null && faction instanceof PlayerFaction) {
                PlayerFaction playerFaction = (PlayerFaction)faction;

                // Put the player on cooldown for using this active
                classProfile.getConsumeCooldowns().put(consumeable, System.currentTimeMillis() + (cooldown * 1000L));

                // Scheduler for notifying the player they are off cooldown
                new BukkitRunnable() {
                    public void run() {
                        // Take away the cooldown
                        classProfile.getConsumeCooldowns().remove(consumeable);

                        // If the player is still online, send them the notification
                        if(Bukkit.getPlayer(uuid) != null) {
                            Bukkit.getPlayer(uuid).sendMessage(ChatColor.GREEN + StringUtils.capitalize(playerClass.getActives().get(consumeable).getType().getName().replace("_", " ").toLowerCase()) + " has been unlocked");
                        } else {
                            // The player is not online, if they don't have cooldowns remove their class profile
                            if(classProfile.getConsumeCooldowns().isEmpty())
                                core.getClasses().removeClassProfile(classProfile.getUuid());
                        }
                    }
                }.runTaskLater(core, cooldown * 20L);

                for(UUID nearby : core.getPlayerTools().getNearbyFactionMembers(playerFaction, player.getLocation())) {
                    Player nearbyPlayer = Bukkit.getPlayer(nearby);
                    ClassProfile nearbyClass = core.getClasses().getClassProfile(nearbyPlayer.getUniqueId());

                    // Here we'll store the affected players effect that was overwritten, and we'll give it back when the active effect expires
                    PotionEffect foundPotionEffect = null;

                    if(nearbyClass == null || nearbyClass.getSelectedClass() == null || !nearbyClass.isActive()) {
                        for(PotionEffect potionEffects : nearbyPlayer.getActivePotionEffects()) {
                            if(potionEffects.getType().equals(playerClass.getActives().get(consumeable).getType()))
                                foundPotionEffect = potionEffects;
                        }
                    }

                    final PotionEffect savedPotionEffect = foundPotionEffect;

                    // Remove and re-assign the effect
                    nearbyPlayer.removePotionEffect(playerClass.getActives().get(consumeable).getType());
                    nearbyPlayer.addPotionEffect(playerClass.getActives().get(consumeable));

                    // Scheduler to re-apply effects to affected players after the active expires
                    new BukkitRunnable() {
                        public void run() {
                            if(nearbyPlayer == null) return;

                            if(core.getClasses().getClassProfile(nearbyPlayer.getUniqueId()) != null && core.getClasses().getClassProfile(nearbyPlayer.getUniqueId()).getSelectedClass() != null) {
                                for(PotionEffect passives : core.getClasses().getClassByClassType(core.getClasses().getClassProfile(nearby).getSelectedClass()).getPassives()) {
                                    if(passives.getType().equals(playerClass.getActives().get(consumeable).getType())) {
                                        nearbyPlayer.removePotionEffect(passives.getType());
                                        nearbyPlayer.addPotionEffect(passives);
                                    }
                                }
                            }

                            if(savedPotionEffect != null && !core.getClasses().getClassProfile(nearbyPlayer.getUniqueId()).isActive()) {
                                player.removePotionEffect(savedPotionEffect.getType());
                                player.addPotionEffect(savedPotionEffect);
                            }
                        }
                    }.runTaskLater(core, (playerClass.getActives().get(consumeable).getDuration() + 5L));

                    // Notify all nearby players they have been given the effect
                    nearbyPlayer.sendMessage(ChatColor.YELLOW + "You now have " + ChatColor.BLUE +
                            StringUtils.capitalize(playerClass.getActives().get(consumeable).getType().getName().replace("_", " ").toLowerCase()) +
                            ChatColor.YELLOW + " for " +
                            ChatColor.GREEN + playerClass.getActives().get(consumeable).getDuration() / 20 + " seconds");
                }

                return;
            }
        }

        // This is now back to non-bard classes, here we remove the players effect and replace it with the active one
        player.removePotionEffect(playerClass.getActives().get(consumeable).getType());
        player.addPotionEffect(playerClass.getActives().get(consumeable));

        // Scheduler to re-apply passive effects if they were overwritten when using the active
        new BukkitRunnable() {
            public void run() {
                if(player == null || core.getClasses().getClassProfile(uuid) == null) return;
                if(!core.getClasses().getClassProfile(uuid).getSelectedClass().equals(classProfile.getSelectedClass())) return;

                for(PotionEffect passives : playerClass.getPassives()) {
                    if(passives.getType().equals(playerClass.getActives().get(consumeable).getType()))
                        player.removePotionEffect(passives.getType());
                        player.addPotionEffect(passives);
                }
            }
        }.runTaskLater(core, (playerClass.getActives().get(consumeable).getDuration() + 5L));

        // Notify the player that they have consumed the active
        player.sendMessage(ChatColor.YELLOW + "You now have " + ChatColor.BLUE +
                StringUtils.capitalize(playerClass.getActives().get(consumeable).getType().getName().replace("_", " ").toLowerCase()) +
                ChatColor.YELLOW + " for " +
                ChatColor.GREEN + playerClass.getActives().get(consumeable).getDuration() / 20 + " seconds");

        // Put the player on cooldown
        classProfile.getConsumeCooldowns().put(consumeable, System.currentTimeMillis() + (cooldown * 1000L));

        // Scheduler to remove active cooldown after it has expired
        new BukkitRunnable() {
            public void run() {
                classProfile.getConsumeCooldowns().remove(consumeable);

                // Notify the player if they are online
                if(Bukkit.getPlayer(uuid) != null) {
                    Bukkit.getPlayer(uuid).sendMessage(ChatColor.GREEN + StringUtils.capitalize(playerClass.getActives().get(consumeable).getType().getName().replace("_", " ").toLowerCase()) + " has been unlocked");
                } else {
                    // Otherwise, if they aren't online check to see if they have timers and if not, remove their class profile
                    if(classProfile.getConsumeCooldowns().isEmpty())
                        core.getClasses().removeClassProfile(classProfile.getUuid());
                }
            }
        }.runTaskLater(core, cooldown * 20L);
    }

}
