package net.lcly.party.util.config;

import lombok.Getter;
import lombok.Setter;
import net.lcly.party.PartyLY;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class Config {


    private final Plugin plugin = PartyLY.getPlugin(PartyLY.class);

    @Getter
    @Setter
    private String name;

    @Getter
    private File file;

    @Getter
    private YamlConfiguration config;

    public Config(String nome) {
        this.setName(nome);
        this.reloadConfig();
    }

    public void saveConfig() {
        try {
            this.getConfig().save(getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveDefault() {
        this.getConfig().options().copyDefaults(true);
    }

    public void saveDefaultConfig() {
        if (!existsConfig()) {
            this.plugin.saveResource(getName(), false);
            this.reloadConfig();
        }
    }

    public void reloadConfig() {
        this.file = new File(plugin.getDataFolder(), getName());
        this.config = YamlConfiguration.loadConfiguration(getFile());
    }

    public void deleteConfig() {
        this.getFile().delete();
    }

    public boolean existsConfig() {
        return this.getFile().exists();
    }

}
