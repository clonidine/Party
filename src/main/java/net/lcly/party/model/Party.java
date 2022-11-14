package net.lcly.party.model;

import lombok.Getter;
import lombok.Setter;
import net.lcly.party.PartyLY;
import net.lcly.party.manager.PartyManager;

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
    private final Set<PartyInvite> partyInvites = new HashSet<>();
    private final PartyManager manager;
    @Getter @Setter private int maxLimit;
    private final PartyLY plugin;

    public Party(UUID leader, PartyLY plugin) {
        this.plugin = plugin;
        this.leader = leader;
        this.manager = plugin.getPartyManager();
        this.members.add(leader);
        this.maxLimit = Integer.parseInt(plugin.getConfig().getString("party-max-limit"));
    }

    public boolean isMember(UUID playerUUID) {
        return this.members.contains(playerUUID);
    }

    public boolean isLeader(UUID playerUUID) {
        return leader.equals(playerUUID);
    }

    public void disband(UUID playerUUID) {
        Party party = this.manager.getParty(playerUUID);
        this.manager.removeParty(party);
        party.getMembers().forEach(member -> this.manager.updatePartyCache(member, null));
    }

    public void invite(PartyInvite invite) {
        if (this.plugin.getConfig().getBoolean("invite-cooldown-mode")) {
            this.manager.getPartyInvitesInCooldown().put(invite, Long.parseLong(plugin.getConfig().getString("invite-cooldown-time")));
        }
        this.partyInvites.add(invite);
    }

    public void join(UUID sender, UUID target) {
        Party party = this.manager.getParty(target);

        this.manager.updatePartyCache(sender, party);
        this.members.add(sender);
        this.partyInvites.remove(getInvite(sender, target));
    }

    public void leave(UUID playerUUID) {
        Party party = this.manager.getParty(playerUUID);
        this.manager.updatePartyCache(playerUUID, null);
        party.getMembers().remove(playerUUID);
    }

    public void kick(UUID playerUUID) {
        Party party = this.manager.getParty(playerUUID);
        this.manager.updatePartyCache(playerUUID, null);
        party.getMembers().remove(playerUUID);
    }

    public PartyInvite getInvite(UUID invited, UUID inviter) {
        for (PartyInvite invite : partyInvites) {
            if (invite.getInvited().equals(invited) && invite.getInviter().equals(inviter)) {
                return invite;
            }
        }
        return null;
    }
}