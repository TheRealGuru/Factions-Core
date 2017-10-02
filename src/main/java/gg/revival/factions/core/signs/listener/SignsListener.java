package gg.revival.factions.core.signs.listener;

import gg.revival.factions.core.FC;
import gg.revival.factions.core.PlayerManager;
import gg.revival.factions.core.signs.Signs;
import gg.revival.factions.core.tools.Permissions;
import gg.revival.factions.obj.FPlayer;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class SignsListener implements Listener {

    @EventHandler
    public void onSignFormatting(SignChangeEvent event) {
        Player player = event.getPlayer();

        if(!player.hasPermission(Permissions.CORE_ADMIN)) return;

        for(int i = 0; i < 3; i++)
            event.setLine(i, ChatColor.translateAlternateColorCodes('&', event.getLine(i)));
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        Player player = event.getPlayer();

        String lineOne = event.getLine(0);
        String lineTwo = event.getLine(1);
        String lineThree = event.getLine(2);
        String lineFour = event.getLine(3);

        if(!player.hasPermission(Permissions.CORE_ADMIN)) return;

        if(lineOne.equalsIgnoreCase("buysign")) {
            if(!Signs.isValidSign(lineTwo, lineThree, lineFour)) {
                player.sendMessage(ChatColor.RED + "Invalid sign");
                return;
            }

            ItemStack item = Signs.getItemStackFromString(lineThree);
            String itemName = item.getType().toString().replace("_", " ");

            if(item.getItemMeta() != null && item.getItemMeta().getDisplayName() != null)
                itemName = ChatColor.stripColor(item.getItemMeta().getDisplayName());

            if(itemName == null) {
                player.sendMessage(ChatColor.RED + "Invalid sign");
                return;
            }

            if(item.getDurability() != 0)
                itemName = itemName + ":" + item.getDurability();

            event.setLine(0, ChatColor.GREEN + "" + ChatColor.BOLD + "- Buy -");
            event.setLine(1, "Amt: " + lineTwo);
            event.setLine(2, StringUtils.capitalize(itemName.toLowerCase()));
            event.setLine(3, "$" + lineFour);

            player.sendMessage(ChatColor.GREEN + "Sign created");
            return;
        }

        if(lineOne.equalsIgnoreCase("sellsign")) {
            if(!Signs.isValidSign(lineTwo, lineThree, lineFour)) {
                player.sendMessage(ChatColor.RED + "Invalid sign");
                return;
            }

            ItemStack item = Signs.getItemStackFromString(lineThree);
            String itemName = item.getType().toString();

            if(item.getItemMeta() != null && item.getItemMeta().getDisplayName() != null)
                itemName = ChatColor.stripColor(item.getItemMeta().getDisplayName());

            if(itemName == null) {
                player.sendMessage(ChatColor.RED + "Invalid sign");
                return;
            }

            if(item.getDurability() != 0)
                itemName = itemName + ":" + item.getDurability();

            event.setLine(0, ChatColor.RED + "" + ChatColor.BOLD + "- Sell -");
            event.setLine(1, "Amt: " + lineTwo);
            event.setLine(2, StringUtils.capitalize(itemName.toLowerCase()));
            event.setLine(3, "$" + lineFour);

            player.sendMessage(ChatColor.GREEN + "Sign created");
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if(!event.getAction().equals(Action.LEFT_CLICK_BLOCK)) return;

        Block block = event.getClickedBlock();

        if(!block.getType().equals(Material.SIGN) && !block.getType().equals(Material.WALL_SIGN) && !block.getType().equals(Material.SIGN_POST)) return;

        if(Signs.getInteractLock().contains(player.getUniqueId())) return;

        Sign sign = (Sign)block.getState();

        String lineOne = sign.getLine(0);
        String lineTwo = sign.getLine(1);
        String lineThree = sign.getLine(2);
        String lineFour = sign.getLine(3);

        if(Signs.isBuySign(lineOne, lineTwo, lineThree, lineFour)) {
            int amount = Integer.valueOf(lineTwo.replace("Amt: ", ""));
            ItemStack item = Signs.getItemStackFromString(lineThree);
            int price = Integer.valueOf(lineFour.replace("$", ""));

            FPlayer facPlayer = PlayerManager.getPlayer(player.getUniqueId());

            if(facPlayer.getBalance() < price) {
                player.sendMessage(ChatColor.RED + "You can not afford this item");
                return;
            }

            facPlayer.setBalance(facPlayer.getBalance() - price);

            item.setAmount(amount);
            player.getInventory().addItem(item);

            player.sendMessage(ChatColor.GREEN + "Purchased " + amount + " " + lineThree + " for $" + price);
            player.sendMessage(ChatColor.GREEN + "Your new balance: " + ChatColor.WHITE + "$" + facPlayer.getBalance());

            Signs.getInteractLock().add(player.getUniqueId());

            new BukkitRunnable()
            {
                public void run()
                {
                    Signs.getInteractLock().remove(player.getUniqueId());
                }
            }.runTaskLater(FC.getFactionsCore(), 5L);
        }

        if(Signs.isSellSign(lineOne, lineTwo, lineThree, lineFour)) {
            int amount = Integer.valueOf(lineTwo.replace("Amt: ", ""));
            ItemStack item = Signs.getItemStackFromString(lineThree);
            int price = Integer.valueOf(lineFour.replace("$", ""));

            if(!player.getInventory().getItemInHand().getType().equals(item.getType())) {
                player.sendMessage(ChatColor.RED + "You must be holding the item you are trying to sell");
                return;
            }

            if(player.getInventory().getItemInHand().getDurability() != item.getDurability()) {
                player.sendMessage(ChatColor.RED + "You must be holding the item you are trying to sell");
                return;
            }

            FPlayer facPlayer = PlayerManager.getPlayer(player.getUniqueId());
            int amtInHand = player.getInventory().getItemInHand().getAmount();

            facPlayer.setBalance(facPlayer.getBalance() + price);

            if(amtInHand < amount) {
                player.sendMessage(ChatColor.RED + "You need " + amount + " of this item in order to sell it");
                return;
            }

            if((amtInHand - amount) == 0) {
                player.getInventory().setItemInHand(null);
            }

            else {
                ItemStack newHand = player.getInventory().getItemInHand();
                newHand.setAmount(amtInHand - amount);
                player.getInventory().setItemInHand(newHand);
            }

            player.sendMessage(ChatColor.GREEN + "Sold " + amount + " " + lineThree + " for $" + price);
            player.sendMessage(ChatColor.GREEN + "Your new balance: " + ChatColor.WHITE + "$" + facPlayer.getBalance());

            Signs.getInteractLock().add(player.getUniqueId());

            new BukkitRunnable()
            {
                public void run()
                {
                    Signs.getInteractLock().remove(player.getUniqueId());
                }
            }.runTaskLater(FC.getFactionsCore(), 5L);
        }
    }
}
