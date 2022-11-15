package net.lcly.party.commands.subcommand.impl;

import net.lcly.party.PartyLY;
import net.lcly.party.commands.subcommand.SubCommand;
import net.lcly.party.dispatcher.MessageDispatcher;
import net.lcly.party.manager.PartyManager;
import net.lcly.party.model.Party;
import net.lcly.party.model.PartyInvite;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class PartyInviteSubCommand implements SubCommand {

    private final PartyManager partyManager;
    private final MessageDispatcher dispatcher;

    public PartyInviteSubCommand(PartyLY plugin) {
        this.partyManager = plugin.getPartyManager();
        this.dispatcher = plugin.getMessageDispatcher();
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("invite", "inv");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player inviter = (Player) sender;
        Party party = this.partyManager.getParty(inviter.getUniqueId());

        if (!this.partyManager.hasParty(inviter.getUniqueId())) {
            this.dispatcher.dispatch(inviter, "no-party-error");
            return false;
        }

        if (!party.isLeader(inviter.getUniqueId())) {
            this.dispatcher.dispatch(inviter, "not-leader-error");
            return false;
        }

        if (args.length != 2) {
            this.dispatcher.dispatch(inviter, "help");
            return false;
        }

        Player invited = Bukkit.getPlayerExact(args[1]);

        if (invited == null) {
            this.dispatcher.dispatch(inviter, "player-not-found-error");
            return false;
        }

        if (inviter.getUniqueId().equals(invited.getUniqueId())) {
            this.dispatcher.dispatch(inviter, "cannot-invite-yourself-error");
            return false;
        }

        if (this.partyManager.hasParty(invited.getUniqueId())) {
            this.dispatcher.dispatch(inviter, "player-already-in-party-error");
            return false;
        }

        PartyInvite invite = party.getInvite(invited.getUniqueId(), inviter.getUniqueId());

        if (invite != null && this.partyManager.getPartyInvitesInCooldown().isInCooldown(invite)) {
            this.dispatcher.dispatch(inviter, "invite-cooldown-message-error", time ->
                    time.replace("%time%", this.partyManager.getPartyInvitesInCooldown().getRemainingTimeFormatted(invite)));
            return false;
        }

        TextComponent textComponent = new TextComponent("(Join)");
        textComponent.setColor(ChatColor.GREEN);
        textComponent.setBold(true);
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party join " + inviter.getName()));

        this.dispatcher.dispatch(invited, "invited-message", player -> player.replace("%player%", inviter.getName()));
        invited.playSound(invited.getLocation(), Sound.NOTE_PLING, 100.0f, 1.0f);
        inviter.playSound(inviter.getLocation(), Sound.NOTE_STICKS, 100.0f, 1.0f);
        invited.spigot().sendMessage(textComponent);

        partyManager.invite(party, new PartyInvite(inviter.getUniqueId(), invited.getUniqueId()));
        this.dispatcher.dispatch(inviter, "inviter-message", player -> player.replace("%player%", invited.getName()));
        return true;
    }
}
