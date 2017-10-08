package gg.revival.factions.core.ui;

import com.google.common.collect.ImmutableList;
import gg.revival.factions.core.FC;
import gg.revival.factions.core.FactionManager;
import gg.revival.factions.core.PlayerManager;
import gg.revival.factions.core.events.obj.DTCEvent;
import gg.revival.factions.core.events.obj.Event;
import gg.revival.factions.core.events.obj.KOTHEvent;
import gg.revival.factions.core.tools.Permissions;
import gg.revival.factions.core.tools.TimeTools;
import gg.revival.factions.obj.FPlayer;
import gg.revival.factions.obj.Faction;
import gg.revival.factions.obj.PlayerFaction;
import gg.revival.factions.timers.TimerType;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UIManager {

    @Getter private FC core;

    public UIManager(FC core) {
        this.core = core;
    }

    /**
     * Contains every players scoreboard information
     */
    @Getter public Map<UUID, RScoreboard> scoreboards = new HashMap<>();

    /**
     * Returns a players scoreboard object
     * @param player The player
     * @return RScoreboard object
     */
    private RScoreboard getScoreboard(Player player) {
        if(scoreboards.containsKey(player.getUniqueId()))
            return scoreboards.get(player.getUniqueId());

        return null;
    }

    /**
     * Updates all UI information for the player
     * @param player The player
     */
    public void update(Player player) {
        RScoreboard scoreboard = getScoreboard(player);
        Scoreboard mcBoard;
        Team fac, ally, admin, mod;

        if(scoreboard == null) {
            scoreboard = new RScoreboard("Test Scoreboard");
            mcBoard = scoreboard.getScoreboard();

            fac = mcBoard.registerNewTeam("f");
            ally = mcBoard.registerNewTeam("a");
            admin = mcBoard.registerNewTeam("adm");
            mod = mcBoard.registerNewTeam("m");

            fac.setCanSeeFriendlyInvisibles(true);
            admin.setCanSeeFriendlyInvisibles(true);
            mod.setCanSeeFriendlyInvisibles(true);

            fac.setPrefix(ChatColor.DARK_GREEN + "");
            ally.setPrefix(ChatColor.LIGHT_PURPLE + "");
            admin.setPrefix("[" + ChatColor.DARK_RED + "Admin" + ChatColor.RESET + "]" + ChatColor.RED);
            mod.setPrefix("[" + ChatColor.DARK_AQUA + "Mod" + ChatColor.RESET + "]" + ChatColor.RESET);
        }

        else {
            mcBoard = scoreboard.getScoreboard();

            fac = mcBoard.getTeam("f");
            ally = mcBoard.getTeam("a");
            admin = mcBoard.getTeam("adm");
            mod = mcBoard.getTeam("m");
        }

        Faction faction = FactionManager.getFactionByPlayer(player.getUniqueId());
        PlayerFaction playerFaction = null;

        if(faction != null) {
            playerFaction = (PlayerFaction)faction;
        }

        for(Player players : Bukkit.getOnlinePlayers()) {
            if(players.hasPermission(Permissions.CORE_ADMIN)) {
                if(!admin.hasEntry(players.getName()))
                    admin.addEntry(players.getName());

                continue;
            }

            if(players.hasPermission(Permissions.CORE_MOD) && !player.hasPermission(Permissions.CORE_ADMIN)) {
                if(!mod.hasEntry(players.getName()))
                    mod.addEntry(players.getName());

                continue;
            }

            if(playerFaction != null && playerFaction.getRoster(true).contains(players.getUniqueId())) {
                if(!fac.hasEntry(players.getName()))
                    fac.addEntry(players.getName());

                continue;
            }

            if(playerFaction != null && FactionManager.isAllyMember(player.getUniqueId(), players.getUniqueId())) {
                if(!ally.hasEntry(players.getName()))
                    ally.addEntry(players.getName());

                continue;
            }

            if(admin.hasEntry(players.getName()))
                admin.removeEntry(players.getName());

            if(mod.hasEntry(players.getName()))
                mod.removeEntry(players.getName());

            if(ally.hasEntry(players.getName()))
                ally.removeEntry(players.getName());

            if(fac.hasEntry(players.getName()))
                fac.removeEntry(players.getName());
        }

        sendActionbar(player);

        scoreboard.send(player);
        scoreboards.put(player.getUniqueId(), scoreboard);
    }

    /**
     * Sends the hotbar message, used to display cooldowns/timers
     * @param player
     */
    private void sendActionbar(Player player) {
        new BukkitRunnable() {
            public void run() {
                FPlayer facPlayer = PlayerManager.getPlayer(player.getUniqueId());

                if(facPlayer == null) return;

                StringBuilder builder = new StringBuilder();

                if(facPlayer.isBeingTimed(TimerType.HOME) && facPlayer.getTimer(TimerType.HOME) != null) {
                    long dur = facPlayer.getTimer(TimerType.HOME).getExpire() - System.currentTimeMillis();
                    builder.append(" " + ChatColor.GOLD + "" + ChatColor.BOLD + "Home Warmup" + ChatColor.WHITE + ": " + ChatColor.YELLOW + TimeTools.getFormattedCooldown(true, dur) + ChatColor.RESET + " ");
                }

                if(facPlayer.isBeingTimed(TimerType.STUCK) && facPlayer.getTimer(TimerType.STUCK) != null) {
                    long dur = facPlayer.getTimer(TimerType.STUCK).getExpire() - System.currentTimeMillis();
                    builder.append(" " + ChatColor.GOLD + "" + ChatColor.BOLD + "Stuck Warmup" + ChatColor.WHITE + ": " + ChatColor.YELLOW + TimeTools.getFormattedCooldown(true, dur) + ChatColor.RESET + " ");
                }

                if(facPlayer.isBeingTimed(TimerType.LOGOUT) && facPlayer.getTimer(TimerType.LOGOUT) != null) {
                    long dur = facPlayer.getTimer(TimerType.LOGOUT).getExpire() - System.currentTimeMillis();
                    builder.append(" " + ChatColor.AQUA + "" + ChatColor.BOLD + "Logout" + ChatColor.WHITE + ": " + ChatColor.YELLOW + TimeTools.getFormattedCooldown(true, dur) + ChatColor.RESET + " ");
                }

                if(facPlayer.isBeingTimed(TimerType.ENDERPEARL) && facPlayer.getTimer(TimerType.ENDERPEARL) != null) {
                    long dur = facPlayer.getTimer(TimerType.ENDERPEARL).getExpire() - System.currentTimeMillis();
                    builder.append(" " + ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Enderpearl" + ChatColor.WHITE + ": " + ChatColor.YELLOW + TimeTools.getFormattedCooldown(true, dur) + ChatColor.RESET + " ");
                }

                if(facPlayer.isBeingTimed(TimerType.TAG) && facPlayer.getTimer(TimerType.TAG) != null) {
                    long dur = facPlayer.getTimer(TimerType.TAG).getExpire() - System.currentTimeMillis();
                    builder.append(" " + ChatColor.RED + "" + ChatColor.BOLD + "Combat-tag" + ChatColor.WHITE + ": " + ChatColor.YELLOW + TimeTools.getFormattedCooldown(true, dur) + ChatColor.RESET + " ");
                }

                if(facPlayer.isBeingTimed(TimerType.SAFETY) && facPlayer.getTimer(TimerType.SAFETY) != null) {
                    long dur = facPlayer.getTimer(TimerType.SAFETY).getExpire() - System.currentTimeMillis();
                    builder.append(" " + ChatColor.GREEN + "" + ChatColor.BOLD + "Safety" + ChatColor.WHITE + ": " + ChatColor.YELLOW + TimeTools.getFormattedCooldown(true, dur) + ChatColor.RESET + " ");
                }

                if(facPlayer.isBeingTimed(TimerType.PVPPROT) && facPlayer.getTimer(TimerType.PVPPROT) != null) {
                    long dur = facPlayer.getTimer(TimerType.PVPPROT).getExpire() - System.currentTimeMillis();
                    builder.append(" " + ChatColor.GREEN + "" + ChatColor.BOLD + "Protection" + ChatColor.WHITE + ": " + ChatColor.YELLOW + TimeTools.formatIntoHHMMSS((int)(dur / 1000L)) + ChatColor.RESET + " ");
                }

                if(facPlayer.isBeingTimed(TimerType.PROGRESSION) && facPlayer.getTimer(TimerType.PROGRESSION) != null) {
                    long dur = facPlayer.getTimer(TimerType.PROGRESSION).getExpire() - System.currentTimeMillis();
                    builder.append(" " + ChatColor.GREEN + "" + ChatColor.BOLD + "Progression" + ChatColor.WHITE + ": " + ChatColor.YELLOW + TimeTools.formatIntoHHMMSS((int)(dur / 1000L)) + ChatColor.RESET + " ");
                }

                if(facPlayer.isBeingTimed(TimerType.CLASS) && facPlayer.getTimer(TimerType.CLASS) != null) {
                    long dur = facPlayer.getTimer(TimerType.CLASS).getExpire() - System.currentTimeMillis();
                    builder.append(" " + ChatColor.BLUE + "" + ChatColor.BOLD + "Class Warmup" + ChatColor.WHITE + ": " + ChatColor.YELLOW + TimeTools.getFormattedCooldown(true, dur) + ChatColor.RESET + " ");
                }

                if(!core.getEvents().getEventManager().getActiveEvents().isEmpty()) {
                    ImmutableList<Event> cache = ImmutableList.copyOf(core.getEvents().getEventManager().getActiveEvents());

                    for(Event activeEvents : cache) {
                        if(activeEvents instanceof KOTHEvent) {
                            KOTHEvent koth = (KOTHEvent)activeEvents;

                            if(koth.isContested()) {
                                builder.append(" " + koth.getDisplayName() + ChatColor.WHITE + ": " + ChatColor.RED + "Contested" + ChatColor.RESET + " ");
                                continue;
                            }

                            if(koth.getNextTicketTime() == -1L) {
                                long dur = koth.getDuration() * 1000L;
                                builder.append(" " + koth.getDisplayName() + ChatColor.WHITE + ": " + ChatColor.YELLOW + TimeTools.getFormattedCooldown(true, dur) + ChatColor.RESET + " ");
                                continue;
                            }

                            long dur = koth.getNextTicketTime() - System.currentTimeMillis();
                            builder.append(" " + koth.getDisplayName() + ChatColor.WHITE + ": " + ChatColor.YELLOW + TimeTools.getFormattedCooldown(true, dur) + ChatColor.RESET + " ");

                            continue;
                        }

                        if(activeEvents instanceof DTCEvent) {
                            DTCEvent dtc = (DTCEvent)activeEvents;

                            if(dtc.getCappingFaction() != null && dtc.getTickets().containsKey(dtc.getCappingFaction())) {
                                builder.append(" " + dtc.getDisplayName() + ChatColor.WHITE + ": " +
                                        ChatColor.YELLOW + dtc.getCappingFaction().getDisplayName() + ChatColor.GOLD + "(" + ChatColor.BLUE + (dtc.getWinCond() - dtc.getTickets().get(dtc.getCappingFaction())) + ChatColor.GOLD + ")" + ChatColor.RESET + " ");
                                continue;
                            }

                            builder.append(" " + dtc.getDisplayName() + ChatColor.WHITE + ": " + ChatColor.YELLOW + dtc.getWinCond() + ChatColor.RESET + " ");
                        }
                    }
                }

                if(builder.toString().length() == 0) return;

                IChatBaseComponent packet = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" +
                        ChatColor.translateAlternateColorCodes('&', builder.toString() + "\"}"));

                PacketPlayOutChat bar = new PacketPlayOutChat(packet, (byte)2);
                ((CraftPlayer)player).getHandle().playerConnection.sendPacket(bar);
            }
        }.runTaskAsynchronously(core);
    }

}
