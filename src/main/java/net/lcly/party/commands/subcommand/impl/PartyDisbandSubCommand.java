package net.lcly.party.commands.subcommand.impl;

import net.lcly.party.PartyLY;
import net.lcly.party.commands.subcommand.SubCommand;
import net.lcly.party.dispatcher.MessageDispatcher;
import net.lcly.party.manager.PartyManager;
import net.lcly.party.model.Party;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class PartyDisbandSubCommand implements SubCommand {

    private final MessageDispatcher dispatcher;
    private final PartyManager partyManager;

    public PartyDisbandSubCommand(PartyLY plugin) {
        this.dispatcher = plugin.getMessageDispatcher();
        this.partyManager = plugin.getPartyManager();
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("disband", "d");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        Party party = this.partyManager.getParty(player.getUniqueId());

        if (!this.partyManager.hasParty(player.getUniqueId())) {
            this.dispatcher.dispatch(sender, "no-party-error");
            return false;
        }

        if (!party.isLeader(player.getUniqueId())) {
            this.dispatcher.dispatch(sender, "not-leader-error");
            return false;
        }

        party.getMembers().forEach(member -> this.dispatcher.dispatch(Bukkit.getPlayer(member), "disbanded-party-message"));
        partyManager.disband(player.getUniqueId());
        return true;
    }
}
