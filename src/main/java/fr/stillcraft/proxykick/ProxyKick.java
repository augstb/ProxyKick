package fr.stillcraft.proxykick;

import fr.stillcraft.proxykick.commands.reload;
import net.md_5.bungee.api.plugin.Plugin;
import fr.stillcraft.proxykick.commands.kick;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;
import java.lang.reflect.Proxy;
import java.util.logging.Level;

public final class ProxyKick extends Plugin {
    public static ProxyKick instance;
    public static Configuration config;
    public static Configuration locale;

    @Override
    public void onEnable() {
        instance = this;

        createFile("config");
        createFile("locale_fr");
        createFile("locale_en");

        try {
            // Load config file
            config = getInstance().getConfig("config");
            String locale_string = config.getString("locale");
            locale = getInstance().getConfig("locale_" + locale_string);

            this.getLogger().log(Level.INFO,"Successfully started ProxyKick.");
            getProxy().getPluginManager().registerCommand(this, new kick());
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

    public static void createFile(String fileName){
        if(!ProxyKick.getInstance().getDataFolder().exists()){
            ProxyKick.getInstance().getDataFolder().mkdir();
        }
        File file = new File(ProxyKick.getInstance().getDataFolder(), fileName+".yml");

        if (!file.exists()){
            try{
                // Create new config file
                file.createNewFile();
                // Initialize configuration
                Configuration config = ProxyKick.getInstance().getConfig(fileName);

                // Writing default config values
                if(fileName.equals("locale_en")){
                    config.set("kicked_string", "&7You have been kicked by &f%sender%");
                    config.set("sender_return", "&7You kicked &f%player%");
                    config.set("reason_string", "&c%reason%");
                    config.set("reason_separator", "&7:");
                    config.set("punctuation", "&7.");
                    config.set("console_kicked_string", "&f%player% &7has been kicked by &f%sender%");
                    config.set("not_found", "&cError: &4%player%&c is not online.");
                    config.set("nobody_online", "&cError: nobody is online.");
                    config.set("help", "&7Usage: &3/kick &b[player name] (reason)");
                    config.set("description", "&7Description: Kick player with custom message.");
                    config.set("bypass_message", "&7You can't kick &f%player%&7.");
                    config.set("bypass_warn", "&f%sender% &7tried to kick you.");
                }
                if(fileName.equals("locale_fr")){
                    config.set("kicked_string", "&7Vous avez été ejecté par &f%sender%");
                    config.set("sender_return", "&7Vous avez éjecté &f%player%");
                    config.set("reason_string", "&c%reason%");
                    config.set("reason_separator", " &7:");
                    config.set("punctuation", "&7.");
                    config.set("console_kicked_string", "&f%player% &7a été éjecté par &f%sender%");
                    config.set("not_found", "&cErreur : &4%player%&c n'est pas connecté.");
                    config.set("nobody_online", "&cErreur : personne n'est connecté.");
                    config.set("help", "&7Syntaxe : &3/kick &b[player name] (raison)");
                    config.set("description", "&7Description : Ejecter un joueur avec un message personnalisé.");
                    config.set("bypass_message", "&7Vous ne pouvez pas éjecter &f%player%&7.");
                    config.set("bypass_warn", "&f%sender% &7a essayé de vous éjecter.");
                }
                if(fileName.equals("config")){
                    config.set("locale", "en");
                    config.set("broadcast", true);
                }
                // Save configuration
                ProxyKick.getInstance().saveConfig(config, fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Configuration getConfig(String fileName) throws IOException {
        return ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), fileName+".yml"));
    }

    public void saveConfig(Configuration config, String fileName) throws IOException {
        ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, new File(getDataFolder(), fileName+".yml"));
    }
}
