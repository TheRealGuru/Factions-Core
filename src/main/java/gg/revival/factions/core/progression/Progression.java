package gg.revival.factions.core.progression;

import gg.revival.factions.core.FC;
import gg.revival.factions.core.progression.command.ProgressionCommand;
import lombok.Getter;

public class Progression {

    @Getter private FC core;

    public Progression(FC core) {
        this.core = core;

        onEnable();
    }

    public void onEnable() {
        loadCommands();
    }

    private void loadCommands() {
        core.getCommand("progression").setExecutor(new ProgressionCommand(core));
    }

}
