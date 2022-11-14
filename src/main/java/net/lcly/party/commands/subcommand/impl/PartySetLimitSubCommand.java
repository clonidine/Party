package net.lcly.party.commands.subcommand.impl;

import net.lcly.party.PartyLY;
import net.lcly.party.commands.subcommand.SubCommand;
import net.lcly.party.dispatcher.MessageDispatcher;
import net.lcly.party.manager.PartyManager;
import net.lcly.party.model.Party;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class PartySetLimitSubCommand implements SubCommand {

    private final PartyManager manager;
    private final MessageDispatcher dispatcher;
    private final PartyLY plugin;

    public PartySetLimitSubCommand(PartyLY plugin) {
        this.plugin = plugin;
        this.manager = plugin.getPartyManager();
        this.dispatcher = plugin.getMessageDispatcher();
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("setlimit");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        if (!this.manager.hasParty(player.getUniqueId())) {
            this.dispatcher.dispatch(player, "no-party-error");
            return false;
        }

        Party party = this.manager.getParty(player.getUniqueId());

        if (args.length != 2) {
            this.dispatcher.dispatch(player, "help");
            return false;
        }

        if (!party.isLeader(player.getUniqueId())) {
            this.dispatcher.dispatch(player, "party-limit-error");
            return false;
        }

        try {
            int limit = Integer.parseInt(args[1]);

            if (limit >= party.getMaxLimit() || limit <= party.getMembers().size()) {
                this.dispatcher.dispatch(player, "party-limit-exceeded-error", max_limit ->
                        max_limit.replace("%max_limit%", plugin.getConfig().getString("party-max-limit")));
                return false;
            }

            party.setMaxLimit(limit);
            this.dispatcher.dispatch(player, "party-limit-message");
        }
        catch (NumberFormatException exception) {
            this.dispatcher.dispatch(player, "number-format-exception-error");
            return false;
        }

        return false;
    }
}
