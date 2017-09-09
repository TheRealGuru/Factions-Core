package gg.revival.factions.core.events.task;

import gg.revival.factions.core.FactionManager;
import gg.revival.factions.core.events.engine.EventManager;
import gg.revival.factions.core.events.engine.KOTHManager;
import gg.revival.factions.core.events.obj.KOTHEvent;
import gg.revival.factions.obj.Faction;
import gg.revival.factions.obj.PlayerFaction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class KOTHTask extends BukkitRunnable {

    @Override
    public void run() {
        if(KOTHManager.getActiveKOTHEvents().isEmpty()) return;

        for(KOTHEvent koth : KOTHManager.getActiveKOTHEvents()) {
            if(koth.getCappingFaction() == null && koth.getNextTicketTime() != -1L) {
                if(koth.isContested()) koth.setContested(false);

                if(koth.getCapDuration() < koth.getDuration()) {
                    if(koth.getCappingFaction() != null) {
                        PlayerFaction oldCapper = koth.getCappingFaction();
                        // TODO: Send no longer capping message to oldCapper
                        oldCapper.sendMessage("You have lost control of " + koth.getEventName());

                        koth.setCappingFaction(null);
                    }
                }

                koth.setNextTicketTime(-1L);
                koth.setCappingFaction(null);
                continue;
            }

            Set<UUID> capzonePlayers = new HashSet<>();

            for(Player players : Bukkit.getOnlinePlayers()) {
                if(!koth.getCapZone().inside(players.getLocation(), true)) continue;
                if(players.isDead()) continue;

                capzonePlayers.add(players.getUniqueId());
            }

            if(!KOTHManager.shouldBeContested(capzonePlayers)) {
                if(koth.isContested())
                    koth.setContested(false);

                if(koth.getNextTicketTime() > System.currentTimeMillis()) {
                    if(koth.getCappingFaction() == null) {
                        Set<PlayerFaction> factions = new HashSet<>();

                        for(UUID players : capzonePlayers) {
                            if(Bukkit.getPlayer(players) == null) continue;

                            Faction faction = FactionManager.getFactionByPlayer(players);

                            if(faction == null || !(faction instanceof PlayerFaction)) continue;

                            PlayerFaction playerFaction = (PlayerFaction)faction;

                            factions.add(playerFaction);
                        }

                        if(factions.size() != 1) {
                            koth.setCappingFaction(null);
                            koth.setNextTicketTime(-1L);
                            continue;
                        } else {
                            PlayerFaction capper = factions.iterator().next();

                            koth.setCappingFaction(capper);
                            KOTHManager.updateCapTimer(koth);

                            // TODO: Send capper faction now controlling message
                            capper.sendMessage("You are now controlling " + koth.getEventName());
                        }
                    }

                    Set<PlayerFaction> factions = new HashSet<>();

                    for(UUID players : capzonePlayers) {
                        if(Bukkit.getPlayer(players) == null) continue;

                        Faction faction = FactionManager.getFactionByPlayer(players);

                        if(faction == null || !(faction instanceof PlayerFaction)) continue;

                        PlayerFaction playerFaction = (PlayerFaction)faction;
                        factions.add(playerFaction);
                    }

                    if(factions.size() == 0) {
                        koth.setNextTicketTime(-1L);
                        koth.setCappingFaction(null);
                        continue;
                    }

                    if(factions.size() > 1) {
                        koth.setContested(true);
                        continue;
                    }

                    if(koth.getCappingFaction() != null) {
                        for(PlayerFaction foundFactions : factions) {
                            if(!foundFactions.getFactionID().equals(koth.getCappingFaction().getFactionID())) {
                                // TODO: Send no longer contesting message to capping faction
                                koth.getCappingFaction().sendMessage("You are no longer controlling " + koth.getEventName());
                                KOTHManager.updateCapTimer(koth);
                                koth.setCappingFaction(foundFactions);
                                // TODO: Send now controlling message to foundFactions
                                koth.getCappingFaction().sendMessage("You are now controlling " + koth.getEventName());
                            }
                        }
                    }

                    if(koth.getCapDuration() % 30 == 0 && koth.getCapDuration() > 0) {
                        // TODO: Broadcast koth controlled message
                        Bukkit.broadcastMessage(koth.getEventName() + " is being controlled");
                    }

                    if(koth.getCapDuration() <= 0) {
                        EventManager.tickEvent(koth);
                    }
                }
            }

            // Should be contested!
            else {
                koth.setContested(true);
            }
        }
    }

}
