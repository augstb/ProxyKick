package fr.stillcraft.proxykick.commands;

import fr.stillcraft.proxykick.ProxyKick;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class version extends Command {
    public version() { super("proxykick:version","proxykick.kick"); }

    @Override
    public void execute(CommandSender sender, String[] args) {
        // Get each string from config and locale data
        String global_prefix = ProxyKick.locale.getString("global.prefix");

        // Colorize each string
        global_prefix = ChatColor.translateAlternateColorCodes('&', global_prefix);

        sender.sendMessage(new TextComponent(ChatColor.WHITE+"--- "+global_prefix+ChatColor.WHITE+" ---"));
        sender.sendMessage(new TextComponent(ChatColor.WHITE+"ProxyKick: "+ChatColor.AQUA+"v"+ProxyKick.version));
        sender.sendMessage(new TextComponent(ChatColor.WHITE+"Source: "+ChatColor.AQUA+ChatColor.ITALIC+"github.com/augstb/ProxyKick"));
        sender.sendMessage(new TextComponent(ChatColor.WHITE+"Dev: "+ChatColor.AQUA+"Augustin Blanchet"));
    }

}
