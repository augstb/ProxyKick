package fr.stillcraft.proxykick.velocity.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import fr.stillcraft.proxykick.core.MessageFormatter;
import fr.stillcraft.proxykick.core.ProxyKickConfig;
import fr.stillcraft.proxykick.velocity.VelocityMain;
import fr.stillcraft.proxykick.velocity.VelocityText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KickCommand implements SimpleCommand {
    private final VelocityMain plugin;

    public KickCommand(VelocityMain plugin) { this.plugin = plugin; }

    @Override
    public void execute(Invocation invocation) {
        CommandSource sender = invocation.source();
        String[] args = invocation.arguments();
        ProxyKickConfig cfg = plugin.cfg;

        // Get each string from config and locale data
        boolean broadcast = cfg.cfgBool("broadcast");
        String kicked = cfg.msg("kick.kicked");
        String confirm = cfg.msg("kick.confirm");
        String reason = cfg.msg("global.reason");
        String separator = cfg.msg("global.separator");
        String punctuation = cfg.msg("global.punctuation");
        String info = cfg.msg("kick.info");
        String offline = cfg.msg("kick.offline");
        String empty = cfg.msg("global.empty");
        String bypass = cfg.msg("kick.bypass");
        String bypass_warn = cfg.msg("kick.bypass_warn");
        String usage = cfg.msg("global.usage")+cfg.msg("kick.usage");
        String description = cfg.msg("global.description")+cfg.msg("kick.description");

        if (args.length > 0) {
            // Return help message
            if (args[0].equalsIgnoreCase("help")) {
                VelocityText.send(sender, usage);
                VelocityText.send(sender, description);
                return;
            }

            // No players connected, send message to sender
            if (plugin.getServer().getAllPlayers().isEmpty()) {
                VelocityText.send(sender, empty);
                return;
            }

            // Loop over players
            for (Player player : plugin.getServer().getAllPlayers()) {
                if (args[0].equalsIgnoreCase(player.getUsername())) {

                    // Construct complete kick strings
                    String reason_string = MessageFormatter.buildReason(args, 1);
                    String[] msgs = MessageFormatter.appendReasonOrPunctuation(kicked, confirm, info, reason_string, reason, separator, punctuation);
                    kicked = msgs[0];
                    confirm = msgs[1];
                    info = msgs[2];

                    // Parse placeholders
                    String senderName = VelocityText.senderName(sender);
                    kicked = MessageFormatter.replacePlaceholders(kicked, senderName, player.getUsername(), reason_string);
                    confirm = MessageFormatter.replacePlaceholders(confirm, senderName, player.getUsername(), reason_string);
                    info = MessageFormatter.replacePlaceholders(info, senderName, player.getUsername(), reason_string);
                    bypass = MessageFormatter.replacePlaceholders(bypass, null, player.getUsername(), null);
                    bypass_warn = MessageFormatter.replacePlaceholders(bypass_warn, senderName, null, null);

                    // If player has bypass do not kick and warn player AND
                    // If sender is a player (CONSOLE and Rcon are not concerned)
                    if (player.hasPermission("proxykick.bypass") && (sender instanceof Player)) {
                        VelocityText.send(sender, bypass);
                        VelocityText.send(player, bypass_warn);
                        return;
                    }

                    // Execute actions (kicks player, and send messages)
                    player.disconnect(VelocityText.of(kicked));
                    plugin.getLogger().info(info);

                    // Broadcast message to all players if broadcast true in config
                    if (broadcast) {
                        for (Player pp : plugin.getServer().getAllPlayers()) {
                            VelocityText.send(pp, info);
                        }
                    } else {
                        VelocityText.send(sender, confirm);
                    }
                    return;
                }
            }
            // Player not found, send message to sender
            offline = MessageFormatter.replacePlaceholders(offline, null, args[0], null);
            VelocityText.send(sender, offline);

        } else {
            // Send usage and description message to sender
            VelocityText.send(sender, usage);
            VelocityText.send(sender, description);
        }
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        String[] args = invocation.arguments();
        if (args.length > 1 || args.length == 0) {
            return Collections.emptyList();
        }

        List<String> matches = new ArrayList<>();
        String search = args[0].toLowerCase();
        for (Player player : plugin.getServer().getAllPlayers()) {
            if (player.getUsername().toLowerCase().startsWith(search)) {
                matches.add(player.getUsername());
            }
        }
        if ("help".startsWith(search)) matches.add("help");
        return matches;
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("proxykick.kick");
    }
}
