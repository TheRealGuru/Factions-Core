package gg.revival.factions.core.locations;

import gg.revival.factions.core.FC;
import gg.revival.factions.core.locations.command.EndCommand;
import gg.revival.factions.core.locations.command.EndExitCommand;
import gg.revival.factions.core.locations.command.SpawnCommand;
import gg.revival.factions.core.locations.listener.LocationsListener;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class Locations {

    @Getter private FC core;
    @Getter @Setter Location spawnLocation, endSpawnLocation, endExitLocation;

    public Locations(FC core) {
        this.core = core;

        onEnable();
    }

    public void saveSpawnLocation(Location location) {
        core.getFileManager().getConfig().set("locations.overworld-spawn.x", location.getX());
        core.getFileManager().getConfig().set("locations.overworld-spawn.y", location.getY());
        core.getFileManager().getConfig().set("locations.overworld-spawn.z", location.getZ());
        core.getFileManager().getConfig().set("locations.overworld-spawn.yaw", location.getYaw());
        core.getFileManager().getConfig().set("locations.overworld-spawn.pitch", location.getPitch());
        core.getFileManager().getConfig().set("locations.overworld-spawn.world", location.getWorld().getName());
        core.getFileManager().saveConfig();

        setSpawnLocation(location);
    }

    public void saveEndSpawnLocation(Location location) {
        core.getFileManager().getConfig().set("locations.end-spawn.x", location.getX());
        core.getFileManager().getConfig().set("locations.end-spawn.y", location.getY());
        core.getFileManager().getConfig().set("locations.end-spawn.z", location.getZ());
        core.getFileManager().getConfig().set("locations.end-spawn.yaw", location.getYaw());
        core.getFileManager().getConfig().set("locations.end-spawn.pitch", location.getPitch());
        core.getFileManager().getConfig().set("locations.end-spawn.world", location.getWorld().getName());
        core.getFileManager().saveConfig();

        setEndSpawnLocation(location);
    }

    public void saveEndExitLocation(Location location) {
        core.getFileManager().getConfig().set("locations.end-exit.x", location.getX());
        core.getFileManager().getConfig().set("locations.end-exit.y", location.getY());
        core.getFileManager().getConfig().set("locations.end-exit.z", location.getZ());
        core.getFileManager().getConfig().set("locations.end-exit.yaw", location.getYaw());
        core.getFileManager().getConfig().set("locations.end-exit.pitch", location.getPitch());
        core.getFileManager().getConfig().set("locations.end-exit.world", location.getWorld().getName());
        core.getFileManager().saveConfig();

        setEndExitLocation(location);
    }

    public void onEnable() {
        loadListeners();
        loadCommands();
    }

    public void loadListeners() {
        Bukkit.getPluginManager().registerEvents(new LocationsListener(core), core);
    }

    private void loadCommands() {
        core.getCommand("spawn").setExecutor(new SpawnCommand(core));
        core.getCommand("end").setExecutor(new EndCommand(core));
        core.getCommand("endexit").setExecutor(new EndExitCommand(core));
    }

}
