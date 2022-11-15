package net.lcly.party.manager;

import com.github.mattnicee7.mattlib.cooldown.CooldownMap;
import lombok.Getter;
import net.lcly.party.PartyLY;
import net.lcly.party.model.Party;
import net.lcly.party.model.PartyInvite;

import java.util.*;
@Getter
public class PartyManager {

    private final Set<Party> parties = new HashSet<>();
    private final Map<UUID, Party> playersInPartyCache = new HashMap<>();
    private final CooldownMap<PartyInvite> partyInvitesInCooldown = new CooldownMap<>();
    private final PartyLY plugin;

    public PartyManager(PartyLY plugin) {
        this.plugin = plugin;
    }

    public Party getParty(UUID playerUUID) {
        return playersInPartyCache.get(playerUUID);
    }

    public boolean hasParty(UUID playerUUID) {
        return playersInPartyCache.containsKey(playerUUID);
    }

    public void removeParty(Party party) {
        parties.remove(party);
    }
    public void disband(UUID playerUUID) {
        Party party = getParty(playerUUID);
        removeParty(party);
        party.getMembers().forEach(member -> updatePartyCache(member, null));
    }

    public void invite(Party party, PartyInvite invite) {
        if (this.plugin.getConfig().getBoolean("invite-cooldown-mode")) {
            getPartyInvitesInCooldown().put(invite, Long.parseLong(plugin.getConfig().getString("invite-cooldown-time")));
        }
        party.getInvites().add(invite);
    }

    public void join(UUID sender, UUID target) {
        Party party = getParty(target);
        updatePartyCache(sender, party);
        party.getMembers().add(sender);
        party.getInvites().remove(party.getInvite(sender, target));
    }

    public void leave(UUID playerUUID) {
        Party party = getParty(playerUUID);
        updatePartyCache(playerUUID, null);
        party.getMembers().remove(playerUUID);
    }

    public void kick(UUID playerUUID) {
        Party party = getParty(playerUUID);
        updatePartyCache(playerUUID, null);
        party.getMembers().remove(playerUUID);
    }


    public void updatePartyCache(UUID playerUUID, Party party) {
        if (party != null) {
            this.playersInPartyCache.put(playerUUID, party);
        } else {
            this.playersInPartyCache.remove(playerUUID);
        }
    }
}
