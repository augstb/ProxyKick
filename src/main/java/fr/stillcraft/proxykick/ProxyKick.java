package fr.stillcraft.proxykick;

import net.md_5.bungee.api.plugin.Plugin;
import fr.stillcraft.proxykick.commands.kick;

import java.util.logging.Level;

public final class ProxyKick extends Plugin {
    public static ProxyKick instance;

    @Override
    public void onEnable() {
        instance = this;
        this.getLogger().log(Level.INFO,"Successfully started ProxyKick.");
        getProxy().getPluginManager().registerCommand(this, new kick());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static ProxyKick getInstance() { return instance; }

}
