package gg.revival.factions.core.events.listener;

import gg.revival.factions.core.FactionManager;
import gg.revival.factions.core.events.builder.DTCBuilder;
import gg.revival.factions.core.events.builder.EventBuilder;
import gg.revival.factions.core.events.builder.KOTHBuilder;
import gg.revival.factions.core.events.engine.EventManager;
import gg.revival.factions.core.events.obj.CapZone;
import gg.revival.factions.core.events.obj.DTCEvent;
import gg.revival.factions.core.events.obj.KOTHEvent;
import gg.revival.factions.core.tools.Permissions;
import gg.revival.factions.obj.Faction;
import gg.revival.factions.obj.ServerFaction;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class EventBuilderListener implements Listener {

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        if(message == null || message.length() == 0) return;
        if(!player.hasPermission(Permissions.CORE_ADMIN)) return;
        if(!EventBuilder.isBuilding(player.getUniqueId())) return;

        if(EventBuilder.getKOTHBuilder(player.getUniqueId()) != null) {
            KOTHBuilder builder = EventBuilder.getKOTHBuilder(player.getUniqueId());

            if(builder.getBuildPhase() == 1) {
                builder.setEventName(message);
                builder.setBuildPhase(2);
                player.sendMessage(builder.getPhaseResponse());

                event.setCancelled(true);
                return;
            }

            if(builder.getBuildPhase() == 2) {
                builder.setDisplayName(message);
                builder.setBuildPhase(3);
                player.sendMessage(builder.getPhaseResponse());

                event.setCancelled(true);
                return;
            }

            if(builder.getBuildPhase() == 3) {
                String namedFaction = message.replace(" ", "");
                Faction faction = FactionManager.getFactionByName(namedFaction);

                if(faction == null || !(faction instanceof ServerFaction)) {
                    player.sendMessage(ChatColor.RED + "Faction not found");

                    event.setCancelled(true);
                    return;
                }

                builder.setHookedFactionName(namedFaction);
                builder.setBuildPhase(4);
                player.sendMessage(builder.getPhaseResponse());

                event.setCancelled(true);
                return;
            }

            if(builder.getBuildPhase() == 6) {
                String response = message.substring(0, 1);

                if(response.equalsIgnoreCase("y")) {
                    builder.setPalace(true);
                    builder.setBuildPhase(7);
                    player.sendMessage(builder.getPhaseResponse());
                    event.setCancelled(true);

                    return;
                }

                if(response.equalsIgnoreCase("n")) {
                    builder.setPalace(false);
                    builder.setBuildPhase(7);
                    player.sendMessage(builder.getPhaseResponse());

                    KOTHEvent kothEvent = builder.convertToKOTH();
                    EventManager.getEvents().add(kothEvent);
                    EventBuilder.saveEvent(kothEvent);

                    event.setCancelled(true);

                    return;
                }

                player.sendMessage(ChatColor.RED + "Invalid response, (y/N)");
                event.setCancelled(true);
            }
        }

        /*
         *  DTC Event Configuration Start
         */
        if(EventBuilder.getDTCBuilder(player.getUniqueId()) != null) {
            DTCBuilder builder = EventBuilder.getDTCBuilder(player.getUniqueId());

            if(builder.getBuildPhase() == 1) {
                builder.setEventName(message);
                builder.setBuildPhase(2);
                player.sendMessage(builder.getPhaseResponse());

                event.setCancelled(true);
                return;
            }

            if(builder.getBuildPhase() == 2) {
                builder.setDisplayName(message);
                builder.setBuildPhase(3);
                player.sendMessage(builder.getPhaseResponse());

                event.setCancelled(true);
                return;
            }

            if(builder.getBuildPhase() == 3) {
                String namedFaction = message.replace(" ", "");
                Faction faction = FactionManager.getFactionByName(namedFaction);

                if(faction == null || !(faction instanceof ServerFaction)) {
                    player.sendMessage(ChatColor.RED + "Faction not found");

                    event.setCancelled(true);
                    return;
                }

                builder.setHookedFactionName(namedFaction);
                builder.setBuildPhase(4);
                player.sendMessage(builder.getPhaseResponse());

                event.setCancelled(true);
                return;
            }

            if(builder.getBuildPhase() == 6) {
                String response = message.substring(0, 1);

                if(response.equalsIgnoreCase("y")) {
                    builder.setPalace(true);
                    builder.setBuildPhase(7);
                    player.sendMessage(builder.getPhaseResponse());
                    event.setCancelled(true);

                    return;
                }

                if(response.equalsIgnoreCase("n")) {
                    builder.setPalace(false);
                    builder.setBuildPhase(7);
                    player.sendMessage(builder.getPhaseResponse());

                    DTCEvent dtcEvent = builder.convertToDTC();
                    EventManager.getEvents().add(dtcEvent);
                    EventBuilder.saveEvent(dtcEvent);

                    event.setCancelled(true);

                    return;
                }

                player.sendMessage(ChatColor.RED + "Invalid response, (y/N)");
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();

        if(!action.equals(Action.RIGHT_CLICK_BLOCK)) return;
        if(!player.hasPermission(Permissions.CORE_ADMIN)) return;
        if(event.getClickedBlock() == null) return;
        if(!EventBuilder.isBuilding(player.getUniqueId())) return;

        if(EventBuilder.getKOTHBuilder(player.getUniqueId()) != null) {
            KOTHBuilder builder = EventBuilder.getKOTHBuilder(player.getUniqueId());
            CapZone capZone = null;

            if(builder.getBuildPhase() == 3) {
                if(!event.getClickedBlock().getType().equals(Material.CHEST)) {
                    player.sendMessage(ChatColor.RED + "This block is not a chest");
                    return;
                }

                builder.setLootChest(event.getClickedBlock().getLocation());
                builder.setBuildPhase(4);
                player.sendMessage(builder.getPhaseResponse());

                event.setCancelled(true);
                return;
            }

            if(builder.getBuildPhase() == 4) {
                if(builder.getCapzone() == null) {
                    capZone = new CapZone(null, null, player.getWorld().getName());
                } else {
                    capZone = builder.getCapzone();
                }

                capZone.setCornerOne(event.getClickedBlock().getLocation());
                builder.setCapzone(capZone);
                builder.setBuildPhase(5);
                player.sendMessage(builder.getPhaseResponse());

                event.setCancelled(true);
                return;
            }

            if(builder.getBuildPhase() == 5) {
                capZone = builder.getCapzone();

                capZone.setCornerTwo(event.getClickedBlock().getLocation());
                capZone.update();

                builder.setBuildPhase(6);
                player.sendMessage(builder.getPhaseResponse());

                event.setCancelled(true);
            }
        }

        if(EventBuilder.getDTCBuilder(player.getUniqueId()) != null) {
            DTCBuilder builder = EventBuilder.getDTCBuilder(player.getUniqueId());

            if(builder.getBuildPhase() == 4) {
                if(!event.getClickedBlock().getType().equals(Material.CHEST)) {
                    player.sendMessage(ChatColor.RED + "This block is not a chest");
                    return;
                }

                builder.setLootChest(event.getClickedBlock().getLocation());
                builder.setBuildPhase(5);
                player.sendMessage(builder.getPhaseResponse());

                event.setCancelled(true);
                return;
            }

            if(builder.getBuildPhase() == 5) {
                builder.setCore(event.getClickedBlock().getLocation());
                builder.setBuildPhase(6);
                player.sendMessage(builder.getPhaseResponse());

                event.setCancelled(true);
            }
        }
    }

}
