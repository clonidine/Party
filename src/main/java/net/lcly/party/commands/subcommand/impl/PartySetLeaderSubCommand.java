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

public class PartySetLeaderSubCommand implements SubCommand {

    private final PartyManager manager;
    private final MessageDispatcher dispatcher;

    public PartySetLeaderSubCommand(PartyLY plugin) {
        this.manager = plugin.getPartyManager();
        this.dispatcher = plugin.getMessageDispatcher();
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("setleader");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        Party party = this.manager.getParty(player.getUniqueId());

        if (!manager.hasParty(player.getUniqueId())) {
            this.dispatcher.dispatch(player, "no-party-error");
            return false;
        }

        if (args.length != 2) {
            this.dispatcher.dispatch(player, "help");
            return false;
        }

        Player target = Bukkit.getPlayerExact(args[1]);

        if (!party.isLeader(player.getUniqueId())) {
            this.dispatcher.dispatch(player, "not-leader-error");
            return false;
        }

        if (target == null) {
            this.dispatcher.dispatch(player, "player-not-found-error");
            return false;
        }

        if (target.getUniqueId().equals(player.getUniqueId())) {
            this.dispatcher.dispatch(player, "cannot-set-leader-yourself-error");
            return false;
        }

        if (!party.isMember(target.getUniqueId())) {
            this.dispatcher.dispatch(player, "player-not-in-your-party-error");
            return false;
        }

        party.setLeader(target.getUniqueId());
        this.dispatcher.dispatch(player, "leader-set-message", member ->
                member.replace("%player%", target.getName()));
        this.dispatcher.dispatch(target, "leader-set-target-message");
        return false;
    }
}
