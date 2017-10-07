package gg.revival.factions.core.mechanics.mobstacking;

import gg.revival.factions.core.FC;
import gg.revival.factions.core.tools.Configuration;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Mobstacker {

    /**
     * Multimap that contains players split cooldowns for each entity type
     */
    @Getter static Map<UUID, List<EntityType>> splitCooldowns = new HashMap<>();

    /**
     * Contains a list of protected (cant be stacked) entities
     */
    @Getter static List<UUID> protectedEntities = new ArrayList<>();

    /**
     * Makes an entity has protected so it cant be merged
     * @param entity The entity to be protected
     * @param time Time it should be protected for
     */
    public static void setProtected(Entity entity, int time) {
        if(protectedEntities.contains(entity.getUniqueId())) return;

        UUID uuid = entity.getUniqueId();

        protectedEntities.add(uuid);

        new BukkitRunnable() {
            public void run() {
                protectedEntities.remove(uuid);
            }
        }.runTaskLaterAsynchronously(FC.getFactionsCore(), time * 20L);
    }

    /**
     * Returns true if both entities are the same
     * @param entityOne
     * @param entityTwo
     * @return Both entities are the same
     */
    public static boolean isSame(Entity entityOne, Entity entityTwo) {
        if(!entityOne.getType().equals(entityTwo.getType())) return false;

        if(entityOne.getType().equals(EntityType.SHEEP) && entityTwo.getType().equals(EntityType.SHEEP)) {
            Sheep sheepOne = (Sheep)entityOne;
            Sheep sheepTwo = (Sheep)entityTwo;

            if(!sheepOne.getColor().equals(sheepTwo.getColor())) return false;
        }

        if(entityOne.getType().equals(EntityType.ZOMBIE) && entityTwo.getType().equals(EntityType.ZOMBIE)) {
            Zombie zombieOne = (Zombie)entityOne;
            Zombie zombieTwo = (Zombie)entityTwo;

            if((zombieOne.isBaby() && !zombieTwo.isBaby()) ||
                    (!zombieOne.isBaby() && zombieTwo.isBaby()) ||
                    (zombieOne.isVillager() && !zombieTwo.isVillager()) ||
                    (!zombieOne.isVillager() && zombieTwo.isVillager())) return false;
        }

        return true;
    }

    /**
     * Returns true if the given entity is a stack
     * @param entity
     * @return
     */
    public static boolean isStack(Entity entity) {
        return getStackSize(entity) > 0;
    }

    /**
     * Returns a value of how many entities are stacked on the given entity
     * @param entity The stacked entity
     * @return How many entities are stacked on given entity
     */
    public static int getStackSize(Entity entity) {
        if(entity.getCustomName() == null) return 0;

        String name = entity.getCustomName();

        if(!name.contains(ChatColor.YELLOW + "x")) return 0;

        String count = name.replace(ChatColor.YELLOW + "x", "");

        if(Integer.valueOf(count) != 0)
            return Integer.valueOf(count);

        return 0;
    }

    /**
     * Attempts to merge two entities/stacks together
     * @param entity
     * @param anotherEntity
     */
    public static void attemptStack(Entity entity, Entity anotherEntity) {
        LivingEntity entityOne = (LivingEntity)entity, entityTwo = (LivingEntity)anotherEntity;

        if(!isSame(entityOne, entityTwo)) return;

        if(entityOne.isLeashed() || entityTwo.isLeashed()) return;

        int entityOneStackSize = 1, entityTwoStackSize = 1;

        if(isStack(entityOne))
            entityOneStackSize = getStackSize(entityOne);

        if(isStack(entityTwo))
            entityTwoStackSize = getStackSize(entityTwo);

        int newStackSize = entityOneStackSize + entityTwoStackSize;

        setProtected(entityOne, 5);
        setProtected(entityTwo, 5);

        if(entityOneStackSize >= entityTwoStackSize) {
            entityTwo.remove();
            entityOne.setCustomName(ChatColor.YELLOW + "x" + newStackSize);
        }

        else {
            entityOne.remove();
            entityTwo.setCustomName(ChatColor.YELLOW + "x" + newStackSize);
        }
    }

    /**
     * Subtracts a stack from a given entity stack
     * @param entity The entity stack
     */
    public static void subtractFromStack(Entity entity) {
        if(!isStack(entity)) return;

        int currentStack = getStackSize(entity);
        int newStack = currentStack - 1;

        Location spawnLocation = entity.getLocation();

        new BukkitRunnable() {
            public void run() {
                LivingEntity newEntity = (LivingEntity) entity.getWorld().spawnEntity(spawnLocation, entity.getType());

                if(entity.getType().equals(EntityType.SHEEP))
                {
                    Sheep oldSheep = (Sheep)entity;
                    Sheep newSheep = (Sheep)newEntity;

                    newSheep.setSheared(oldSheep.isSheared());
                    newSheep.setAge(oldSheep.getAge());
                    newSheep.setColor(oldSheep.getColor());
                }

                if(entity.getType().equals(EntityType.ZOMBIE)) {
                    Zombie oldZombie = (Zombie)entity;
                    Zombie newZombie = (Zombie)newEntity;

                    newZombie.setBaby(oldZombie.isBaby());
                    newZombie.setVillager(oldZombie.isVillager());
                }

                if(newStack > 1)
                    newEntity.setCustomName(ChatColor.YELLOW + "x" + newStack);

                setProtected(newEntity, 5);
            }
        }.runTask(FC.getFactionsCore());
    }

    /**
     * Splits a stack in half and spawns two protected entity stacks
     * @param entity The entity to be split
     */
    public static void splitStack(Entity entity) {
        if(!isStack(entity)) return;
        if(entity instanceof Monster) return;
        if(getStackSize(entity) <= 1) return;

        int newStackSize = getStackSize(entity) / 2;

        Entity splitEntity = entity.getWorld().spawnEntity(entity.getLocation(), entity.getType());

        if(newStackSize % 2 == 0) {
            if(newStackSize > 1) {
                splitEntity.setCustomName(ChatColor.YELLOW + "x" + newStackSize);
                entity.setCustomName(ChatColor.YELLOW + "x" + newStackSize);
            }
        }

        else {
            splitEntity.setCustomName(ChatColor.YELLOW + "x" + (newStackSize + 1));

            if(newStackSize > 1)
                entity.setCustomName(ChatColor.YELLOW + "x" + newStackSize);
        }

        setProtected(entity, 30);
        setProtected(splitEntity, 30);
    }

    /**
     * Timer that constantly checks for new mobs to merge
     */
    public static void run() {
        if(!Configuration.mobstackingEnabled) return;

        new BukkitRunnable()
        {
            public void run()
            {
                for(World worlds : Bukkit.getServer().getWorlds()) {
                    for(LivingEntity entity : worlds.getLivingEntities()) {
                        if(protectedEntities.contains(entity.getUniqueId())) continue;
                        if(getStackSize(entity) >= Configuration.mobstackingMaxStack) continue;

                        EntityType type = entity.getType();

                        if(
                                type.equals(EntityType.SHEEP) ||
                                        type.equals(EntityType.CAVE_SPIDER) ||
                                        type.equals(EntityType.CHICKEN) ||
                                        type.equals(EntityType.COW) ||
                                        type.equals(EntityType.CREEPER) ||
                                        type.equals(EntityType.GHAST) ||
                                        type.equals(EntityType.GUARDIAN) ||
                                        type.equals(EntityType.PIG) ||
                                        type.equals(EntityType.PIG_ZOMBIE) ||
                                        type.equals(EntityType.RABBIT) ||
                                        type.equals(EntityType.SPIDER) ||
                                        type.equals(EntityType.SQUID) ||
                                        type.equals(EntityType.ZOMBIE) ||
                                        type.equals(EntityType.SKELETON)) {

                            for(Entity nearbyEntities : entity.getNearbyEntities(10, 3, 10)) {
                                if(!nearbyEntities.getType().equals(type)) continue;
                                if(!(nearbyEntities instanceof LivingEntity)) continue;
                                if(protectedEntities.contains(nearbyEntities.getUniqueId())) continue;
                                if(getStackSize(nearbyEntities) >= Configuration.mobstackingMaxStack) continue;

                                attemptStack(entity, nearbyEntities);
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(FC.getFactionsCore(), 0L, Configuration.mobstackingInterval * 20L);
    }
}
