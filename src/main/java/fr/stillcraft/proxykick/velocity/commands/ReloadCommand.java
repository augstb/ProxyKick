package fr.stillcraft.proxykick.velocity.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import fr.stillcraft.proxykick.core.ProxyKickConfig;
import fr.stillcraft.proxykick.velocity.VelocityMain;
import fr.stillcraft.proxykick.velocity.VelocityText;

public class ReloadCommand implements SimpleCommand {
    private final VelocityMain plugin;

    public ReloadCommand(VelocityMain plugin) { this.plugin = plugin; }

    @Override
    public void execute(Invocation invocation) {
        CommandSource sender = invocation.source();
        String[] args = invocation.arguments();
        ProxyKickConfig cfg = plugin.cfg;

        if (args.length > 0) {
            // Get each string from config and locale data
            String usage = cfg.msg("global.usage")+cfg.msg("reload.usage");
            String description = cfg.msg("global.description")+cfg.msg("reload.description");

            // Return help message
            if (args[0].equalsIgnoreCase("help")) {
                VelocityText.send(sender, usage);
                VelocityText.send(sender, description);
                return;
            }
        }

        // Reload config file
        cfg.checkAndLoad();

        String success = cfg.msg("global.prefix")+" "+cfg.msg("reload.success");
        VelocityText.send(sender, success);
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("proxykick.reload");
    }
}
