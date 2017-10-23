package gg.revival.factions.core.events.listener;

import gg.revival.factions.claims.Claim;
import gg.revival.factions.claims.ClaimManager;
import gg.revival.factions.claims.ServerClaimType;
import gg.revival.factions.core.FC;
import gg.revival.factions.core.PlayerManager;
import gg.revival.factions.core.events.obj.Event;
import gg.revival.factions.obj.FPlayer;
import gg.revival.factions.obj.ServerFaction;
import gg.revival.factions.timers.TimerType;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class EventsListener implements Listener {

    @Getter private FC core;

    public EventsListener(FC core) {
        this.core = core;
    }

    /**
     * Used to prevent pearling in Palace claims
     * @param event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onProjectileHit(PlayerTeleportEvent event) {
        if(!event.getCause().equals(PlayerTeleportEvent.TeleportCause.ENDER_PEARL)) return;

        if(event.isCancelled()) return;

        Player player = event.getPlayer();
        FPlayer facPlayer = PlayerManager.getPlayer(player.getUniqueId());
        Location location = event.getTo();
        Claim insideClaim = ClaimManager.getClaimAt(location, false);

        if(insideClaim == null) return;

        if(!(insideClaim.getClaimOwner() instanceof ServerFaction)) return;

        ServerFaction serverFaction = (ServerFaction)insideClaim.getClaimOwner();

        if(!serverFaction.getType().equals(ServerClaimType.EVENT)) return;

        Event insideEvent = core.getEvents().getEventManager().getEventByClaim(insideClaim);

        if(insideEvent == null || !insideEvent.isPalace()) return;

        ItemStack enderpearl = new ItemStack(Material.ENDER_PEARL);
        player.getInventory().addItem(enderpearl);

        facPlayer.removeTimer(TimerType.ENDERPEARL);

        player.sendMessage(ChatColor.RED + "You can not use enderpearls in Palace claims");

        event.setCancelled(true);
    }

    /**
     * Used to prevent logging out and logging back in on active event claims
     * @param event
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        final UUID uuid = player.getUniqueId();

        new BukkitRunnable() {
            public void run() {
                FPlayer facPlayer = PlayerManager.getPlayer(uuid);
                boolean needsTeleport = false;

                if(Bukkit.getPlayer(uuid) == null || facPlayer == null || facPlayer.getLocation() == null || facPlayer.getLocation().getCurrentClaim() == null) return;

                Claim currentClaim = facPlayer.getLocation().getCurrentClaim();

                if(!(currentClaim.getClaimOwner() instanceof ServerFaction)) return;

                ServerFaction serverFaction = (ServerFaction)currentClaim.getClaimOwner();

                if(serverFaction == null || !serverFaction.getType().equals(ServerClaimType.EVENT)) return;

                if(core.getEvents().getEventManager().getActiveEvents().isEmpty()) return;

                for(Event activeEvents : core.getEvents().getEventManager().getActiveEvents()) {
                    if(activeEvents.getHookedFactionId() == null || !activeEvents.getHookedFactionId().equals(serverFaction.getFactionID())) continue;
                    needsTeleport = true;
                }

                if(!needsTeleport) return;

                int x = player.getLocation().getBlockX();
                int z = player.getLocation().getBlockZ();

                while (ClaimManager.getClaimAt(new Location(player.getWorld(), x, player.getLocation().getBlockY(), z), true) != null) {
                    x += 5;
                    z += 5;
                }

                Location safeBlock = new Location(player.getWorld(), x, player.getWorld().getHighestBlockYAt(x, z), z);

                player.teleport(safeBlock.add(0, 2, 0));
                player.sendMessage(ChatColor.RED + "" + ChatColor.UNDERLINE + "You have been teleported outside the event claims");
            }
        }.runTaskLater(core, 20L);
    }

}
