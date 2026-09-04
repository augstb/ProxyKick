package fr.stillcraft.proxykick.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import fr.stillcraft.proxykick.core.ProxyKickConfig;
import fr.stillcraft.proxykick.velocity.commands.HelpCommand;
import fr.stillcraft.proxykick.velocity.commands.KickAllCommand;
import fr.stillcraft.proxykick.velocity.commands.KickCommand;
import fr.stillcraft.proxykick.velocity.commands.ProxyKickCommand;
import fr.stillcraft.proxykick.velocity.commands.ReloadCommand;
import fr.stillcraft.proxykick.velocity.commands.VersionCommand;
import org.slf4j.Logger;

import java.nio.file.Path;

@Plugin(id = "proxykick", name = "ProxyKick", version = ProxyKickConfig.VERSION, authors = "Augustin Blanchet",
        description = "ProxyKick is a proxy plugin which allows Minecraft server moderators to kick players from the entire network.")
public final class VelocityMain {
    private static VelocityMain instance;

    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;

    public ProxyKickConfig cfg;

    @Inject
    public VelocityMain(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        instance = this;
        cfg = new ProxyKickConfig(dataDirectory.toFile(), logger::warn);
        cfg.checkAndLoad();

        // Register commands (same names/aliases/permissions as the BungeeCord side)
        CommandManager commandManager = server.getCommandManager();
        commandManager.register(commandManager.metaBuilder("proxykick:help").plugin(this).build(), new HelpCommand(this));
        commandManager.register(commandManager.metaBuilder("proxykick:kick").aliases("kick").plugin(this).build(), new KickCommand(this));
        commandManager.register(commandManager.metaBuilder("proxykick:kickall").aliases("kickall").plugin(this).build(), new KickAllCommand(this));
        commandManager.register(commandManager.metaBuilder("proxykick:reload").plugin(this).build(), new ReloadCommand(this));
        commandManager.register(commandManager.metaBuilder("proxykick:version").aliases("proxykick:info").plugin(this).build(), new VersionCommand(this));
        commandManager.register(commandManager.metaBuilder("proxykick").aliases("pk").plugin(this).build(), new ProxyKickCommand(this));

        logger.info("Enabled plugin ProxyKick version " + ProxyKickConfig.VERSION + " by Augustin Blanchet");
    }

    public static VelocityMain getInstance() { return instance; }

    public ProxyServer getServer() { return server; }

    public Logger getLogger() { return logger; }
}
