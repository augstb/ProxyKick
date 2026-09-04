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

public class kickall extends Command implements TabExecutor {
    public kickall() { super("proxykick:kickall", "proxykick.kickall", "kickall"); }

    @Override
    public void execute(CommandSender sender, String[] args) {
        // Get each string from config and locale data
        boolean broadcast = Main.cfg.cfgBool("broadcast");
        String kicked = Main.cfg.msg("kickall.kicked");
        String confirm = Main.cfg.msg("kickall.confirm");
        String reason = Main.cfg.msg("global.reason");
        String separator = Main.cfg.msg("global.separator");
        String punctuation = Main.cfg.msg("global.punctuation");
        String info = Main.cfg.msg("kickall.info");
        String offline = Main.cfg.msg("kickall.offline");
        String empty = Main.cfg.msg("global.empty");
        String usage = Main.cfg.msg("global.usage")+Main.cfg.msg("kickall.usage");
        String description = Main.cfg.msg("global.description")+Main.cfg.msg("kickall.description");

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
        String reason_string = MessageFormatter.buildReason(args, 0);
        String[] msgs = MessageFormatter.appendReasonOrPunctuation(kicked, confirm, info, reason_string, reason, separator, punctuation);
        kicked = msgs[0];
        confirm = msgs[1];
        info = msgs[2];

        // Parse placeholders
        kicked = MessageFormatter.replacePlaceholders(kicked, sender.getName(), null, reason_string);
        confirm = MessageFormatter.replacePlaceholders(confirm, sender.getName(), null, reason_string);
        info = MessageFormatter.replacePlaceholders(info, sender.getName(), null, reason_string);

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
