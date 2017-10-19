package gg.revival.factions.core.events.messages;

import com.google.common.base.Joiner;
import gg.revival.factions.core.events.obj.DTCEvent;
import gg.revival.factions.core.events.obj.Event;
import gg.revival.factions.core.events.obj.KOTHEvent;
import gg.revival.factions.core.tools.TimeTools;
import gg.revival.factions.obj.PlayerFaction;
import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventsMessages {

    public String asKOTH(String message) {
        return ChatColor.GOLD + "[King of the Hill] " + ChatColor.RESET + message;
    }

    public String asDTC(String message) {
        return ChatColor.GOLD + "[Destroy the Core] " + ChatColor.RESET + message;
    }

    public String asPalace(String message) {
        return ChatColor.GOLD + "[Palace] " + ChatColor.RESET + message;
    }

    public String asEOTW(String message) {
        return ChatColor.GOLD + "[End of the World] " + ChatColor.RESET + message;
    }

    public String asGeneral(String message) {
        return ChatColor.GOLD + "[Events] " + ChatColor.RESET + message;
    }

    public String nowControlling(Event event) {
        return ChatColor.GOLD + "You" + ChatColor.YELLOW + " are now controlling " + event.getDisplayName();
    }

    public String controlLost(PlayerFaction controller, Event event) {
        return ChatColor.GOLD + controller.getDisplayName() + ChatColor.YELLOW + " lost control of " + event.getDisplayName();
    }

    public static String beingControlled(PlayerFaction controller, Event event) {
        return event.getDisplayName() + ChatColor.YELLOW + " is being controlled by " + ChatColor.GOLD + controller.getDisplayName();
    }

    public String beingContested(Event event) {
        return event.getDisplayName() + ChatColor.YELLOW + " is being " + ChatColor.RED + "contested";
    }

    public String receivedLoot(String claimer, List<ItemStack> loot) {
        StringBuilder response = new StringBuilder();

        response.append(ChatColor.DARK_GREEN + claimer + ChatColor.YELLOW + " has claimed an " + ChatColor.GOLD + "Event Key" + ChatColor.YELLOW + " and received:" + "\n");

        for(ItemStack contents : loot) {
            response.append(ChatColor.GREEN + " - " + contents.getAmount() + "x " + WordUtils.capitalize(contents.getType().name().replace("_", " ").toLowerCase()));

            if(!contents.getEnchantments().isEmpty()) {
                List<String> enchantments = new ArrayList<>();

                for(Enchantment enchant : contents.getEnchantments().keySet())
                    enchantments.add(WordUtils.capitalize(enchant.getName().toLowerCase().replace("_", " ") + " " + contents.getEnchantmentLevel(enchant)));

                response.append(" " + ChatColor.GREEN + "w/ " + ChatColor.BLUE + Joiner.on(ChatColor.AQUA + ", " + ChatColor.BLUE).join(enchantments) + ChatColor.RESET + "\n");
            } else {
                response.append("\n");
            }
        }

        return response.toString();
    }

    public String ticked(Event event) {
        if(event instanceof KOTHEvent) {
            KOTHEvent koth = (KOTHEvent)event;

            return ChatColor.GOLD + koth.getCappingFaction().getDisplayName() + ChatColor.YELLOW + " has gained a ticket for controlling " + event.getDisplayName() + ChatColor.GOLD +
                    " [" + ChatColor.YELLOW + koth.getTickets().get(koth.getCappingFaction()) + ChatColor.GOLD + "/" + ChatColor.YELLOW + koth.getWinCond() + ChatColor.GOLD + "]";
        }

        if(event instanceof DTCEvent) {
            DTCEvent dtc = (DTCEvent)event;

            return ChatColor.GOLD + dtc.getCappingFaction().getDisplayName() + ChatColor.YELLOW + " is destroying the core at " + event.getDisplayName() + ChatColor.GOLD +
                    " [" + ChatColor.YELLOW + dtc.getTickets().get(dtc.getCappingFaction()) + ChatColor.GOLD + "/" + ChatColor.YELLOW + dtc.getWinCond() + ChatColor.GOLD + "]";
        }

        return null;
    }

    public String started(Event event) {
        return event.getDisplayName() + ChatColor.YELLOW + " has started";
    }

    public String stopped(Event event) {
        return event.getDisplayName() + ChatColor.YELLOW + " has stopped";
    }

    public String captured(Event event) {
        if(event instanceof KOTHEvent) {
            KOTHEvent koth = (KOTHEvent)event;

            return event.getDisplayName() + ChatColor.YELLOW + " has been captured by " + ChatColor.GOLD + koth.getCappingFaction().getDisplayName();
        }

        if(event instanceof DTCEvent) {
            DTCEvent dtc = (DTCEvent)event;

            return event.getDisplayName() + ChatColor.YELLOW + " has been destroyed by " + ChatColor.GOLD + dtc.getCappingFaction().getDisplayName();
        }

        return null;
    }

    public String eventInfo(Event event) {
        StringBuilder result = new StringBuilder();

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("E '@' hh:mm a");

        result.append(ChatColor.RED + "" + ChatColor.STRIKETHROUGH + "--------------------" + "\n");
        result.append(ChatColor.WHITE + "Showing information for: " + ChatColor.RESET + event.getDisplayName() + "\n");
        result.append("     " + "\n");

        if(event.isActive())
            result.append(ChatColor.YELLOW + "This event is currently " + ChatColor.GREEN + "Active" + "\n");
        else
            result.append(ChatColor.YELLOW + "THis event is currently " + ChatColor.RED + "Inactive" + "\n");

        result.append("     " + "\n");

        if(event.isPalace())
            result.append(ChatColor.AQUA + "This is a Palace event, capturing this event would give your faction full access to the Palace for the following week" + "\n" + "     " + "\n");

        result.append(ChatColor.YELLOW + "Located at: " + ChatColor.GOLD +
                "World: " + ChatColor.AQUA + StringUtils.capitalize(event.getLootChest().getWorld().getEnvironment().toString().replace("_", " ")) + ChatColor.GOLD +
                " X: " + ChatColor.AQUA + event.getLootChest().getBlockX() + ChatColor.GOLD +
                " Y: " + ChatColor.AQUA + event.getLootChest().getBlockY() + ChatColor.GOLD +
                " Z: " + ChatColor.AQUA + event.getLootChest().getBlockZ() + "\n");

        result.append("     " + "\n");

        result.append(ChatColor.GOLD + "Schedule: " + "\n");

        for(int days : event.getSchedule().keySet()) {
            int hr = event.getSchedule().get(days).getKey();
            int min = event.getSchedule().get(days).getValue();

            result.append(ChatColor.YELLOW + TimeTools.convertSchedule(days, hr, min) + "\n");
        }

        result.append("     " + "\n" + ChatColor.GOLD + "It is currently " + ChatColor.YELLOW + formatter.format(date) + "\n");
        result.append(ChatColor.RED + "" + ChatColor.STRIKETHROUGH + "--------------------" + "\n");

        return result.toString();
    }

}
