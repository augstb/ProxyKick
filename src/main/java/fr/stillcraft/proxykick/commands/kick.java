package fr.stillcraft.proxykick.commands;

import com.google.common.collect.ImmutableSet;
import fr.stillcraft.proxykick.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

public class kick extends Command implements TabExecutor {
    public kick() { super("proxykick:kick","proxykick.kick", "kick"); }

    @Override
    public void execute(CommandSender sender, String[] args) {
        // Get each string from config and locale data
        boolean broadcast = Main.config.getBoolean("broadcast");
        String kicked = Main.locale.getString("kick.kicked");
        String confirm = Main.locale.getString("kick.confirm");
        String reason = Main.locale.getString("global.reason");
        String separator = Main.locale.getString("global.separator");
        String punctuation = Main.locale.getString("global.punctuation");
        String info = Main.locale.getString("kick.info");
        String offline = Main.locale.getString("kick.offline");
        String empty = Main.locale.getString("global.empty");
        String bypass = Main.locale.getString("kick.bypass");
        String bypass_warn = Main.locale.getString("kick.bypass_warn");
        String usage = Main.locale.getString("global.usage")+Main.locale.getString("kick.usage");
        String description = Main.locale.getString("global.description")+Main.locale.getString("kick.description");

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
            offline = offline.replaceAll("%player%", args[0]);
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
