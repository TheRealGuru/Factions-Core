package gg.revival.factions.core.lives;

import java.util.UUID;

public interface LivesUpdateCallback {

    void onUpdate(UUID uuid, int newLives);

}
