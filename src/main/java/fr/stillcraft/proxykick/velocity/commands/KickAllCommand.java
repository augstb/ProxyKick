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

public class KickAllCommand implements SimpleCommand {
    private final VelocityMain plugin;

    public KickAllCommand(VelocityMain plugin) { this.plugin = plugin; }

    @Override
    public void execute(Invocation invocation) {
        CommandSource sender = invocation.source();
        String[] args = invocation.arguments();
        ProxyKickConfig cfg = plugin.cfg;

        // Get each string from config and locale data
        boolean broadcast = cfg.cfgBool("broadcast");
        String kicked = cfg.msg("kickall.kicked");
        String confirm = cfg.msg("kickall.confirm");
        String reason = cfg.msg("global.reason");
        String separator = cfg.msg("global.separator");
        String punctuation = cfg.msg("global.punctuation");
        String info = cfg.msg("kickall.info");
        String offline = cfg.msg("kickall.offline");
        String empty = cfg.msg("global.empty");
        String usage = cfg.msg("global.usage")+cfg.msg("kickall.usage");
        String description = cfg.msg("global.description")+cfg.msg("kickall.description");

        if (args.length > 0) {
            // Return help message
            if (args[0].equalsIgnoreCase("help")) {
                VelocityText.send(sender, usage);
                VelocityText.send(sender, description);
                return;
            }
        }

        // Construct complete kick strings
        String reason_string = MessageFormatter.buildReason(args, 0);
        String[] msgs = MessageFormatter.appendReasonOrPunctuation(kicked, confirm, info, reason_string, reason, separator, punctuation);
        kicked = msgs[0];
        confirm = msgs[1];
        info = msgs[2];

        // Parse placeholders
        String senderName = VelocityText.senderName(sender);
        kicked = MessageFormatter.replacePlaceholders(kicked, senderName, null, reason_string);
        confirm = MessageFormatter.replacePlaceholders(confirm, senderName, null, reason_string);
        info = MessageFormatter.replacePlaceholders(info, senderName, null, reason_string);

        boolean success = false;
        for (Player player : plugin.getServer().getAllPlayers()) {

            // If player do not have bypass or sender is not player then kick
            if (!player.hasPermission("proxykick.bypass") || !(sender instanceof Player)) {
                player.disconnect(VelocityText.of(kicked));
                success = true;
            }
        }

        // Broadcast message to all players if broadcast true in config
        if (success) {
            if (broadcast) {
                for (Player pp : plugin.getServer().getAllPlayers()) {
                    VelocityText.send(pp, info);
                }
            } else {
                VelocityText.send(sender, confirm);
            }
        } else {
            if (!plugin.getServer().getAllPlayers().isEmpty()) VelocityText.send(sender, offline);
            else VelocityText.send(sender, empty);
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
        if ("help".startsWith(search)) matches.add("help");
        return matches;
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("proxykick.kickall");
    }
}
