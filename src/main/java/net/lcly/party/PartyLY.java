package net.lcly.party;

import lombok.Getter;
import net.lcly.party.commands.PartyCommand;
import net.lcly.party.dispatcher.MessageDispatcher;
import net.lcly.party.listener.PlayerLeaveListener;
import net.lcly.party.manager.PartyManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class PartyLY extends JavaPlugin {

    @Getter private PartyManager partyManager;
    @Getter private MessageDispatcher messageDispatcher;
    @Override
    public void onEnable() {
        loadMessageDispatcher();
        registerManager();
        registerListener();
        registerCommand();
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "PartyLY enabled");
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "PartyLY disabled");
    }

    public void registerCommand() {
        new PartyCommand(this);
    }

    public void registerManager() {
        partyManager = new PartyManager(this);
    }

    public void registerListener() {
        new PlayerLeaveListener(this);
    }

    private void loadMessageDispatcher() {
        this.messageDispatcher = new MessageDispatcher().load();
    }
}