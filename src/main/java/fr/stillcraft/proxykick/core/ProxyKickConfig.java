package fr.stillcraft.proxykick.core;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

/**
 * Loads and migrates ProxyKick's config.yml and locale files. Built on YamlDocument/YamlStore
 * (a minimal wrapper around SnakeYAML with a safe Constructor - no dependency on the BungeeCord
 * proxy runtime), so this same class is shared verbatim by the BungeeCord and Velocity entry points.
 */
public final class ProxyKickConfig {
    // Version (don't forget to increment)
    public static final String VERSION = "1.3";

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

    private final File dataFolder;
    private final Consumer<String> warningLogger;

    public YamlDocument config;
    public YamlDocument locale;

    public ProxyKickConfig(File dataFolder, Consumer<String> warningLogger) {
        this.dataFolder = dataFolder;
        this.warningLogger = warningLogger;
    }

    // Checks/migrates all config files, then (re)loads config and locale into this instance.
    public void checkAndLoad() {
        checkConfig("config");
        checkConfig("locales/locale_fr");
        checkConfig("locales/locale_en");
        try {
            config = getConfig("config");
            locale = getConfig("locales/locale_" + resolveLocale());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String cfg(String key) { return config.getString(key); }
    public boolean cfgBool(String key) { return config.getBoolean(key); }
    public String msg(String key) { return locale.getString(key); }

    // Reads config's "locale" value and falls back to "en" if it doesn't match a supported locale file.
    public String resolveLocale() {
        String locale_string = config.getString("locale");
        if (!locale_string.equals("en") && !locale_string.equals("fr")) {
            warningLogger.accept("Unknown locale '" + locale_string + "' in config.yml, falling back to 'en'.");
            locale_string = "en";
        }
        return locale_string;
    }

    // Stores a config value with the correct type: real Boolean for "true"/"false", raw String otherwise.
    private static void setConfigValue(YamlDocument config, String key, String value) {
        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
            config.set(key, Boolean.parseBoolean(value));
        } else {
            config.set(key, value);
        }
    }

    public void checkConfig(String fileName) {
        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }
        File file = new File(dataFolder, fileName+".yml");
        try {
            boolean save_config = false;
            if (!file.exists()) {
                // Initialize configuration
                file.getParentFile().mkdirs();
                file.createNewFile();
                YamlDocument config = getConfig(fileName);

                // Writing default config values
                if (fileName.equals("locales/locale_en") || fileName.equals("locales/locale_fr")) {
                    for (String locale_key : locale_keys) {
                        config.set(locale_key, defaultConfig(locale_key, fileName));
                    }
                }
                if (fileName.equals("config")) {
                    for (String config_key : config_keys) {
                        String temp_str = defaultConfig(config_key, fileName);
                        setConfigValue(config, config_key, temp_str);
                    }
                }
                // Save configuration
                saveConfig(config, fileName);
            } else { // Check config data (add keys if does not exists)
                YamlDocument config = getConfig(fileName);
                if (fileName.equals("locales/locale_en") || fileName.equals("locales/locale_fr")) {
                    for (int i=0; i<locale_keys.length; i++){                                   // browse locale keys ...
                        if (!locale_keys[i].equals("global.version")) {                         // if not global.version key
                            if (config.getString(locale_keys[i]).isEmpty()) {                   // if key is empty
                                if (!config.getString("global.version").equals(VERSION)) { // if versions are not the same
                                    if (config.getString("global.version").isEmpty()) {    // convert from v1.0
                                        if (!config.getString(locale_keys_v1_0[i]).isEmpty()) { // convert only non-empty keys
                                            config.set(locale_keys[i], config.getString(locale_keys_v1_0[i]));
                                            config.set(locale_keys_v1_0[i], null);
                                        } else {                                                // Add default key to locale file
                                            config.set(locale_keys[i], defaultConfig(locale_keys[i], fileName));
                                        }
                                    }
                                } else {                                                        // if versions are the same add default
                                    config.set(locale_keys[i], defaultConfig(locale_keys[i], fileName));
                                }
                                save_config = true;
                            } else if (!config.getString("global.version").equals(VERSION)) {
                                if (config.getString("global.version").isEmpty()) {
                                    // Convert from version 1.0
                                    if (config.getString(locale_keys_v1_0[i]).isEmpty()) {
                                        config.set(locale_keys[i], defaultConfig(locale_keys[i], fileName));
                                    }
                                }
                            }
                        } else {
                            if(!config.getString(locale_keys[i]).equals(VERSION)){ // modify version if does not coincides with plugin version.
                                config.set(locale_keys[i], defaultConfig(locale_keys[i], fileName));

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
                            String temp_str = defaultConfig(config_key, fileName);
                            setConfigValue(config, config_key, temp_str);
                            save_config = true;
                        }
                    }
                }
                // Save configuration
                if (save_config) saveConfig(config, fileName);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String defaultConfig(String key, String locale){
        // config file default values :
        if(key.equals("version"))                 return VERSION;
        if(key.equals("locale"))                  return "en";
        if(key.equals("broadcast"))               return "true";

        // locale files default values :
        if(locale.equals("locales/locale_en")) {
            if(key.equals("global.reason"))       return "&c%reason%";
            if(key.equals("global.separator"))    return "&7: ";
            if(key.equals("global.punctuation"))  return "&7.";
            if(key.equals("global.empty"))        return "&cError: nobody is online.";
            if(key.equals("global.usage"))        return "&fUsage: ";
            if(key.equals("global.description"))  return "&fDescription: ";
            if(key.equals("global.prefix"))       return "&f[ProxyKick]";
            if(key.equals("global.version"))      return VERSION;

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
        } else if(locale.equals("locales/locale_fr")) {
            if(key.equals("global.reason"))       return "&c%reason%";
            if(key.equals("global.separator"))    return " &7: ";
            if(key.equals("global.punctuation"))  return "&7.";
            if(key.equals("global.empty"))        return "&cErreur : personne n'est connecté.";
            if(key.equals("global.usage"))        return "&fSyntaxe : ";
            if(key.equals("global.description"))  return "&fDescription : ";
            if(key.equals("global.prefix"))       return "&f[ProxyKick]";
            if(key.equals("global.version"))      return VERSION;

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

    public YamlDocument getConfig(String fileName) throws IOException {
        return YamlStore.load(new File(dataFolder, fileName+".yml"));
    }

    public void saveConfig(YamlDocument config, String fileName) throws IOException {
        YamlStore.save(config, new File(dataFolder, fileName+".yml"));
    }
}
