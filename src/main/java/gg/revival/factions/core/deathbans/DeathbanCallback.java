package gg.revival.factions.core.deathbans;

import java.util.Set;

public interface DeathbanCallback
{

    void onQueryDone(Set<Death> result);

}
