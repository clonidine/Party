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

public class PartyLeaveSubCommand implements SubCommand {

    private final PartyManager manager;
    private final MessageDispatcher dispatcher;
    public PartyLeaveSubCommand(PartyLY plugin) {
        this.manager = plugin.getPartyManager();
        this.dispatcher = plugin.getMessageDispatcher();
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("exit", "leave");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player target = (Player) sender;

        if (!this.manager.hasParty(target.getUniqueId())) {
            this.dispatcher.dispatch(target, "no-party-error");
            return false;
        }

        Party party = this.manager.getParty(target.getUniqueId());

        if (args.length != 1) {
            this.dispatcher.dispatch(target, "help");
            return false;
        }

        if (party.isLeader(target.getUniqueId())) {
            this.dispatcher.dispatch(target, "leader-cannot-leave-party-error");
            return false;
        }

        party.leave(target.getUniqueId());
        this.dispatcher.dispatch(target, "left-party-message");
        party.getMembers().forEach(member -> this.dispatcher.dispatch(Bukkit.getPlayer(member), "party-members-leaving-message", player ->
                player.replace("%player%", target.getName())));
        return true;
    }
}
