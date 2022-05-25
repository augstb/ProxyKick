package fr.stillcraft.proxykick.commands;

import fr.stillcraft.proxykick.ProxyKick;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.logging.Level;

public class kick extends Command {
    public kick() { super("proxykick:kick","proxykick.kick", "kick"); }

    @Override
    public void execute(CommandSender sender, String[] args) {
        // Read configuration
        // Get config and locale data
        // Configuration config = ProxyKick.getInstance().getConfig("config");
        // String locale_string = ProxyKick.config.getString("locale");
        // Configuration locale = ProxyKick.getInstance().getConfig("locale_" + locale_string);

        // Get each string from config and locale data
        boolean broadcast = ProxyKick.config.getBoolean("broadcast");
        String kicked_string = ProxyKick.locale.getString("kicked_string");
        String sender_return = ProxyKick.locale.getString("sender_return");
        String reason_string = ProxyKick.locale.getString("reason_string");
        String reason_separator = ProxyKick.locale.getString("reason_separator");
        String punctuation = ProxyKick.locale.getString("punctuation");
        String console_kicked_string = ProxyKick.locale.getString("console_kicked_string");
        String not_found = ProxyKick.locale.getString("not_found");
        String nobody_online = ProxyKick.locale.getString("nobody_online");
        String help = ProxyKick.locale.getString("help");
        String description = ProxyKick.locale.getString("description");
        String bypass_message = ProxyKick.locale.getString("bypass_message");
        String bypass_warn = ProxyKick.locale.getString("bypass_warn");

        // Colorize each string
        kicked_string = ChatColor.translateAlternateColorCodes('&', kicked_string);
        sender_return = ChatColor.translateAlternateColorCodes('&', sender_return);
        reason_string = ChatColor.translateAlternateColorCodes('&', reason_string);
        reason_separator = ChatColor.translateAlternateColorCodes('&', reason_separator);
        punctuation = ChatColor.translateAlternateColorCodes('&', punctuation);
        console_kicked_string = ChatColor.translateAlternateColorCodes('&', console_kicked_string);
        not_found = ChatColor.translateAlternateColorCodes('&', not_found);
        nobody_online = ChatColor.translateAlternateColorCodes('&', nobody_online);
        help = ChatColor.translateAlternateColorCodes('&', help);
        description = ChatColor.translateAlternateColorCodes('&', description);
        bypass_message = ChatColor.translateAlternateColorCodes('&', bypass_message);
        bypass_warn = ChatColor.translateAlternateColorCodes('&', bypass_warn);

        if (args.length >= 1) {
            // No players connected, send message to sender
            if (ProxyKick.getInstance().getProxy().getPlayers().size() == 0) {
                sender.sendMessage(new TextComponent(nobody_online));
                return;
            }

            // Loop over players
            for (ProxiedPlayer player : ProxyKick.getInstance().getProxy().getPlayers()) {
                if (args[0].equalsIgnoreCase(player.getDisplayName())) {
                    args[0] = "";

                    // Construct complete kick strings
                    StringBuilder stringBuilder = new StringBuilder();
                    for (String arg : args) {
                        stringBuilder.append(arg).append(" ");
                    }
                    String reason = stringBuilder.toString();

                    // Check if there is a reason or not.
                    if (reason.trim().isEmpty()) {
                        kicked_string += punctuation;
                        sender_return += punctuation;
                        console_kicked_string += punctuation;
                    } else {
                        kicked_string += reason_separator + reason_string;
                        sender_return += reason_separator + reason_string;
                        console_kicked_string += reason_separator + reason_string;
                    }

                    // Parse placeholders
                    bypass_warn = bypass_warn.replaceAll("%sender%", sender.getName());
                    kicked_string = kicked_string.replaceAll("%sender%", sender.getName());
                    sender_return = sender_return.replaceAll("%sender%", sender.getName());
                    console_kicked_string = console_kicked_string.replaceAll("%sender%", sender.getName());
                    kicked_string = kicked_string.replaceAll("%reason%", reason);
                    sender_return = sender_return.replaceAll("%reason%", reason);
                    console_kicked_string = console_kicked_string.replaceAll("%reason%", reason);
                    bypass_message = bypass_message.replaceAll("%player%", player.getDisplayName());
                    kicked_string = kicked_string.replaceAll("%player%", player.getDisplayName());
                    sender_return = sender_return.replaceAll("%player%", player.getDisplayName());
                    console_kicked_string = console_kicked_string.replaceAll("%player%", player.getDisplayName());

                    // If player has bypass do not kick and warn player AND
                    // If sender is a player (CONSOLE and Rcon are not concerned)
                    if (player.hasPermission("proxykick.bypass") && (sender instanceof ProxiedPlayer)) {
                        sender.sendMessage(new TextComponent(bypass_message));
                        player.sendMessage(new TextComponent(bypass_warn));
                        return;
                    }

                    // Execute actions (kicks player, and send messages)
                    player.disconnect(new TextComponent(kicked_string));
                    ProxyKick.getInstance().getLogger().log(Level.INFO, console_kicked_string);

                    // Broadcast message to all players if broadcast true in config
                    if (broadcast) {
                        for (ProxiedPlayer pp : ProxyKick.getInstance().getProxy().getPlayers()) {
                            pp.sendMessage(new TextComponent(console_kicked_string));
                        }
                    } else {
                        sender.sendMessage(new TextComponent(sender_return));
                    }

                    // Exit for loop
                    return;
                }
            }

            // Player not found, send message to sender
            not_found = not_found.replaceAll("%player%", args[0]);
            sender.sendMessage(new TextComponent(not_found));

        } else {
            // Send help and description message to sender
            sender.sendMessage(new TextComponent(help));
            sender.sendMessage(new TextComponent(description));
        }
    }
}
