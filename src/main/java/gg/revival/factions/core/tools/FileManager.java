package gg.revival.factions.core.tools;

import gg.revival.factions.core.FC;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class FileManager {

    public static File configFile;
    public static FileConfiguration configConfig;

    public static void createFiles() {
        try {
            if (!FC.getFactionsCore().getDataFolder().exists()) {
                FC.getFactionsCore().getDataFolder().mkdirs();
            }

            configFile = new File(FC.getFactionsCore().getDataFolder(), "config.yml");

            if (!configFile.exists()) {
                configFile.getParentFile().mkdirs();
                FC.getFactionsCore().saveResource("config.yml", true);
            }

            configConfig = new YamlConfiguration();

            try {
                configConfig.load(configFile);
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

    public static void saveConfig() {
        try {
            configConfig.save(configFile);
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

            if (!configFile.exists()) {
                configFile.getParentFile().mkdirs();
                FC.getFactionsCore().saveResource("config.yml", true);
            }

            configConfig = new YamlConfiguration();

            try {
                configConfig.load(configFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}