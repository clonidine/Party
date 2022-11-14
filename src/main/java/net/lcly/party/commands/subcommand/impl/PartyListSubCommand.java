package net.lcly.party.commands.subcommand.impl;

import net.lcly.party.PartyLY;
import net.lcly.party.commands.subcommand.SubCommand;
import net.lcly.party.dispatcher.MessageDispatcher;
import net.lcly.party.manager.PartyManager;
import net.lcly.party.model.Party;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PartyListSubCommand implements SubCommand {
    private final PartyManager manager;
    private final MessageDispatcher dispatcher;

    public PartyListSubCommand(PartyLY plugin) {
        this.manager = plugin.getPartyManager();
        this.dispatcher = plugin.getMessageDispatcher();
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("members", "list");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        if (!this.manager.hasParty(player.getUniqueId())) {
            this.dispatcher.dispatch(player, "no-party-error");
            return false;
        }

        Party party = this.manager.getParty(player.getUniqueId());
        List<String> names = new ArrayList<>();
        party.getMembers().forEach(member -> names.add(Bukkit.getPlayer(member).getName()));
        this.dispatcher.dispatch(player, "party-list-members-message", name -> name.replace("%name%", Arrays.toString(names.toArray())));
        return true;
    }
}
