package fr.stillcraft.proxykick;

import fr.stillcraft.proxykick.commands.*;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;

public final class Main extends Plugin {
    public static Main instance;
    public static Configuration config;
    public static Configuration locale;

    // Version (don't forget to increment)
    public static final String version = "1.1";
    // Used config files keys
    private static final String[] locale_keys = {
            "kick.kicked","kick.confirm","kick.info","kick.offline","kick.bypass","kick.bypass_warn","kick.usage","kick.description",
            "kickall.kicked","kickall.info","kickall.offline","kickall.usage","kickall.description","kickall.confirm",
            "global.reason","global.separator","global.punctuation","global.empty","global.usage","global.description","global.prefix",
            "help.usage","help.description",
            "version.usage","version.description",
            "reload.success","reload.usage","reload.description",
            "global.version" // VERSION SHOULD BE LAST
    };
    private static final String[] config_keys  = {"version","locale","broadcast"};
    private static final String[] locale_keys_v1_0 = {
            "format.kicked","format.confirm","format.info","errors.offline","errors.bypass","errors.bypass_warn","","",
            "","","","","","",
            "format.reason","","format.punctuation","errors.empty","","","",
            "","",
            "","",
            "","","",
            ""
    };

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
            getProxy().getPluginManager().registerCommand(this, new help());
            getProxy().getPluginManager().registerCommand(this, new kick());
            getProxy().getPluginManager().registerCommand(this, new kickall());
            getProxy().getPluginManager().registerCommand(this, new proxykick());
            getProxy().getPluginManager().registerCommand(this, new reload());
            getProxy().getPluginManager().registerCommand(this, new version());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Main getInstance() { return instance; }

