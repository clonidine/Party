package net.lcly.party.manager;

import com.github.mattnicee7.mattlib.cooldown.CooldownMap;
import lombok.Getter;
import net.lcly.party.PartyLY;
import net.lcly.party.model.Party;
import net.lcly.party.model.PartyInvite;

import java.util.*;

public class PartyManager {

    @Getter private final Set<Party> parties = new HashSet<>();
    @Getter private final Map<UUID, Party> playersInPartyCache = new HashMap<>();
    @Getter private final CooldownMap<PartyInvite> partyInvitesInCooldown = new CooldownMap<>();

    public PartyManager(PartyLY plugin) {
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

    public void updatePartyCache(UUID playerUUID, Party party) {
        if (party != null) {
            this.playersInPartyCache.put(playerUUID, party);
        } else {
            this.playersInPartyCache.remove(playerUUID);
        }
    }
}
