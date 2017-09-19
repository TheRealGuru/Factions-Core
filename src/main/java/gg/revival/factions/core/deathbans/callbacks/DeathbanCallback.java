package gg.revival.factions.core.deathbans.callbacks;

import gg.revival.factions.core.deathbans.Death;

import java.util.Set;

public interface DeathbanCallback {

    void onQueryDone(Set<Death> result);

}
