package net.lcly.party.model;

import lombok.Getter;
import lombok.Setter;
import net.lcly.party.PartyLY;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Party {

    @Getter
    @Setter
    private UUID leader;
    @Getter
    private final Set<UUID> members = new HashSet<>();
    @Getter
    private final Set<PartyInvite> invites = new HashSet<>();
    @Getter @Setter private int maxLimit;

    public Party(UUID leader, PartyLY plugin) {
        this.leader = leader;
        this.members.add(leader);
        this.maxLimit = Integer.parseInt(plugin.getConfig().getString("party-max-limit"));
    }

    public boolean isMember(UUID playerUUID) {
        return members.contains(playerUUID);
    }

    public boolean isLeader(UUID playerUUID) {
        return leader.equals(playerUUID);
    }

    public PartyInvite getInvite(UUID invited, UUID inviter) {
        for (PartyInvite invite : invites) {
            if (invite.getInvited().equals(invited) && invite.getInviter().equals(inviter)) {
                return invite;
            }
        }
        return null;
    }
}