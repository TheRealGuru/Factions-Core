package gg.revival.factions.core.locations;

import gg.revival.factions.core.FC;
import gg.revival.factions.core.locations.command.EndCommand;
import gg.revival.factions.core.locations.command.SpawnCommand;
import gg.revival.factions.core.locations.listener.LocationsListener;
import gg.revival.factions.core.tools.FileManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

public class Locations {

    @Getter @Setter static Location spawnLocation, endSpawnLocation;

    public static void saveSpawnLocation(Location location) {
        FileConfiguration config = FileManager.getConfig();

        config.set("locations.overworld-spawn.x", location.getX());
        config.set("locations.overworld-spawn.y", location.getY());
        config.set("locations.overworld-spawn.z", location.getZ());
        config.set("locations.overworld-spawn.yaw", location.getYaw());
        config.set("locations.overworld-spawn.pitch", location.getPitch());
        config.set("locations.overworld-spawn.world", location.getWorld().getName());
        FileManager.saveConfig();

        setSpawnLocation(location);
    }

    public static void saveEndSpawnLocation(Location location) {
        FileConfiguration config = FileManager.getConfig();

        config.set("locations.end-spawn.x", location.getX());
        config.set("locations.end-spawn.y", location.getY());
        config.set("locations.end-spawn.z", location.getZ());
        config.set("locations.end-spawn.yaw", location.getYaw());
        config.set("locations.end-spawn.pitch", location.getPitch());
        config.set("locations.end-spawn.world", location.getWorld().getName());
        FileManager.saveConfig();

        setEndSpawnLocation(location);
    }

    public static void onEnable() {
        loadListeners();
        loadCommands();
    }

    public static void loadListeners() {
        Bukkit.getPluginManager().registerEvents(new LocationsListener(), FC.getFactionsCore());
    }

    public static void loadCommands() {
        FC.getFactionsCore().getCommand("spawn").setExecutor(new SpawnCommand());
        FC.getFactionsCore().getCommand("end").setExecutor(new EndCommand());
    }

}
