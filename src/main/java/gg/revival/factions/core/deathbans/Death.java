package gg.revival.factions.core.deathbans;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public class Death {

    @Getter UUID uuid;
    @Getter UUID killed;
    @Getter @Setter String reason;
    @Getter long createdTime;
    @Getter @Setter long expiresTime;

    public Death(UUID uuid, UUID killed, String reason, long createdTimed, long expiresTime) {
        this.uuid = uuid;
        this.killed = killed;
        this.reason = reason;
        this.createdTime = createdTimed;
        this.expiresTime = expiresTime;
    }

    public boolean isExpired() {
        return expiresTime <= System.currentTimeMillis();
    }

}
