package gg.revival.factions.core.progression;

import gg.revival.factions.core.FC;
import gg.revival.factions.core.progression.command.ProgressionCommand;

public class Progression {

    public static void onEnable() {
        loadCommands();
    }

    private static void loadCommands() {
        FC.getFactionsCore().getCommand("progression").setExecutor(new ProgressionCommand());
    }

}
