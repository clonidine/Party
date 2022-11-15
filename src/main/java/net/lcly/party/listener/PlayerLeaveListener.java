package net.lcly.party.listener;

import net.lcly.party.PartyLY;
import net.lcly.party.dispatcher.MessageDispatcher;
import net.lcly.party.manager.PartyManager;
import net.lcly.party.model.Party;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Objects;

public class PlayerLeaveListener implements Listener {

    private final PartyLY plugin;
    private final PartyManager partyManager;

    public PlayerLeaveListener(PartyLY plugin) {
        this.plugin = plugin;
        this.partyManager = this.plugin.getPartyManager();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Party party = partyManager.getParty(player.getUniqueId());

        if (party == null) {
            return;
        }

        MessageDispatcher messageDispatcher = this.plugin.getMessageDispatcher();

        if (!party.isLeader(player.getUniqueId())) {
            party.getMembers().forEach(member -> messageDispatcher.dispatch(Bukkit.getPlayer(member), "party-members-leaving-message", playerBukkit ->
                    playerBukkit.replace("%player%", player.getName())));
            partyManager.updatePartyCache(player.getUniqueId(), null);
            party.getMembers().remove(player.getUniqueId());
            return;
        }

        party.getMembers().stream().filter(Objects::nonNull).forEach(member -> messageDispatcher.dispatch(Bukkit.getPlayer(member), "disbanded-party-message"));
        partyManager.disband(player.getUniqueId());
    }
}