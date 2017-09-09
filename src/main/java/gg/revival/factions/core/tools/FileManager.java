package gg.revival.factions.core.tools;

import gg.revival.factions.core.FC;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class FileManager {

    public static File configFile;
    public static FileConfiguration configConfig;

    public static File eventsFile;
    public static FileConfiguration eventsConfig;

    public static void createFiles() {
        try {
            if (!FC.getFactionsCore().getDataFolder().exists()) {
                FC.getFactionsCore().getDataFolder().mkdirs();
            }

            configFile = new File(FC.getFactionsCore().getDataFolder(), "config.yml");
            eventsFile = new File(FC.getFactionsCore().getDataFolder(), "events.yml");

            if (!configFile.exists()) {
                configFile.getParentFile().mkdirs();
                FC.getFactionsCore().saveResource("config.yml", true);
            }

            if(!eventsFile.exists()) {
                eventsFile.getParentFile().mkdirs();
            }

            configConfig = new YamlConfiguration();
            eventsConfig = new YamlConfiguration();

            try {
                configConfig.load(configFile);
                eventsConfig.load(eventsFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static FileConfiguration getConfig() {
        return configConfig;
    }
    public static FileConfiguration getEvents() { return eventsConfig; }

    public static void saveConfig() {
        try {
            configConfig.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveEvents() {
        try {
            eventsConfig.save(eventsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void reloadFiles() {
        try {
            if (!FC.getFactionsCore().getDataFolder().exists()) {
                FC.getFactionsCore().getDataFolder().mkdirs();
            }

            configFile = new File(FC.getFactionsCore().getDataFolder(), "config.yml");
            eventsFile = new File(FC.getFactionsCore().getDataFolder(), "events.yml");

            if (!configFile.exists()) {
                configFile.getParentFile().mkdirs();
                FC.getFactionsCore().saveResource("config.yml", true);
            }

            if(!eventsFile.exists()) {
                eventsFile.getParentFile().mkdirs();
            }

            configConfig = new YamlConfiguration();
            eventsConfig = new YamlConfiguration();

            try {
                configConfig.load(configFile);
                eventsConfig.load(eventsFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}