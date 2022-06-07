package fr.stillcraft.proxykick.commands;

import com.google.common.collect.ImmutableSet;
import fr.stillcraft.proxykick.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.HashSet;
import java.util.Set;

public class kickall extends Command implements TabExecutor {
    public kickall() { super("proxykick:kickall", "proxykick.kickall", "kickall"); }

    @Override
    public void execute(CommandSender sender, String[] args) {
        // Get each string from config and locale data
        boolean broadcast = Main.config.getBoolean("format.broadcast");
        String kicked = Main.locale.getString("kickall.kicked");
        String confirm = Main.locale.getString("kickall.confirm");
        String reason = Main.locale.getString("global.reason");
        String separator = Main.locale.getString("global.separator");
        String punctuation = Main.locale.getString("global.punctuation");
        String info = Main.locale.getString("kickall.info");
        String offline = Main.locale.getString("kickall.offline");
        String empty = Main.locale.getString("global.empty");
        String usage = Main.locale.getString("global.usage")+Main.locale.getString("kickall.usage");
        String description = Main.locale.getString("global.description")+Main.locale.getString("kickall.description");

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

        if(args.length > 0) {
            // Return help message
            if (args[0].equalsIgnoreCase("help")) {
                sender.sendMessage(new TextComponent(usage));
                sender.sendMessage(new TextComponent(description));
                return;
            }
        }

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
            reason_string = reason_string.substring(0, reason_string.length()-1);
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

        boolean success = false;
        for (ProxiedPlayer player : Main.getInstance().getProxy().getPlayers()) {

            // If player do not have bypass or sender is not player then kick
            if (!player.hasPermission("proxykick.bypass") || !(sender instanceof ProxiedPlayer)) {
                player.disconnect(new TextComponent(kicked));
                success = true;
            }
        }

        // Broadcast message to all players if broadcast true in config
        if (success) {
            if (broadcast) {
                for (ProxiedPlayer pp : Main.getInstance().getProxy().getPlayers()) {
                    pp.sendMessage(new TextComponent(info));
                }
            } else {
                sender.sendMessage(new TextComponent(confirm));
            }
        } else {
            if(Main.getInstance().getProxy().getPlayers().size() > 0) sender.sendMessage(new TextComponent(offline));
            else sender.sendMessage(new TextComponent(empty));
        }
    }

    public Iterable<String> onTabComplete(CommandSender sender, String[] args){
        if (args.length>1 || args.length==0){
            return ImmutableSet.of();
        }

        Set<String> matches = new HashSet<>();
        if (args.length == 1){
            String search = args[0].toLowerCase();
            if ("help".startsWith(search)) matches.add("help");
        }
        return matches;
    }
}
