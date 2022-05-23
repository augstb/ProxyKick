package fr.stillcraft.proxykick.commands;

import fr.stillcraft.proxykick.ProxyKick;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.config.Configuration;

import java.io.IOException;
import java.util.logging.Level;

public class kick extends Command {
    public kick() { super("kick"); }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (args.length >= 1){

            // No players connected
            if (ProxyKick.getInstance().getProxy().getPlayers().size() == 0) {
                try {
                    Configuration config = ProxyKick.getInstance().getConfig("config");
                    String locale_string = config.getString("locale");
                    Configuration locale = ProxyKick.getInstance().getConfig("locale_"+locale_string);

                    // Get strings from locale config file and colorize
                    String nobody_online = locale.getString("nobody_online");
                    nobody_online = ChatColor.translateAlternateColorCodes('&', nobody_online);

                    // Send nobody message to sender
                    commandSender.sendMessage(new TextComponent(nobody_online));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return;
            }

            // Loop over players
            for (ProxiedPlayer player: ProxyKick.getInstance().getProxy().getPlayers()) {
                if (args[0].equalsIgnoreCase(player.getDisplayName())) {
                    args[0] = "";
                    StringBuilder stringBuilder = new StringBuilder();
                    for(String arg: args){
                        stringBuilder.append(arg).append(" ");
                    }
                    String reason = stringBuilder.toString();        // Build reason string...

                    try {
                        Configuration config = ProxyKick.getInstance().getConfig("config");
                        String locale_string = config.getString("locale");
                        Configuration locale = ProxyKick.getInstance().getConfig("locale_"+locale_string);

                        // Get strings from locale config file
                        String kicked_string = locale.getString("kicked_string");
                        String sender_return = locale.getString("sender_return");
                        String reason_string = locale.getString("reason_string");
                        String reason_separator = locale.getString("reason_separator");
                        String punctuation = locale.getString("punctuation");
                        String console_kicked_string = locale.getString("console_kicked_string");

                        // Colorize strings
                        kicked_string = ChatColor.translateAlternateColorCodes('&', kicked_string);
                        sender_return = ChatColor.translateAlternateColorCodes('&', sender_return);
                        reason_string = ChatColor.translateAlternateColorCodes('&', reason_string);
                        reason_separator = ChatColor.translateAlternateColorCodes('&', reason_separator);
                        punctuation = ChatColor.translateAlternateColorCodes('&', punctuation);
                        console_kicked_string = ChatColor.translateAlternateColorCodes('&', console_kicked_string);

                        // Check if there is a reason or not.
                        if(reason.trim().isEmpty()){
                            kicked_string += punctuation;
                            sender_return += punctuation;
                            console_kicked_string += punctuation;
                        } else {
                            kicked_string += reason_separator+reason_string;
                            sender_return += reason_separator+reason_string;
                            console_kicked_string += reason_separator+reason_string;
                        }

                        // Replace placeholders at the end, after all operations on string
                        kicked_string = kicked_string.replaceAll("%sender%", commandSender.getName());
                        kicked_string = kicked_string.replaceAll("%player%", player.getDisplayName());
                        kicked_string = kicked_string.replaceAll("%reason%", reason);
                        sender_return = sender_return.replaceAll("%sender%", commandSender.getName());
                        sender_return = sender_return.replaceAll("%player%", player.getDisplayName());
                        sender_return = sender_return.replaceAll("%reason%", reason);
                        console_kicked_string = console_kicked_string.replaceAll("%sender%", commandSender.getName());
                        console_kicked_string = console_kicked_string.replaceAll("%player%", player.getDisplayName());
                        console_kicked_string = console_kicked_string.replaceAll("%reason%", reason);

                        // Execute actions (kicks player, and send messages)
                        player.disconnect(new TextComponent(kicked_string));
                        ProxyKick.getInstance().getLogger().log(Level.INFO,console_kicked_string);

                        // Broadcast message to all players if broadcast true in config
                        boolean broadcast = config.getBoolean("broadcast");
                        if(broadcast){
                            for (ProxiedPlayer pp: ProxyKick.getInstance().getProxy().getPlayers()) {
                                pp.sendMessage(new TextComponent(console_kicked_string));
                            }
                        } else {
                            commandSender.sendMessage(new TextComponent(sender_return));
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    // Exit for loop
                    return;
                }
            }

            // Player not found in the for loop
            try {
                Configuration config = ProxyKick.getInstance().getConfig("config");
                String locale_string = config.getString("locale");
                Configuration locale = ProxyKick.getInstance().getConfig("locale_"+locale_string);

                // Get strings from locale config file, colorize and replace placeholders
                String not_found = locale.getString("not_found");
                not_found = ChatColor.translateAlternateColorCodes('&', not_found);
                not_found = not_found.replaceAll("%player%", args[0]);

                // Send not found message to sender
                commandSender.sendMessage(new TextComponent(not_found));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        } else {
            try {
                Configuration config = ProxyKick.getInstance().getConfig("config");
                String locale_string = config.getString("locale");
                Configuration locale = ProxyKick.getInstance().getConfig("locale_"+locale_string);

                // Get strings from locale config file, colorize and replace placeholders
                String help = locale.getString("help");
                help = ChatColor.translateAlternateColorCodes('&', help);
                String description = locale.getString("description");
                description = ChatColor.translateAlternateColorCodes('&', description);

                // Send help and description message to sender
                commandSender.sendMessage(new TextComponent(help));
                commandSender.sendMessage(new TextComponent(description));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
