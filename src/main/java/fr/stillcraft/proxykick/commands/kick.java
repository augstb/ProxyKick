package fr.stillcraft.proxykick.commands;

import fr.stillcraft.proxykick.ProxyKick;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.Arrays;
import java.util.logging.Level;

public class kick extends Command {
    public kick() { super("proxykick:kick","proxykick.kick", "kick"); }

    @Override
    public void execute(CommandSender sender, String[] args) {
        // Get each string from config and locale data
        boolean broadcast = ProxyKick.config.getBoolean("format.broadcast");
        String kicked = ProxyKick.locale.getString("kick.kicked");
        String confirm = ProxyKick.locale.getString("kick.confirm");
        String reason = ProxyKick.locale.getString("global.reason");
        String separator = ProxyKick.locale.getString("global.separator");
        String punctuation = ProxyKick.locale.getString("global.punctuation");
        String info = ProxyKick.locale.getString("kick.info");
        String offline = ProxyKick.locale.getString("kick.offline");
        String empty = ProxyKick.locale.getString("global.empty");
        String bypass = ProxyKick.locale.getString("kick.bypass");
        String bypass_warn = ProxyKick.locale.getString("kick.bypass_warn");
        String usage = ProxyKick.locale.getString("kick.usage");
        String description = ProxyKick.locale.getString("kick.description");

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
            if (ProxyKick.getInstance().getProxy().getPlayers().size() == 0) {
                sender.sendMessage(new TextComponent(empty));
                return;
            }

            // Loop over players
            for (ProxiedPlayer player : ProxyKick.getInstance().getProxy().getPlayers()) {
                if (args[0].equalsIgnoreCase(player.getDisplayName())) {

                    // Construct complete kick strings
                    StringBuilder stringBuilder = new StringBuilder();

                    for (String arg : Arrays.copyOfRange(args, 1, args.length)) {
                        stringBuilder.append(arg).append(" ");
                    }
                    String reason_string = stringBuilder.toString();
                    // Check if there is a reason or not.
                    if (reason_string.trim().isEmpty()) {
                        kicked += punctuation;
                        confirm += punctuation;
                        info += punctuation;
                    } else {
                        reason_string = reason_string.substring(0, reason_string.length()-1);
                        kicked += separator + reason;
                        confirm += separator + reason;
                        info += separator + reason;
                    }

                    // Parse placeholders
                    bypass_warn = bypass_warn.replaceAll("%sender%", sender.getName());
                    kicked = kicked.replaceAll("%sender%", sender.getName());
                    confirm = confirm.replaceAll("%sender%", sender.getName());
                    info = info.replaceAll("%sender%", sender.getName());
                    kicked = kicked.replaceAll("%reason%", reason_string);
                    confirm = confirm.replaceAll("%reason%", reason_string);
                    info = info.replaceAll("%reason%", reason_string);
                    bypass = bypass.replaceAll("%player%", player.getDisplayName());
                    kicked = kicked.replaceAll("%player%", player.getDisplayName());
                    confirm = confirm.replaceAll("%player%", player.getDisplayName());
                    info = info.replaceAll("%player%", player.getDisplayName());

                    // If player has bypass do not kick and warn player AND
                    // If sender is a player (CONSOLE and Rcon are not concerned)
                    if (player.hasPermission("proxykick.bypass") && (sender instanceof ProxiedPlayer)) {
                        sender.sendMessage(new TextComponent(bypass));
                        player.sendMessage(new TextComponent(bypass_warn));
                        return;
                    }

                    // Execute actions (kicks player, and send messages)
                    player.disconnect(new TextComponent(kicked));
                    ProxyKick.getInstance().getLogger().log(Level.INFO, info);

                    // Broadcast message to all players if broadcast true in config
                    if (broadcast) {
                        for (ProxiedPlayer pp : ProxyKick.getInstance().getProxy().getPlayers()) {
                            pp.sendMessage(new TextComponent(info));
                        }
                    } else {
                        sender.sendMessage(new TextComponent(confirm));
                    }
                    return;
                }
            }
            // Player not found, send message to sender
            offline = offline.replaceAll("%player%", args[0]);
            sender.sendMessage(new TextComponent(offline));

        } else {
            // Send usage and description message to sender
            sender.sendMessage(new TextComponent(usage));
            sender.sendMessage(new TextComponent(description));
        }
    }
}
