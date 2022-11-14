package net.lcly.party.model;

import lombok.Getter;

import java.util.Date;
import java.util.UUID;

public class PartyInvite {

    @Getter private final UUID inviter;
    @Getter private final UUID invited;
    @Getter private final Date moment;

    public PartyInvite(UUID inviter, UUID invited) {
        this.inviter = inviter;
        this.invited = invited;
        this.moment = new Date();
    }
}
