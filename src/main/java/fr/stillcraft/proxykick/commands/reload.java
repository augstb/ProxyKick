package fr.stillcraft.proxykick.commands;

import com.google.common.collect.ImmutableSet;
import fr.stillcraft.proxykick.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.HashSet;
import java.util.Set;

public class reload extends Command implements TabExecutor {
    public reload() { super("proxykick:reload","proxykick.reload"); }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length > 0) {
            // Get each string from config and locale data
            String usage = Main.cfg.msg("global.usage")+Main.cfg.msg("reload.usage");
            String description = Main.cfg.msg("global.description")+Main.cfg.msg("reload.description");

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

        // Reload config file
        Main.cfg.checkAndLoad();

        String success = Main.cfg.msg("global.prefix")+" "+Main.cfg.msg("reload.success");
        success = ChatColor.translateAlternateColorCodes('&', success);

        sender.sendMessage(new TextComponent(success));
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
