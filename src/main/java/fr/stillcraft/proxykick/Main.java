package fr.stillcraft.proxykick;

import fr.stillcraft.proxykick.commands.*;
import fr.stillcraft.proxykick.core.ProxyKickConfig;
import net.md_5.bungee.api.plugin.Plugin;

public final class Main extends Plugin {
    public static Main instance;
    public static ProxyKickConfig cfg;

    @Override
    public void onEnable() {
        instance = this;
        cfg = new ProxyKickConfig(getDataFolder(), getLogger()::warning);
        cfg.checkAndLoad();

        // Register new commands
        getProxy().getPluginManager().registerCommand(this, new help());
        getProxy().getPluginManager().registerCommand(this, new kick());
        getProxy().getPluginManager().registerCommand(this, new kickall());
        getProxy().getPluginManager().registerCommand(this, new proxykick());
        getProxy().getPluginManager().registerCommand(this, new reload());
        getProxy().getPluginManager().registerCommand(this, new version());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Main getInstance() { return instance; }
}
