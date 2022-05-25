package fr.stillcraft.proxykick.commands;

import fr.stillcraft.proxykick.ProxyKick;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.logging.Level;

public class kickall extends Command {
    public kickall() { super("proxykick:kickall", "proxykick.kickall", "kickall"); }

    @Override
    public void execute(CommandSender sender, String[] args) {
        // Get each string from config and locale data
        boolean broadcast = ProxyKick.config.getBoolean("format.broadcast");
        String kicked = ProxyKick.locale.getString("kickall.kicked");
        String confirm = ProxyKick.locale.getString("kickall.confirm");
        String reason = ProxyKick.locale.getString("global.reason");
        String separator = ProxyKick.locale.getString("global.separator");
        String punctuation = ProxyKick.locale.getString("global.punctuation");
        String info = ProxyKick.locale.getString("kickall.info");
        String empty = ProxyKick.locale.getString("global.empty");
        String usage = ProxyKick.locale.getString("kickall.usage");
        String description = ProxyKick.locale.getString("kickall.description");

        // Colorize each string
        kicked = ChatColor.translateAlternateColorCodes('&', kicked);
        confirm = ChatColor.translateAlternateColorCodes('&', confirm);
        reason = ChatColor.translateAlternateColorCodes('&', reason);
        separator = ChatColor.translateAlternateColorCodes('&', separator);
        punctuation = ChatColor.translateAlternateColorCodes('&', punctuation);
        info = ChatColor.translateAlternateColorCodes('&', info);
        empty = ChatColor.translateAlternateColorCodes('&', empty);
        usage = ChatColor.translateAlternateColorCodes('&', usage);
        description = ChatColor.translateAlternateColorCodes('&', description);

        for (ProxiedPlayer player : ProxyKick.getInstance().getProxy().getPlayers()) {
            args[0] = "";

            // Construct complete kick strings
            StringBuilder stringBuilder = new StringBuilder();
            for (String arg : args) {
                stringBuilder.append(arg).append(" ");
            }
            String reason_string = stringBuilder.toString();
            // Check if there is a reason or not.
            if (reason_string.trim().isEmpty()) {
                kicked += punctuation;
                confirm += punctuation;
                info += punctuation;
            } else {
                kicked += separator + reason;
                confirm += separator + reason;
                info += separator + reason;
            }

            // Parse placeholders
            kicked = kicked.replaceAll("%sender%", sender.getName());
            confirm = confirm.replaceAll("%sender%", sender.getName());
            info = info.replaceAll("%sender%", sender.getName());
            kicked = kicked.replaceAll("%reason%", reason_string);
            confirm = confirm.replaceAll("%reason%", reason_string);
            info = info.replaceAll("%reason%", reason_string);
            kicked = kicked.replaceAll("%player%", player.getDisplayName());
            confirm = confirm.replaceAll("%player%", player.getDisplayName());
            info = info.replaceAll("%player%", player.getDisplayName());

            // If player has bypass do not kick AND
            // If sender is a player (CONSOLE and Rcon are not concerned)
            if (player.hasPermission("proxykick.bypass") && (sender instanceof ProxiedPlayer)){
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
        }
    }
}
