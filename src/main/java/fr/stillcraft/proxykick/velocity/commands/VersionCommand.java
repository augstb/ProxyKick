package fr.stillcraft.proxykick.velocity.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import fr.stillcraft.proxykick.core.ProxyKickConfig;
import fr.stillcraft.proxykick.velocity.VelocityMain;
import fr.stillcraft.proxykick.velocity.VelocityText;

public class VersionCommand implements SimpleCommand {
    private final VelocityMain plugin;

    public VersionCommand(VelocityMain plugin) { this.plugin = plugin; }

    @Override
    public void execute(Invocation invocation) {
        CommandSource sender = invocation.source();
        String globalPrefix = plugin.cfg.msg("global.prefix");

        VelocityText.send(sender, "&f--- " + globalPrefix + "&f ---");
        VelocityText.send(sender, "&fProxyKick: &bv" + ProxyKickConfig.VERSION);
        VelocityText.send(sender, "&fSource: &b&ogithub.com/augstb/ProxyKick");
        VelocityText.send(sender, "&fDev: &bAugustin Blanchet");
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("proxykick.kick");
    }
}
