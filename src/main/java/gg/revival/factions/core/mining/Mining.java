package gg.revival.factions.core.mining;

import gg.revival.factions.claims.Claim;
import gg.revival.factions.claims.ClaimManager;
import gg.revival.factions.core.FC;
import gg.revival.factions.core.FactionManager;
import gg.revival.factions.core.stats.Stats;
import gg.revival.factions.core.tools.Configuration;
import gg.revival.factions.obj.Faction;
import gg.revival.factions.obj.PlayerFaction;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Mining {

    /**
     * Contains all stone that has been placed below Y=32 since the server started up
     */
    @Getter static List<Location> placedBlocks = new ArrayList<>();

    /**
     * Runs a lottery to determine if a player should receive a drop and how big that drop should be
     * @param player The drop player
     * @param location The location the drop should be placed at if it does happen
     */
    public static void runLottery(Player player, Location location) {
        if(placedBlocks.contains(location)) return;

        Random random = new Random();
        int size = random.nextInt(8);
        float chance = random.nextFloat();

        if(size < 2)
            size = 2;

        if(location.getWorld().getEnvironment().equals(World.Environment.NETHER) && chance <= Configuration.miningGlowstoneChance) {
            generateVein(player, location, random, size, Material.GLOWSTONE);
            return;
        }

        if(chance <= Configuration.miningGoldChance && chance > Configuration.miningDiamondChance && location.getBlockY() <= 32) {
            generateVein(player, location, random, size, Material.GOLD_ORE);
            return;
        }

        if(chance <= Configuration.miningDiamondChance && chance > Configuration.miningEmeraldChance && location.getBlockY() <= 16) {
            generateVein(player, location, random, size, Material.DIAMOND_ORE);
            return;
        }

        if(chance <= Configuration.miningEmeraldChance && location.getBlockY() <= 16) {
            generateVein(player, location, random, size, Material.EMERALD_ORE);
        }
    }

    /**
     * Generates a vein at a defined location
     * @param player Player the vein has been "uncovered" by
     * @param location The location the vein should be based around
     * @param random Randomizer
     * @param size How big the vein should be
     * @param material What material the vein should consist of
     */
    public static void generateVein(Player player, Location location, Random random, int size, Material material) {
        Faction faction = FactionManager.getFactionByPlayer(player.getUniqueId());
        PlayerFaction playerFaction = null;

        if(faction != null)
            playerFaction = (PlayerFaction)faction;

        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        World world = location.getWorld();

        double rpi = random.nextDouble() * 3.1415926535897D;

        double x1 = x + Math.sin(rpi) * size / 8.0D;
        double x2 = x - Math.sin(rpi) * size / 8.0D;
        double z1 = z + Math.cos(rpi) * size / 8.0D;
        double z2 = z - Math.cos(rpi) * size / 8.0D;

        double y1 = y + random.nextInt(3) + 2;
        double y2 = y + random.nextInt(3) + 2;

        int found = 0;

        for (int i = 0; i <= size; i++) {
            double xPos = x1 + (x2 - x1) * i / size;
            double yPos = y1 + (y2 - y1) * i / size;
            double zPos = z1 + (z2 - z1) * i / size;

            double work = random.nextDouble() * size / 16.0D;
            double workXZ = (Math.sin((float) (i * 3.1415926535897D / size)) + 1.0D) * work + 1.0D;
            double workY = (Math.sin((float) (i * 3.1415926535897D / size)) + 1.0D) * work + 1.0D;

            int xBegin = (int) Math.floor(xPos - workXZ / 2.0D);
            int yBegin = (int) Math.floor(yPos - workY / 2.0D);
            int zBegin = (int) Math.floor(zPos - workXZ / 2.0D);

            int xFinish = (int) Math.floor(xPos + workXZ / 2.0D);
            int yFinish = (int) Math.floor(yPos + workY / 2.0D);
            int zFinish = (int) Math.floor(zPos + workXZ / 2.0D);

            for (int ix = xBegin; ix <= xFinish; ix++) {
                double xMore = (ix + 0.5D - xPos) / (workXZ / 2.0D);

                if (xMore * xMore < 1.0D) {
                    for (int iy = yBegin; iy <= yFinish; iy++) {

                        double yMore = (iy + 0.5D - yPos) / (workY / 2.0D);

                        if (xMore * xMore + yMore * yMore < 1.0D) {
                            for (int iz = zBegin; iz <= zFinish; iz++) {
                                double zMore = (iz + 0.5D - zPos) / (workXZ / 2.0D);

                                if (xMore * xMore + yMore * yMore + zMore * zMore < 1.0D) {
                                    Block block = world.getBlockAt(new Location(world, ix, iy, iz));
                                    Claim claim = ClaimManager.getClaimAt(block.getLocation(), false);

                                    if(claim != null) {
                                        Faction claimOwner = claim.getClaimOwner();

                                        if(playerFaction == null || !playerFaction.getFactionID().equals(claimOwner.getFactionID())) continue;
                                    }

                                    if(block == null || block.getType().equals(Material.AIR)) continue;

                                    if(material.equals(Material.GLOWSTONE)) {
                                        if(!block.getType().equals(Material.NETHERRACK)) continue;

                                        player.playSound(block.getLocation(), Sound.GLASS, 1.0f, 1.0f);
                                    }

                                    if(material.equals(Material.GOLD_ORE) || material.equals(Material.DIAMOND_ORE) || material.equals(Material.EMERALD_ORE)) {
                                        if(!block.getType().equals(Material.STONE)) continue;

                                        player.playSound(block.getLocation(), Sound.DIG_STONE, 1.0f, 1.0f);
                                    }

                                    block.setType(material);
                                    found++;
                                }
                            }
                        }
                    }
                }
            }
        }

        if(found > 0) {
            if(material.equals(Material.GOLD_ORE)) {
                if(Configuration.announceFoundGold) {
                    Bukkit.broadcastMessage("[RM] " + ChatColor.GOLD + player.getName() + " uncovered " + found + " Gold Ore");
                } else {
                    player.sendMessage("[RM] " + ChatColor.GOLD + player.getName() + " uncovered " + found + " Gold Ore");
                }

                Stats.getStats(player.getUniqueId()).addGold(found);
            }

            if(material.equals(Material.DIAMOND_ORE)) {
                if(Configuration.announceFoundDiamond) {
                    Bukkit.broadcastMessage("[RM] " + ChatColor.AQUA + player.getName() + " uncovered " + found + " Diamond Ore");
                } else {
                    player.sendMessage("[RM] " + ChatColor.AQUA + player.getName() + " uncovered " + found + " Diamond Ore");
                }

                Stats.getStats(player.getUniqueId()).addDiamond(found);
            }

            if(material.equals(Material.EMERALD_ORE)) {
                if(Configuration.announceFoundEmerald) {
                    Bukkit.broadcastMessage("[RM] " + ChatColor.GREEN + player.getName() + " uncovered " + found + " Emerald Ore");
                } else {
                    player.sendMessage("[RM] " + ChatColor.GREEN + player.getName() + " uncovered " + found + " Emerald Ore");
                }

                Stats.getStats(player.getUniqueId()).addEmerald(found);
            }

            if(material.equals(Material.GLOWSTONE)) {
                if(Configuration.announceFoundGlowstone) {
                    Bukkit.broadcastMessage("[RM] " + ChatColor.YELLOW + player.getName() + " uncovered " + found + " Glowstone Blocks");
                } else {
                    player.sendMessage("[RM] " + ChatColor.YELLOW + player.getName() + " uncovered " + found + " Glowstone Blocks");
                }
            }
        }
    }

    public static void onEnable() {
        loadListeners();
    }

    public static void loadListeners() {
        if(Configuration.miningEnabled)
            Bukkit.getPluginManager().registerEvents(new MiningEventsListener(), FC.getFactionsCore());
    }

}
