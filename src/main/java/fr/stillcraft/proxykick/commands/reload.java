package fr.stillcraft.proxykick.commands;

import com.google.common.collect.ImmutableSet;
import fr.stillcraft.proxykick.ProxyKick;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class reload extends Command implements TabExecutor {
    public reload() { super("proxykick:reload","proxykick.reload"); }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length > 0) {
            // Get each string from config and locale data
            String usage = ProxyKick.locale.getString("global.usage")+ProxyKick.locale.getString("reload.usage");
            String description = ProxyKick.locale.getString("global.description")+ProxyKick.locale.getString("reload.description");

            // Colorize each string
            usage = ChatColor.translateAlternateColorCodes('&', usage);
            description = ChatColor.translateAlternateColorCodes('&', description);

            // Return help message
            if (args[0].equalsIgnoreCase("help")) {
                sender.sendMessage(new TextComponent(usage));
                sender.sendMessage(new TextComponent(description));
                return;
            }
        }

        ProxyKick.checkConfig("config");
        ProxyKick.checkConfig("locale_fr");
        ProxyKick.checkConfig("locale_en");
        try {
            // Reload config file
            ProxyKick.config = ProxyKick.getInstance().getConfig("config");
            String locale_string = ProxyKick.config.getString("locale");
            ProxyKick.locale = ProxyKick.getInstance().getConfig("locale_" + locale_string);

            String success = ProxyKick.locale.getString("global.prefix")+" "+ProxyKick.locale.getString("reload.success");
            success = ChatColor.translateAlternateColorCodes('&', success);

            sender.sendMessage(new TextComponent(success));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Iterable<String> onTabComplete(CommandSender sender, String[] args){
        if (args.length>2 || args.length==0){
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
