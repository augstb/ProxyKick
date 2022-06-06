package fr.stillcraft.proxykick.commands;

import fr.stillcraft.proxykick.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class help extends Command {
    public help() { super("proxykick:help","proxykick.kick"); }

    @Override
    public void execute(CommandSender sender, String[] args) {
        // Get each string from config and locale data
        String global_prefix = Main.locale.getString("global.prefix");
        String help_usage = Main.locale.getString("help.usage");
        String help_description = Main.locale.getString("help.description");
        String kick_usage = Main.locale.getString("kick.usage");
        String kick_description = Main.locale.getString("kick.description");
        String kickall_usage = Main.locale.getString("kickall.usage");
        String kickall_description = Main.locale.getString("kickall.description");
        String reload_usage = Main.locale.getString("reload.usage");
        String reload_description = Main.locale.getString("reload.description");

        // Colorize each string
        global_prefix = ChatColor.translateAlternateColorCodes('&', global_prefix);
        help_usage = ChatColor.translateAlternateColorCodes('&', help_usage);
        help_description = ChatColor.translateAlternateColorCodes('&', help_description);
        kick_usage = ChatColor.translateAlternateColorCodes('&', kick_usage);
        kick_description = ChatColor.translateAlternateColorCodes('&', kick_description);
        kickall_usage = ChatColor.translateAlternateColorCodes('&', kickall_usage);
        kickall_description = ChatColor.translateAlternateColorCodes('&', kickall_description);
        reload_usage = ChatColor.translateAlternateColorCodes('&', reload_usage);
        reload_description = ChatColor.translateAlternateColorCodes('&', reload_description);

        sender.sendMessage(new TextComponent(ChatColor.WHITE+"--- "+global_prefix+ChatColor.WHITE+" ---"));
        sender.sendMessage(new TextComponent(help_usage+ChatColor.WHITE+" - "+help_description));
        sender.sendMessage(new TextComponent(kick_usage+ChatColor.WHITE+" - "+kick_description));
        sender.sendMessage(new TextComponent(kickall_usage+ChatColor.WHITE+" - "+kickall_description));
        sender.sendMessage(new TextComponent(reload_usage+ChatColor.WHITE+" - "+reload_description));
    }

}
