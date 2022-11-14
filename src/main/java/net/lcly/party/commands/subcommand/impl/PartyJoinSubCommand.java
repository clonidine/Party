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

public class PartyJoinSubCommand implements SubCommand {

    private final PartyManager manager;
    private final MessageDispatcher dispatcher;

    public PartyJoinSubCommand(PartyLY plugin) {
        this.manager = plugin.getPartyManager();
        this.dispatcher = plugin.getMessageDispatcher();
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("join");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player joiner = (Player) sender;
        Party joinerParty = this.manager.getParty(joiner.getUniqueId());

        if (args.length != 2) {
            this.dispatcher.dispatch(joiner, "help");
            return false;
        }

        if (joinerParty != null) {
            this.dispatcher.dispatch(joiner, "need-leave-party-error");
            return false;
        }

        Player target = Bukkit.getPlayerExact(args[1]);

        if (target == null) {
            this.dispatcher.dispatch(joiner, "player-not-found-error");
            return false;
        }

        if (this.manager.getParty(target.getUniqueId()) == null) {
            this.dispatcher.dispatch(joiner, "party-not-found-error");
            return false;
        }

        Party targetParty = this.manager.getParty(target.getUniqueId());

        if (targetParty.getMembers().contains(joiner.getUniqueId())) {
            this.dispatcher.dispatch(joiner, "already-in-this-party-error");
            return false;
        }

        if (targetParty.getInvite(joiner.getUniqueId(), target.getUniqueId()) == null) {
            this.dispatcher.dispatch(joiner, "not-invited-error");
            return false;
        }

        if (targetParty.getMembers().size() == targetParty.getMaxLimit()) {
            this.dispatcher.dispatch(joiner, "party-full-error");
            return false;
        }

        targetParty.join(joiner.getUniqueId(), target.getUniqueId());
        this.dispatcher.dispatch(joiner, "party-joiner-message");
        targetParty.getMembers().stream().filter(member -> member != joiner.getUniqueId()).forEach(member -> this.dispatcher.dispatch(Bukkit.getPlayer(member), "party-members-joining-message", player ->
                player.replace("%player%", joiner.getName())));
        return true;
    }
}
