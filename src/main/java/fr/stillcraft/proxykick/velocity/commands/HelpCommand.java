package fr.stillcraft.proxykick.velocity.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import fr.stillcraft.proxykick.core.ProxyKickConfig;
import fr.stillcraft.proxykick.velocity.VelocityMain;
import fr.stillcraft.proxykick.velocity.VelocityText;

public class HelpCommand implements SimpleCommand {
    private final VelocityMain plugin;

    public HelpCommand(VelocityMain plugin) { this.plugin = plugin; }

    @Override
    public void execute(Invocation invocation) {
        CommandSource sender = invocation.source();
        ProxyKickConfig cfg = plugin.cfg;

        boolean senderIsPlayer = (sender instanceof Player);
        boolean hasKickallPerm = (!senderIsPlayer || sender.hasPermission("proxykick.kickall"));
        boolean hasReloadPerm = (!senderIsPlayer || sender.hasPermission("proxykick.reload"));

        // Get each string from config and locale data
        String globalPrefix = cfg.msg("global.prefix");
        String helpUsage = cfg.msg("help.usage");
        String helpDescription = cfg.msg("help.description");
        String kickUsage = cfg.msg("kick.usage");
        String kickDescription = cfg.msg("kick.description");
        String kickallUsage = cfg.msg("kickall.usage");
        String kickallDescription = cfg.msg("kickall.description");
        String reloadUsage = cfg.msg("reload.usage");
        String reloadDescription = cfg.msg("reload.description");
        String versionUsage = cfg.msg("version.usage");
        String versionDescription = cfg.msg("version.description");

        VelocityText.send(sender, "&f--- " + globalPrefix + "&f ---");
        VelocityText.send(sender, kickUsage + "&f - " + kickDescription);
        if (hasKickallPerm) VelocityText.send(sender, kickallUsage + "&f - " + kickallDescription);
        VelocityText.send(sender, helpUsage + "&f - " + helpDescription);
        if (hasReloadPerm) VelocityText.send(sender, reloadUsage + "&f - " + reloadDescription);
        VelocityText.send(sender, versionUsage + "&f - " + versionDescription);
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("proxykick.kick");
    }
}