    public static void checkConfig(String fileName) {
        if(!Main.getInstance().getDataFolder().exists()){
            Main.getInstance().getDataFolder().mkdir();
        }
        File file = new File(Main.getInstance().getDataFolder(), fileName+".yml");
        try {
            boolean save_config = false;
            if (!file.exists()) {
                // Initialize configuration
                file.createNewFile();
                Configuration config = Main.getInstance().getConfig(fileName);

                // Writing default config values
                if (fileName.equals("locale_en") || fileName.equals("locale_fr")) {
                    for (String locale_key : locale_keys) {
                        config.set(locale_key, Main.getInstance().defaultConfig(locale_key, fileName));
                    }
                }
                if (fileName.equals("config")) {
                    for (String config_key : config_keys) {
                        String temp_str = Main.getInstance().defaultConfig(config_key, fileName);
                        if (Boolean.parseBoolean(temp_str)) config.set(config_key, Boolean.parseBoolean(temp_str));
                        else config.set(config_key, temp_str);
                    }
                }
                // Save configuration
                Main.getInstance().saveConfig(config, fileName);
            } else { // Check config data (add keys if does not exists)
                Configuration config = Main.getInstance().getConfig(fileName);
                if (fileName.equals("locale_en") || fileName.equals("locale_fr")) {
                    for (int i=0; i<locale_keys.length; i++){                                   // browse locale keys ...
                        if (!locale_keys[i].equals("global.version")) {                         // if not global.version key
                            if (config.getString(locale_keys[i]).isEmpty()) {                   // if key is empty
                                if (!config.getString("global.version").equals(version)) { // if versions are not the same
                                    if (config.getString("global.version").isEmpty()) {    // convert from v1.0
                                        if (!config.getString(locale_keys_v1_0[i]).isEmpty()) { // convert only non-empty keys
                                            config.set(locale_keys[i], config.getString(locale_keys_v1_0[i]));
                                            config.set(locale_keys_v1_0[i], null);
                                        } else {                                                // Add default key to locale file
                                            config.set(locale_keys[i], Main.getInstance().defaultConfig(locale_keys[i], fileName));
                                        }
                                    }
                                } else {                                                        // if versions are the same add default
                                    config.set(locale_keys[i], Main.getInstance().defaultConfig(locale_keys[i], fileName));
                                }
                                save_config = true;
                            } else if (!config.getString("global.version").equals(version)) {
                                if (config.getString("global.version").isEmpty()) {
                                    // Convert from version 1.0
                                    if (config.getString(locale_keys_v1_0[i]).isEmpty()) {
                                        config.set(locale_keys[i], Main.getInstance().defaultConfig(locale_keys[i], fileName));
                                    }
                                }
                            }
                        } else {
                            if(!config.getString(locale_keys[i]).equals(version)){ // modify version if does not coincides with plugin version.
                                config.set(locale_keys[i], Main.getInstance().defaultConfig(locale_keys[i], fileName));

                                // Throw old 1.0 config keys
                                config.set("format", null);
                                config.set("errors", null);

                                save_config = true;
                            }
                        }
                    }
                }
                if (fileName.equals("config")) {
                    for (String config_key : config_keys) {
                        if (config.getString(config_key).isEmpty()) {
                            // Handle Boolean types
                            String temp_str = Main.getInstance().defaultConfig(config_key, fileName);
                            if (Boolean.parseBoolean(temp_str)) config.set(config_key, Boolean.parseBoolean(temp_str));
                            else config.set(config_key, temp_str);
                            save_config = true;
                        }
                    }
                }
                // Save configuration
                if (save_config) Main.getInstance().saveConfig(config, fileName);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String defaultConfig(String key, String locale){
        // config file default values :
        if(key.equals("version"))                 return version;
        if(key.equals("locale"))                  return "en";
        if(key.equals("broadcast"))               return "true";

        // locale files default values :
        if(locale.equals("locale_en")) {
            if(key.equals("global.reason"))       return "&c%reason%";
            if(key.equals("global.separator"))    return "&7: ";
            if(key.equals("global.punctuation"))  return "&7.";
            if(key.equals("global.empty"))        return "&cError: nobody is online.";
            if(key.equals("global.usage"))        return "&fUsage: ";
            if(key.equals("global.description"))  return "&fDescription: ";
            if(key.equals("global.prefix"))       return "&f[ProxyKick]";
            if(key.equals("global.version"))      return version;

            if(key.equals("kick.kicked"))         return "&7You have been kicked by &f%sender%";
            if(key.equals("kick.confirm"))        return "&7You kicked &f%player%";
            if(key.equals("kick.info"))           return "&f%player% &7has been kicked by &f%sender%";
            if(key.equals("kick.offline"))        return "&cError: &4%player%&c is not online.";
            if(key.equals("kick.bypass"))         return "&7You can't kick &f%player%&7.";
            if(key.equals("kick.bypass_warn"))    return "&f%sender% &7tried to kick you.";
            if(key.equals("kick.usage"))          return "&3/kick &b[playername] (reason)";
            if(key.equals("kick.description"))    return "&7Kick player with a message.";

            if(key.equals("kickall.kicked"))      return "&7Everyone have been kicked by &f%sender%";
            if(key.equals("kickall.confirm"))     return "&7You kicked everyone";
            if(key.equals("kickall.info"))        return "&7Everyone have been kicked by &f%sender%";
            if(key.equals("kickall.offline"))     return "&cError: nobody is kickable.";
            if(key.equals("kickall.usage"))       return "&3/kickall &b(reason)";
            if(key.equals("kickall.description")) return "&7Kick everyone with a message.";

            if(key.equals("help.usage"))          return "&3/proxykick:help";
            if(key.equals("help.description"))    return "&7Show the help page.";

            if(key.equals("version.usage"))          return "&3/proxykick:version";
            if(key.equals("version.description"))    return "&7Show plugin version.";

            if(key.equals("reload.success"))      return "&7Config and locale files reloaded.";
            if(key.equals("reload.usage"))        return "&3/proxykick:reload";
            if(key.equals("reload.description"))  return "&7Reload the configuration files.";
        } else if(locale.equals("locale_fr")) {
            if(key.equals("global.reason"))       return "&c%reason%";
            if(key.equals("global.separator"))    return " &7: ";
            if(key.equals("global.punctuation"))  return "&7.";
            if(key.equals("global.empty"))        return "&cErreur : personne n'est connecté.";
            if(key.equals("global.usage"))        return "&fSyntaxe : ";
            if(key.equals("global.description"))  return "&fDescription : ";
            if(key.equals("global.prefix"))       return "&f[ProxyKick]";
            if(key.equals("global.version"))      return version;

            if(key.equals("kick.kicked"))         return "&7Vous avez été ejecté par &f%sender%";
            if(key.equals("kick.confirm"))        return "&7Vous avez éjecté &f%player%";
            if(key.equals("kick.info"))           return "&f%player% &7a été éjecté par &f%sender%";
            if(key.equals("kick.offline"))        return "&cErreur : &4%player%&c n'est pas connecté.";
            if(key.equals("kick.bypass"))         return "&7Vous ne pouvez pas éjecter &f%player%&7.";
            if(key.equals("kick.bypass_warn"))    return "&f%sender% &7a essayé de vous éjecter.";
            if(key.equals("kick.usage"))          return "&3/kick &b[joueur] (raison)";
            if(key.equals("kick.description"))    return "&7Ejecter un joueur avec un message.";

            if(key.equals("kickall.kicked"))      return "&7Tout le monde a été éjecté par &f%sender%";
            if(key.equals("kickall.confirm"))     return "&7Vous avez éjecté tout le monde";
            if(key.equals("kickall.info"))        return "&7Tout le monde a été éjecté par &f%sender%";
            if(key.equals("kickall.offline"))     return "&cError: Personne n'est éjectable.";
            if(key.equals("kickall.usage"))       return "&3/kickall &b(reason)";
            if(key.equals("kickall.description")) return "&7Ejecter tout le monde avec un message.";

            if(key.equals("help.usage"))          return "&3/proxykick:help";
            if(key.equals("help.description"))    return "&7Afficher la page d'aide.";

            if(key.equals("version.usage"))       return "&3/proxykick:version";
            if(key.equals("version.description")) return "&7Afficher la version du plugin.";

            if(key.equals("reload.success"))      return "&7Fichiers de config et de langue rechargés.";
            if(key.equals("reload.usage"))        return "&3/proxykick:reload";
            if(key.equals("reload.description"))  return "&7Recharger les fichiers de configuration.";
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
