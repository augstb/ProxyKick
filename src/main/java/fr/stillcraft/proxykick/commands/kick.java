package fr.stillcraft.proxykick.commands;

import com.google.common.collect.ImmutableSet;
import fr.stillcraft.proxykick.Main;
import fr.stillcraft.proxykick.core.MessageFormatter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

public class kick extends Command implements TabExecutor {
    public kick() { super("proxykick:kick","proxykick.kick", "kick"); }

    @Override
    public void execute(CommandSender sender, String[] args) {
        // Get each string from config and locale data
        boolean broadcast = Main.cfg.cfgBool("broadcast");
        String kicked = Main.cfg.msg("kick.kicked");
        String confirm = Main.cfg.msg("kick.confirm");
        String reason = Main.cfg.msg("global.reason");
        String separator = Main.cfg.msg("global.separator");
        String punctuation = Main.cfg.msg("global.punctuation");
        String info = Main.cfg.msg("kick.info");
        String offline = Main.cfg.msg("kick.offline");
        String empty = Main.cfg.msg("global.empty");
        String bypass = Main.cfg.msg("kick.bypass");
        String bypass_warn = Main.cfg.msg("kick.bypass_warn");
        String usage = Main.cfg.msg("global.usage")+Main.cfg.msg("kick.usage");
        String description = Main.cfg.msg("global.description")+Main.cfg.msg("kick.description");

        // Colorize each string
        kicked = ChatColor.translateAlternateColorCodes('&', kicked);
        confirm = ChatColor.translateAlternateColorCodes('&', confirm);
        reason = ChatColor.translateAlternateColorCodes('&', reason);
        separator = ChatColor.translateAlternateColorCodes('&', separator);
        punctuation = ChatColor.translateAlternateColorCodes('&', punctuation);
        info = ChatColor.translateAlternateColorCodes('&', info);
        offline = ChatColor.translateAlternateColorCodes('&', offline);
        empty = ChatColor.translateAlternateColorCodes('&', empty);
        usage = ChatColor.translateAlternateColorCodes('&', usage);
        description = ChatColor.translateAlternateColorCodes('&', description);
        bypass = ChatColor.translateAlternateColorCodes('&', bypass);
        bypass_warn = ChatColor.translateAlternateColorCodes('&', bypass_warn);

        if (args.length > 0) {
            // Return help message
            if (args[0].equalsIgnoreCase("help")) {
                sender.sendMessage(new TextComponent(usage));
                sender.sendMessage(new TextComponent(description));
                return;
            }

            // No players connected, send message to sender
            if (Main.getInstance().getProxy().getPlayers().size() == 0) {
                sender.sendMessage(new TextComponent(empty));
                return;
            }

            // Loop over players
            for (ProxiedPlayer player : Main.getInstance().getProxy().getPlayers()) {
                if (args[0].equalsIgnoreCase(player.getName())) {

                    // Construct complete kick strings
                    String reason_string = MessageFormatter.buildReason(args, 1);
                    String[] msgs = MessageFormatter.appendReasonOrPunctuation(kicked, confirm, info, reason_string, reason, separator, punctuation);
                    kicked = msgs[0];
                    confirm = msgs[1];
                    info = msgs[2];

                    // Parse placeholders
                    kicked = MessageFormatter.replacePlaceholders(kicked, sender.getName(), player.getDisplayName(), reason_string);
                    confirm = MessageFormatter.replacePlaceholders(confirm, sender.getName(), player.getDisplayName(), reason_string);
                    info = MessageFormatter.replacePlaceholders(info, sender.getName(), player.getDisplayName(), reason_string);
                    bypass = MessageFormatter.replacePlaceholders(bypass, null, player.getDisplayName(), null);
                    bypass_warn = MessageFormatter.replacePlaceholders(bypass_warn, sender.getName(), null, null);

                    // If player has bypass do not kick and warn player AND
                    // If sender is a player (CONSOLE and Rcon are not concerned)
                    if (player.hasPermission("proxykick.bypass") && (sender instanceof ProxiedPlayer)) {
                        sender.sendMessage(new TextComponent(bypass));
                        player.sendMessage(new TextComponent(bypass_warn));
                        return;
                    }

                    // Execute actions (kicks player, and send messages)
                    player.disconnect(new TextComponent(kicked));
                    Main.getInstance().getLogger().log(Level.INFO, info);

                    // Broadcast message to all players if broadcast true in config
                    if (broadcast) {
                        for (ProxiedPlayer pp : Main.getInstance().getProxy().getPlayers()) {
                            pp.sendMessage(new TextComponent(info));
                        }
                    } else {
                        sender.sendMessage(new TextComponent(confirm));
                    }
                    return;
                }
            }
            // Player not found, send message to sender
            offline = MessageFormatter.replacePlaceholders(offline, null, args[0], null);
            sender.sendMessage(new TextComponent(offline));

        } else {
            // Send usage and description message to sender
            sender.sendMessage(new TextComponent(usage));
            sender.sendMessage(new TextComponent(description));
        }
    }

    public Iterable<String> onTabComplete(CommandSender sender, String[] args){
        if (args.length>1 || args.length==0){
            return ImmutableSet.of();
        }

        Set<String> matches = new HashSet<>();
        if (args.length == 1){
            String search = args[0].toLowerCase();
            for (ProxiedPlayer player: Main.getInstance().getProxy().getPlayers()){
                if (player.getName().toLowerCase().startsWith(search)){
                    matches.add(player.getName());
                }
            }
            if ("help".startsWith(search)) matches.add("help");
        }
        return matches;
    }
}
