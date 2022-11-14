package net.lcly.party.commands;

import com.github.mattnicee7.mattlib.cooldown.CooldownMap;
import com.google.common.collect.Lists;
import net.lcly.party.PartyLY;
import net.lcly.party.commands.subcommand.SubCommand;
import net.lcly.party.commands.subcommand.impl.*;
import net.lcly.party.dispatcher.MessageDispatcher;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class PartyCommand implements CommandExecutor {


    private final List<SubCommand> subCommands = Lists.newArrayList();
    private final CooldownMap<CommandSender> playersInCooldown = CooldownMap.of(CommandSender.class);
    private final MessageDispatcher dispatcher;
    private final PartyLY plugin;

    public PartyCommand(PartyLY plugin) {
        this.plugin = plugin;
        this.dispatcher = plugin.getMessageDispatcher();
        plugin.getCommand("party").setExecutor(this);

        this.subCommands.add(new PartyCreateSubCommand(plugin));
        this.subCommands.add(new PartyDisbandSubCommand(plugin));
        this.subCommands.add(new PartyInviteSubCommand(plugin));
        this.subCommands.add(new PartyJoinSubCommand(plugin));
        this.subCommands.add(new PartyListSubCommand(plugin));
        this.subCommands.add(new PartyLeaveSubCommand(plugin));
        this.subCommands.add(new PartyKickSubCommand(plugin));
        this.subCommands.add(new PartySetLeaderSubCommand(plugin));
        this.subCommands.add(new PartySetLimitSubCommand(plugin));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can execute this command");
            return false;
        }

        if (this.playersInCooldown.isInCooldown(sender)) {
            this.dispatcher.dispatch(sender, "player-cooldown-message-error");
            return false;
        }

        if (args.length > 0) {
            for (SubCommand subCommand : subCommands) {
                if (subCommand.getAliases().contains(args[0].toLowerCase())) {
                    if (plugin.getConfig().getBoolean("players-cooldown-mode")) {
                        this.playersInCooldown.put(sender, Long.parseLong(plugin.getConfig().getString("players-command-execute-time")));
                    }
                    return subCommand.execute(sender, args);
                }
            }
        }
        dispatcher.dispatch(sender, "help");
        return false;
    }
}
