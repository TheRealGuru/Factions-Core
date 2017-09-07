package gg.revival.factions.core.tools;

import java.util.UUID;

public interface OfflinePlayerCallback
{

    void onQueryDone(UUID uuid, String username);

}
