package net.lcly.party.commands.subcommand.impl;

import net.lcly.party.PartyLY;
import net.lcly.party.commands.subcommand.SubCommand;
import net.lcly.party.dispatcher.MessageDispatcher;
import net.lcly.party.manager.PartyManager;
import net.lcly.party.model.Party;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class PartyKickSubCommand implements SubCommand {
    private final PartyManager manager;
    private final MessageDispatcher dispatcher;

    public PartyKickSubCommand(PartyLY plugin) {
        this.manager = plugin.getPartyManager();
        this.dispatcher = plugin.getMessageDispatcher();
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("kick");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player playerSender = (Player) sender;
        Party party = this.manager.getParty(playerSender.getUniqueId());

        if (args.length != 2) {
            this.dispatcher.dispatch(playerSender, "help");
            return false;
        }

        if (party == null) {
            this.dispatcher.dispatch(playerSender, "no-party-error");
            return false;
        }

        Player target = Bukkit.getPlayerExact(args[1]);

        if (!party.isLeader(playerSender.getUniqueId())) {
            this.dispatcher.dispatch(playerSender, "not-leader-error");
            return false;
        }

        if (target == null) {
            this.dispatcher.dispatch(playerSender, "player-not-found-error");
            return false;
        }

        if (!party.isMember(target.getUniqueId())) {
            this.dispatcher.dispatch(playerSender, "player-not-in-your-party-error");
            return false;
        }

        if (playerSender.getUniqueId().equals(target.getUniqueId())) {
            this.dispatcher.dispatch(playerSender, "cannot-kick-yourself-error");
            return false;
        }

        party.kick(target.getUniqueId());
        party.getMembers().forEach(member -> this.dispatcher.dispatch(Bukkit.getPlayer(member), "party-members-kick-message", player ->
                player.replace("%player%", target.getName())));
        this.dispatcher.dispatch(target, "target-kick-message");
        return true;
    }
}
