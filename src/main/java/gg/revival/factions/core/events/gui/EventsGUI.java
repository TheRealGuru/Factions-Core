package gg.revival.factions.core.events.gui;

import gg.revival.factions.core.events.engine.EventManager;
import gg.revival.factions.core.events.obj.DTCEvent;
import gg.revival.factions.core.events.obj.Event;
import gg.revival.factions.core.events.obj.KOTHEvent;
import gg.revival.factions.core.tools.TimeTools;
import gg.revival.factions.obj.PlayerFaction;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class EventsGUI {

    public static boolean isGUI(Inventory inventory) {
        return inventory != null && inventory.getName() != null && inventory.getName().equals(ChatColor.BLACK + "Events");
    }

    public static void update(Inventory inventory) {
        inventory.clear();

        for(Event events : EventManager.getEvents()) {
            ItemStack activeEvent = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)13);
            ItemStack inactiveEvent = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)14);

            ItemMeta activeMeta = activeEvent.getItemMeta();
            ItemMeta inactiveMeta = inactiveEvent.getItemMeta();

            activeMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            inactiveMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

            StringBuilder name = new StringBuilder();
            List<String> lore = new ArrayList<>();

            name.append(events.getDisplayName());

            if(events.isActive()) {
                lore.add(ChatColor.GRAY + "This event is " + ChatColor.GREEN + "Active");
                lore.add("     ");
                lore.add(ChatColor.AQUA + "Location" + ChatColor.WHITE + ": " +
                        StringUtils.capitalize(events.getLootChest().getWorld().getEnvironment().toString().toLowerCase()) +
                        ", X: " + events.getLootChest().getBlockX() + " Y: " + events.getLootChest().getBlockY() + " Z: " + events.getLootChest().getBlockZ());
                lore.add("      ");

                if(events instanceof KOTHEvent) {
                    KOTHEvent koth = (KOTHEvent)events;

                    if(koth.getNextTicketTime() == -1L) {
                        lore.add(events.getDisplayName() + ChatColor.YELLOW + ": " + TimeTools.getFormattedCooldown(false, koth.getDuration() * 1000L));
                    } else {
                        lore.add(events.getDisplayName() + ChatColor.YELLOW + ": " + TimeTools.getFormattedCooldown(false, koth.getNextTicketTime() - System.currentTimeMillis()));
                    }

                    if(koth.getCappingFaction() != null) {
                        lore.add(ChatColor.YELLOW + "Contested by: " + ChatColor.BLUE + koth.getCappingFaction().getDisplayName());
                    }

                    lore.add("         ");
                    lore.add(ChatColor.YELLOW + "Tickets needed to win: " + ChatColor.LIGHT_PURPLE + koth.getWinCond());

                    lore.add("       ");
                    lore.add(ChatColor.YELLOW + "Current Standings:");

                    if(!koth.getTickets().isEmpty()) {
                        int cursor = 1;

                        for(PlayerFaction ticketFactions : koth.getTicketsInOrder().keySet()) {
                            int tickets = koth.getTicketsInOrder().get(ticketFactions);

                            lore.add(ChatColor.GOLD + "" + cursor + ". " + ChatColor.BLUE + ticketFactions.getDisplayName() + " " + ChatColor.GOLD + "(" + ChatColor.YELLOW + tickets + ChatColor.GOLD + ")");
                        }
                    }

                    else {
                        lore.add(ChatColor.GRAY + "This event is not being contested");
                    }
                }

                if(events instanceof DTCEvent) {
                    DTCEvent dtc = (DTCEvent)events;

                    if(dtc.getCappingFaction() != null) {
                        int tickets = dtc.getTickets().get(dtc.getCappingFaction());
                        lore.add(events.getDisplayName() + ChatColor.YELLOW + ": " + dtc.getCappingFaction().getDisplayName() + " " + ChatColor.GOLD + "(" + ChatColor.YELLOW + tickets + ChatColor.GOLD + ")");
                        lore.add(ChatColor.RED + "Will reset if core is not broken in" + ChatColor.YELLOW + ": " + TimeTools.getFormattedCooldown(false, dtc.getResetTime() - System.currentTimeMillis()));
                        lore.add("       ");
                    }

                    lore.add(ChatColor.YELLOW + "Tickets needed to win: " + ChatColor.LIGHT_PURPLE + dtc.getWinCond());
                }

                activeMeta.setDisplayName(name.toString());
                activeMeta.setLore(lore);
                activeEvent.setItemMeta(activeMeta);

                inventory.addItem(activeEvent);
            }

            else {
                lore.add(ChatColor.GRAY + "This event is " + ChatColor.RED + "Inactive");
                lore.add("     ");
                lore.add(ChatColor.AQUA + "Location" + ChatColor.WHITE + ": " +
                        StringUtils.capitalize(events.getLootChest().getWorld().getEnvironment().toString().toLowerCase()) +
                        ", X: " + events.getLootChest().getBlockX() + " Y: " + events.getLootChest().getBlockY() + " Z: " + events.getLootChest().getBlockZ());

                inactiveMeta.setDisplayName(name.toString());
                inactiveMeta.setLore(lore);
                inactiveEvent.setItemMeta(inactiveMeta);

                inventory.addItem(inactiveEvent);
            }
        }
    }

    public static void open(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 9, ChatColor.BLACK + "Events");

        for(Event events : EventManager.getEvents()) {
            ItemStack activeEvent = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)13);
            ItemStack inactiveEvent = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)14);

            ItemMeta activeMeta = activeEvent.getItemMeta();
            ItemMeta inactiveMeta = inactiveEvent.getItemMeta();

            activeMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            inactiveMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

            StringBuilder name = new StringBuilder();
            List<String> lore = new ArrayList<>();

            name.append(events.getDisplayName());

            if(events.isActive()) {
                lore.add(ChatColor.GRAY + "This event is " + ChatColor.GREEN + "Active");
                lore.add("     ");
                lore.add(ChatColor.AQUA + "Location" + ChatColor.WHITE + ": " +
                        StringUtils.capitalize(events.getLootChest().getWorld().getEnvironment().toString().toLowerCase()) +
                        ", X: " + events.getLootChest().getBlockX() + " Y: " + events.getLootChest().getBlockY() + " Z: " + events.getLootChest().getBlockZ());
                lore.add("      ");

                if(events instanceof KOTHEvent) {
                    KOTHEvent koth = (KOTHEvent)events;

                    if(koth.getNextTicketTime() == -1L) {
                        lore.add(events.getDisplayName() + ChatColor.YELLOW + ": " + TimeTools.getFormattedCooldown(false, koth.getDuration() * 1000L));
                    } else {
                        lore.add(events.getDisplayName() + ChatColor.YELLOW + ": " + TimeTools.getFormattedCooldown(false, koth.getNextTicketTime() - System.currentTimeMillis()));
                    }

                    if(koth.getCappingFaction() != null) {
                        lore.add(ChatColor.YELLOW + "Contested by: " + ChatColor.BLUE + koth.getCappingFaction().getDisplayName());
                    }

                    lore.add("         ");
                    lore.add(ChatColor.YELLOW + "Tickets needed to win: " + ChatColor.LIGHT_PURPLE + koth.getWinCond());

                    lore.add("       ");
                    lore.add(ChatColor.YELLOW + "Current Standings:");

                    if(!koth.getTickets().isEmpty()) {
                        int cursor = 1;

                        for(PlayerFaction ticketFactions : koth.getTicketsInOrder().keySet()) {
                            int tickets = koth.getTicketsInOrder().get(ticketFactions);

                            lore.add(ChatColor.GOLD + "" + cursor + ". " + ChatColor.BLUE + ticketFactions.getDisplayName() + " " + ChatColor.GOLD + "(" + ChatColor.YELLOW + tickets + ChatColor.GOLD + ")");
                        }
                    }

                    else {
                        lore.add(ChatColor.GRAY + "This event is not being contested");
                    }
                }

                if(events instanceof DTCEvent) {
                    DTCEvent dtc = (DTCEvent)events;

                    if(dtc.getCappingFaction() != null) {
                        int tickets = dtc.getTickets().get(dtc.getCappingFaction());
                        lore.add(events.getDisplayName() + ChatColor.YELLOW + ": " + dtc.getCappingFaction().getDisplayName() + " " + ChatColor.GOLD + "(" + ChatColor.YELLOW + tickets + ChatColor.GOLD + ")");
                        lore.add(ChatColor.RED + "Will reset if core is not broken in" + ChatColor.YELLOW + ": " + TimeTools.getFormattedCooldown(false, dtc.getResetTime() - System.currentTimeMillis()));
                    }

                    lore.add("       ");
                    lore.add(ChatColor.YELLOW + "Tickets needed to win: " + ChatColor.LIGHT_PURPLE + dtc.getWinCond());
                }

                activeMeta.setDisplayName(name.toString());
                activeMeta.setLore(lore);
                activeEvent.setItemMeta(activeMeta);

                inventory.addItem(activeEvent);
            }

            else {
                lore.add(ChatColor.GRAY + "This event is " + ChatColor.RED + "Inactive");
                lore.add("     ");
                lore.add(ChatColor.AQUA + "Location" + ChatColor.WHITE + ": " +
                        StringUtils.capitalize(events.getLootChest().getWorld().getEnvironment().toString().toLowerCase()) +
                        ", X: " + events.getLootChest().getBlockX() + " Y: " + events.getLootChest().getBlockY() + " Z: " + events.getLootChest().getBlockZ());

                inactiveMeta.setDisplayName(name.toString());
                inactiveMeta.setLore(lore);
                inactiveEvent.setItemMeta(inactiveMeta);

                inventory.addItem(inactiveEvent);
            }
        }

        player.openInventory(inventory);
    }

}
