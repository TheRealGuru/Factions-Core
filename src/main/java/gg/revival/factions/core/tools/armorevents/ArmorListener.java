package gg.revival.factions.core.tools.armorevents;

import com.google.common.collect.ImmutableSet;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.ItemStack;

public class ArmorListener implements Listener {

    private final ImmutableSet<Material> ignoredItems = ImmutableSet.of(Material.FURNACE, Material.CHEST, Material.TRAPPED_CHEST, Material.BEACON,
            Material.DISPENSER, Material.DROPPER, Material.HOPPER, Material.WORKBENCH, Material.ENCHANTMENT_TABLE, Material.ENDER_CHEST, Material.ANVIL,
            Material.BED_BLOCK, Material.FENCE_GATE, Material.SPRUCE_FENCE_GATE, Material.BIRCH_FENCE_GATE, Material.ACACIA_FENCE_GATE, Material.JUNGLE_FENCE_GATE,
            Material.DARK_OAK_FENCE_GATE, Material.IRON_DOOR_BLOCK, Material.WOODEN_DOOR, Material.SPRUCE_DOOR, Material.BIRCH_DOOR, Material.JUNGLE_DOOR,
            Material.ACACIA_DOOR, Material.DARK_OAK_DOOR, Material.WOOD_BUTTON, Material.STONE_BUTTON, Material.TRAP_DOOR, Material.IRON_TRAPDOOR, Material.DIODE_BLOCK_OFF,
            Material.DIODE_BLOCK_ON, Material.REDSTONE_COMPARATOR_ON, Material.REDSTONE_COMPARATOR_OFF, Material.FENCE, Material.SPRUCE_FENCE, Material.BIRCH_FENCE, Material.JUNGLE_FENCE,
            Material.DARK_OAK_FENCE, Material.ACACIA_FENCE, Material.NETHER_FENCE, Material.CAULDRON, Material.SIGN_POST, Material.WALL_SIGN, Material.SIGN, Material.LEVER);

    @EventHandler
    public final void onInventoryClick(final InventoryClickEvent e){
        boolean shift = false, numberkey = false;

        if(e.isCancelled()) return;

        if(e.getClick().equals(ClickType.SHIFT_LEFT) || e.getClick().equals(ClickType.SHIFT_RIGHT))
            shift = true;

        if(e.getClick().equals(ClickType.NUMBER_KEY))
            numberkey = true;

        if(e.getSlotType() != InventoryType.SlotType.ARMOR && e.getSlotType() != InventoryType.SlotType.QUICKBAR && e.getSlotType() != InventoryType.SlotType.CONTAINER) return;
        if(e.getClickedInventory() != null && !e.getClickedInventory().getType().equals(InventoryType.PLAYER)) return;
        if (!e.getInventory().getType().equals(InventoryType.CRAFTING) && !e.getInventory().getType().equals(InventoryType.PLAYER)) return;
        if(!(e.getWhoClicked() instanceof Player)) return;
        if(e.getCurrentItem() == null) return;

        ArmorType newArmorType = ArmorType.matchType(shift ? e.getCurrentItem() : e.getCursor());

        if(!shift && newArmorType != null && e.getRawSlot() != newArmorType.getSlot())
            return;

        if(shift){
            newArmorType = ArmorType.matchType(e.getCurrentItem());

            if(newArmorType != null){
                boolean equipping = true;

                if(e.getRawSlot() == newArmorType.getSlot())
                    equipping = false;

                if(newArmorType.equals(ArmorType.HELMET) && (equipping ? e.getWhoClicked().getInventory().getHelmet() == null : e.getWhoClicked().getInventory().getHelmet() != null) ||
                        newArmorType.equals(ArmorType.CHESTPLATE) && (equipping ? e.getWhoClicked().getInventory().getChestplate() == null : e.getWhoClicked().getInventory().getChestplate() != null) ||
                        newArmorType.equals(ArmorType.LEGGINGS) && (equipping ? e.getWhoClicked().getInventory().getLeggings() == null : e.getWhoClicked().getInventory().getLeggings() != null) ||
                        newArmorType.equals(ArmorType.BOOTS) && (equipping ? e.getWhoClicked().getInventory().getBoots() == null : e.getWhoClicked().getInventory().getBoots() != null)) {

                    ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent((Player) e.getWhoClicked(), ArmorEquipEvent.EquipMethod.SHIFT_CLICK, newArmorType, equipping ? null : e.getCurrentItem(), equipping ? e.getCurrentItem() : null);
                    Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);

                    if(armorEquipEvent.isCancelled())
                        e.setCancelled(true);
                }
            }
        }

