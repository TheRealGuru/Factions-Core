package gg.revival.factions.core.events.task;

import gg.revival.factions.core.FC;
import gg.revival.factions.core.FactionManager;
import gg.revival.factions.core.events.obj.KOTHEvent;
import gg.revival.factions.obj.Faction;
import gg.revival.factions.obj.PlayerFaction;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class KOTHTask extends BukkitRunnable {

    @Getter private FC core;

    public KOTHTask(FC core) {
        this.core = core;
    }

    private Set<KOTHEvent> recentlyBroadcasted = new HashSet<>();

    private void silence(KOTHEvent event) {
        if(recentlyBroadcasted.contains(event)) return;

        recentlyBroadcasted.add(event);

        new BukkitRunnable() {
            public void run() {
                recentlyBroadcasted.remove(event);
            }
        }.runTaskLater(core, 10 * 20L);
    }

    @Override
    public void run() {
        if(core.getEvents().getKothManager() == null || core.getEvents().getKothManager().getActiveKOTHEvents().isEmpty()) return;

        for(KOTHEvent koth : core.getEvents().getKothManager().getActiveKOTHEvents()) {
            Set<UUID> capzonePlayers = new HashSet<>();

            for(Player players : Bukkit.getOnlinePlayers()) {
                if(!koth.getCapZone().inside(players.getLocation(), true)) continue;
                if(players.isDead() || players.getHealth() < 0.0) continue;

                capzonePlayers.add(players.getUniqueId());
            }

            if(capzonePlayers.isEmpty() && koth.getCappingFaction() != null) {
                if(koth.getCapDuration() <= (koth.getDuration() - 10)) {
                    if(!recentlyBroadcasted.contains(koth)) {
                        if(koth.isPalace())
                            Bukkit.broadcastMessage(core.getEvents().getEventMessages().asPalace(core.getEvents().getEventMessages().controlLost(koth.getCappingFaction(), koth)));
                        else
                            Bukkit.broadcastMessage(core.getEvents().getEventMessages().asKOTH(core.getEvents().getEventMessages().controlLost(koth.getCappingFaction(), koth)));

                        silence(koth);
                    }
                }

                koth.setCappingFaction(null);
                koth.setNextTicketTime(-1L);
                continue;
            }

            if(!core.getEvents().getKothManager().shouldBeContested(capzonePlayers)) {
                if(koth.isContested()) {
                    koth.setContested(false);
                    koth.setPauseDuration(0L);
                }

                if(koth.getNextTicketTime() > System.currentTimeMillis() || koth.getNextTicketTime() == -1L) {
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
                            core.getEvents().getKothManager().updateCapTimer(koth);

                            if(koth.isPalace())
                                capper.sendMessage(core.getEvents().getEventMessages().asPalace(core.getEvents().getEventMessages().nowControlling(koth)));
                            else
                                capper.sendMessage(core.getEvents().getEventMessages().asKOTH(core.getEvents().getEventMessages().nowControlling(koth)));
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
                        koth.setPauseDuration(koth.getNextTicketTime() - System.currentTimeMillis());
                        koth.setNextTicketTime(System.currentTimeMillis() + koth.getPauseDuration());

                        continue;
                    }

                    if(koth.getCappingFaction() != null) {
                        for(PlayerFaction foundFactions : factions) {
                            if(!foundFactions.getFactionID().equals(koth.getCappingFaction().getFactionID())) {
                                if(koth.isPalace())
                                    Bukkit.broadcastMessage(core.getEvents().getEventMessages().asPalace(core.getEvents().getEventMessages().controlLost(koth.getCappingFaction(), koth)));
                                else
                                    Bukkit.broadcastMessage(core.getEvents().getEventMessages().asKOTH(core.getEvents().getEventMessages().controlLost(koth.getCappingFaction(), koth)));

                                core.getEvents().getKothManager().updateCapTimer(koth);
                                koth.setCappingFaction(foundFactions);

                                if(koth.isPalace())
                                    Bukkit.broadcastMessage(core.getEvents().getEventMessages().asPalace(core.getEvents().getEventMessages().nowControlling(koth)));
                                else
                                    Bukkit.broadcastMessage(core.getEvents().getEventMessages().asKOTH(core.getEvents().getEventMessages().nowControlling(koth)));
                            }
                        }
                    }

                    if(koth.getCapDuration() % 30 == 0 && koth.getCapDuration() > 0 && koth.getCapDuration() < koth.getDuration()) {
                        if(!recentlyBroadcasted.contains(koth)) {
                            if(koth.isPalace())
                                Bukkit.broadcastMessage(core.getEvents().getEventMessages().asPalace(core.getEvents().getEventMessages().beingControlled(koth.getCappingFaction(), koth)));
                            else
                                Bukkit.broadcastMessage(core.getEvents().getEventMessages().asKOTH(core.getEvents().getEventMessages().beingControlled(koth.getCappingFaction(), koth)));

                            silence(koth);
                        }
                    }
                }

                if(koth.getNextTicketTime() <= System.currentTimeMillis()) {
                    core.getEvents().getKothManager().updateCapTimer(koth);

                    core.getEvents().getEventManager().tickEvent(koth);
                }
            }

            // Should be contested!
            else {
                if(!koth.isContested()) {
                    koth.setContested(true);
                    koth.setPauseDuration(koth.getNextTicketTime() - System.currentTimeMillis());
                }

                koth.setNextTicketTime(System.currentTimeMillis() + koth.getPauseDuration());

                if(!recentlyBroadcasted.contains(koth)) {
                    if(koth.isPalace())
                        Bukkit.broadcastMessage(core.getEvents().getEventMessages().asPalace(core.getEvents().getEventMessages().beingContested(koth)));
                    else
                        Bukkit.broadcastMessage(core.getEvents().getEventMessages().asKOTH(core.getEvents().getEventMessages().beingContested(koth)));

                    silence(koth);
                }
            }
        }
    }

}
