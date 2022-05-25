package fr.stillcraft.proxykick;

import fr.stillcraft.proxykick.commands.kickall;
import fr.stillcraft.proxykick.commands.proxykick;
import fr.stillcraft.proxykick.commands.reload;
import net.md_5.bungee.api.plugin.Plugin;
import fr.stillcraft.proxykick.commands.kick;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;
import java.util.logging.Level;

public final class ProxyKick extends Plugin {
    public static ProxyKick instance;
    public static Configuration config;
    public static Configuration locale;

    // Used config files keys
    private static final String[] locale_keys = {"kick.kicked","kickall.kicked","kick.confirm","global.reason",
            "global.separator","global.punctuation","kick.info","kickall.info","kick.offline","global.empty",
            "kick.bypass","kick.bypass_warn","kick.usage","kick.description","kickall.usage",
            "kickall.description"};
    private static final String[] config_keys  = {"locale", "broadcast"};

    @Override
    public void onEnable() {
        instance = this;

        checkConfig("config");
        checkConfig("locale_fr");
        checkConfig("locale_en");
        try {
            // Load config file
            config = getInstance().getConfig("config");
            String locale_string = config.getString("locale");
            locale = getInstance().getConfig("locale_" + locale_string);

            // Register new commands
            getProxy().getPluginManager().registerCommand(this, new proxykick());
            getProxy().getPluginManager().registerCommand(this, new kick());
            getProxy().getPluginManager().registerCommand(this, new kickall());
            getProxy().getPluginManager().registerCommand(this, new reload());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static ProxyKick getInstance() { return instance; }

    public static void checkConfig(String fileName) {
        if(!ProxyKick.getInstance().getDataFolder().exists()){
            ProxyKick.getInstance().getDataFolder().mkdir();
        }
        File file = new File(ProxyKick.getInstance().getDataFolder(), fileName+".yml");
        try {
            if (!file.exists()) {
                file.createNewFile();
                // Initialize configuration
                Configuration config = ProxyKick.getInstance().getConfig(fileName);

                // Writing default config values
                if (fileName.equals("locale_en") || fileName.equals("locale_fr")) {
                    for (String locale_key : locale_keys) {
                        config.set(locale_key, ProxyKick.getInstance().defaultConfig(locale_key, fileName));
                    }
                }
                if (fileName.equals("config")) {
                    for (String config_key : config_keys) {
                        String temp_str = ProxyKick.getInstance().defaultConfig(config_key, fileName);
                        if (Boolean.parseBoolean(temp_str)) config.set(config_key, Boolean.parseBoolean(temp_str));
                        else config.set(config_key, temp_str);
                    }
                }
                // Save configuration
                ProxyKick.getInstance().saveConfig(config, fileName);
            } else { // Check config data (add keys if does not exists)
                boolean save_config = false;
                Configuration config = ProxyKick.getInstance().getConfig(fileName);
                if (fileName.equals("locale_en") || fileName.equals("locale_fr")) {
                    for (String locale_key : locale_keys) {
                        if (config.getString(locale_key).isEmpty()) {
                            config.set(locale_key, ProxyKick.getInstance().defaultConfig(locale_key, fileName));
                            save_config = true;
                        }
                    }
                }
                if (fileName.equals("config")) {
                    for (String config_key : config_keys) {
                        if (config.getString(config_key).isEmpty()) {
                            // Handle Boolean types
                            String temp_str = ProxyKick.getInstance().defaultConfig(config_key, fileName);
                            if (Boolean.parseBoolean(temp_str)) config.set(config_key, Boolean.parseBoolean(temp_str));
                            else config.set(config_key, temp_str);
                            save_config = true;
                        }
                    }
                }
                // Save configuration
                if (save_config) ProxyKick.getInstance().saveConfig(config, fileName);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String defaultConfig(String key, String locale){
        // config file default values :
        if(key.equals("locale")) return "en";
        if(key.equals("broadcast")) return "true";

        // locale files default values :
        if(locale.equals("locale_en")) {
            if(key.equals("global.reason"))       return "&c%reason%";
            if(key.equals("global.separator"))    return "&7:";
            if(key.equals("global.punctuation"))  return "&7.";
            if(key.equals("global.empty"))        return "&cError: nobody is online.";

            if(key.equals("kick.kicked"))         return "&7You have been kicked by &f%sender%";
            if(key.equals("kick.confirm"))        return "&7You kicked &f%player%";
            if(key.equals("kick.info"))           return "&f%player% &7has been kicked by &f%sender%";
            if(key.equals("kick.offline"))        return "&cError: &4%player%&c is not online.";
            if(key.equals("kick.bypass"))         return "&7You can't kick &f%player%&7.";
            if(key.equals("kick.bypass_warn"))    return "&f%sender% &7tried to kick you.";
            if(key.equals("kick.usage"))          return "&7Usage: &3/kick &b[player name] (reason)";
            if(key.equals("kick.description"))    return "&7Description: Kick player with custom message.";

            if(key.equals("kickall.kicked"))      return "&7Everyone have been kicked by &f%sender%";
            if(key.equals("kickall.confirm"))     return "&7You kicked everyone";
            if(key.equals("kickall.info"))        return "&7Everyone have been kicked by &f%sender%";
            if(key.equals("kickall.usage"))       return "&7Usage: &3/kickall (reason)";
            if(key.equals("kickall.description")) return "&7Description: Kick everyone with custom message.";
        } else if(locale.equals("locale_fr")) {
            if(key.equals("global.reason"))       return "&c%reason%";
            if(key.equals("global.separator"))    return " &7:";
            if(key.equals("global.punctuation"))  return "&7.";
            if(key.equals("global.empty"))        return "&cErreur : personne n'est connecté.";

            if(key.equals("kick.kicked"))         return "&7Vous avez été ejecté par &f%sender%";
            if(key.equals("kick.confirm"))        return "&7Vous avez éjecté &f%player%";
            if(key.equals("kick.info"))           return "&f%player% &7a été éjecté par &f%sender%";
            if(key.equals("kick.offline"))        return "&cErreur : &4%player%&c n'est pas connecté.";
            if(key.equals("kick.bypass"))         return "&7Vous ne pouvez pas éjecter &f%player%&7.";
            if(key.equals("kick.bypass_warn"))    return "&f%sender% &7a essayé de vous éjecter.";
            if(key.equals("kick.usage"))          return "&7Syntaxe : &3/kick &b[nom du joueur] (raison)";
            if(key.equals("kick.description"))    return "&7Description : Ejecter un joueur avec un message personnalisé.";

            if(key.equals("kickall.kicked"))      return "&7Tout le monde a été éjecté par &f%sender%";
            if(key.equals("kickall.confirm"))     return "&7Vous avez éjecté tout le monde";
            if(key.equals("kickall.info"))        return "&7Tout le monde a été éjecté par &f%sender%";
            if(key.equals("kickall.usage"))       return "&7Syntaxe : &3/kickall (reason)";
            if(key.equals("kickall.description")) return "&7Description : Ejecter tout le monde avec un message personnalisé.";
        }
        return "";
    }

    public Configuration getConfig(String fileName) throws IOException {
        return ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), fileName+".yml"));
    }

    public void saveConfig(Configuration config, String fileName) throws IOException {
        ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, new File(getDataFolder(), fileName+".yml"));
    }
}