        else {
            ItemStack newArmorPiece = e.getCursor();
            ItemStack oldArmorPiece = e.getCurrentItem();

            if(numberkey) {
                if(e.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
                    ItemStack hotbarItem = e.getClickedInventory().getItem(e.getHotbarButton());

                    if(hotbarItem != null) {
                        newArmorType = ArmorType.matchType(hotbarItem);
                        newArmorPiece = hotbarItem;
                        oldArmorPiece = e.getClickedInventory().getItem(e.getSlot());
                    }

                    else {
                        newArmorType = ArmorType.matchType(e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR ? e.getCurrentItem() : e.getCursor());
                    }
                }
            }

            else {
                newArmorType = ArmorType.matchType(e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR ? e.getCurrentItem() : e.getCursor());
            }

            if(newArmorType != null && e.getRawSlot() == newArmorType.getSlot()) {
                ArmorEquipEvent.EquipMethod method = ArmorEquipEvent.EquipMethod.DRAG;

                if(e.getAction().equals(InventoryAction.HOTBAR_SWAP) || numberkey) method = ArmorEquipEvent.EquipMethod.HOTBAR_SWAP;

                ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent((Player) e.getWhoClicked(), method, newArmorType, oldArmorPiece, newArmorPiece);
                Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);

                if(armorEquipEvent.isCancelled())
                    e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void playerInteractEvent(PlayerInteractEvent e) {
        if(e.getAction() == Action.PHYSICAL) return;

        if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            final Player player = e.getPlayer();

            if(e.getClickedBlock() != null && e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Material mat = e.getClickedBlock().getType();

                for(Material material : ignoredItems)
                    if(mat.equals(material)) return;
            }

            ArmorType newArmorType = ArmorType.matchType(e.getItem());

            if(newArmorType != null) {
                if(newArmorType.equals(ArmorType.HELMET) &&
                        e.getPlayer().getInventory().getHelmet() == null ||
                        newArmorType.equals(ArmorType.CHESTPLATE) && e.getPlayer().getInventory().getChestplate() == null ||
                        newArmorType.equals(ArmorType.LEGGINGS) && e.getPlayer().getInventory().getLeggings() == null ||
                        newArmorType.equals(ArmorType.BOOTS) && e.getPlayer().getInventory().getBoots() == null) {

                    ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(e.getPlayer(), ArmorEquipEvent.EquipMethod.HOTBAR, ArmorType.matchType(e.getItem()), null, e.getItem());
                    Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);

                    if(armorEquipEvent.isCancelled()) {
                        e.setCancelled(true);
                        player.updateInventory();
                    }
                }
            }
        }
    }

    @EventHandler
    public void itemBreakEvent(PlayerItemBreakEvent e){
        ArmorType type = ArmorType.matchType(e.getBrokenItem());

        if(type != null) {
            Player p = e.getPlayer();

            ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(p, ArmorEquipEvent.EquipMethod.BROKE, type, e.getBrokenItem(), null);
            Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);

            if(armorEquipEvent.isCancelled()) {
                ItemStack i = e.getBrokenItem().clone();

                i.setAmount(1);
                i.setDurability((short) (i.getDurability() - 1));

                if(type.equals(ArmorType.HELMET)) {
                    p.getInventory().setHelmet(i);
                } else if (type.equals(ArmorType.CHESTPLATE)) {
                    p.getInventory().setChestplate(i);
                } else if (type.equals(ArmorType.LEGGINGS)) {
                    p.getInventory().setLeggings(i);
                } else if (type.equals(ArmorType.BOOTS)) {
                    p.getInventory().setBoots(i);
                }
            }
        }
    }

    @EventHandler
    public void playerDeathEvent(PlayerDeathEvent e){
        Player p = e.getEntity();

        for(ItemStack i : p.getInventory().getArmorContents()) {
            if(i != null && !i.getType().equals(Material.AIR))
                Bukkit.getServer().getPluginManager().callEvent(new ArmorEquipEvent(p, ArmorEquipEvent.EquipMethod.DEATH, ArmorType.matchType(i), i, null));
        }
    }

    private Location shift(Location start, BlockFace direction, int multiplier){
        if(multiplier == 0) return start;
        return new Location(start.getWorld(), start.getX() + direction.getModX() * multiplier, start.getY() + direction.getModY() * multiplier, start.getZ() + direction.getModZ() * multiplier);
    }
}
