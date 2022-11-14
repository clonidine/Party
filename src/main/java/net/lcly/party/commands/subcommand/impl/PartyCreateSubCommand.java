package net.lcly.party.commands.subcommand.impl;

import net.lcly.party.PartyLY;
import net.lcly.party.commands.subcommand.SubCommand;
import net.lcly.party.dispatcher.MessageDispatcher;
import net.lcly.party.manager.PartyManager;
import net.lcly.party.model.Party;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class PartyCreateSubCommand implements SubCommand {

    private final PartyManager manager;
    private final PartyLY plugin;
    private final MessageDispatcher dispatcher;

    public PartyCreateSubCommand(PartyLY plugin) {
        this.plugin = plugin;
        this.manager = plugin.getPartyManager();
        this.dispatcher = plugin.getMessageDispatcher();
    }


    @Override
    public List<String> getAliases() {
        return Arrays.asList("create", "c");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        Party party = this.manager.getParty(player.getUniqueId());

        if (party != null) {
            this.dispatcher.dispatch(player, "already-created-party-error");
            return false;
        }

        party = new Party(player.getUniqueId(), this.plugin);
        this.manager.updatePartyCache(player.getUniqueId(), party);
        this.dispatcher.dispatch(player, "created-party-message");
        return true;
    }
}
