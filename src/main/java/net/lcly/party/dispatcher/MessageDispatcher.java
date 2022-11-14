package net.lcly.party.dispatcher;

import lombok.Getter;
import lombok.val;
import net.lcly.party.util.chat.ChatUtils;
import net.lcly.party.util.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class MessageDispatcher {

    @Getter private Config messagesConfig;
    private final Map<String, String[]> messages = new HashMap<>();

    public MessageDispatcher load() {
        this.messagesConfig = new Config("messages.yml");
        this.messagesConfig.saveDefaultConfig();
        this.messagesConfig.reloadConfig();

        this.loadMessages();
        return this;
    }

    private void loadMessages() {
        this.messages.clear();

        for (String key : this.messagesConfig.getConfig().getKeys(false)) {
            if (this.messagesConfig.getConfig().isString(key))
                this.messages.put(key, new String[]{ChatUtils.colorize(this.messagesConfig.getConfig().getString(key))});
            else
                this.messages.put(key, this.messagesConfig.getConfig().getStringList(key).stream().map(ChatUtils::colorize).toArray(String[]::new));
        }

    }

    public void dispatch(CommandSender commandSender, String path) {
        this.dispatch(commandSender, path, s -> s);
    }

    public void dispatch(CommandSender commandSender, String path, Function<String, String> formats) {
        val messages = this.messages.get(path);
        for (String message : messages) {
            commandSender.sendMessage(formats.apply(message));
        }
    }

    public void globalDispatch(String path) {
        this.globalDispatch(path, s -> s);
    }

    public void globalDispatch(String path, Function<String, String> formats) {
        val messages = this.messages.get(path);
        Bukkit.getOnlinePlayers().forEach(player -> {
            for (String message : messages)
                player.sendMessage(formats.apply(message));
        });
    }
}