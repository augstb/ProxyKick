package fr.stillcraft.proxykick.commands;

import fr.stillcraft.proxykick.ProxyKick;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

import java.io.IOException;

public class reload extends Command {
    public reload() { super("proxykick:reload","proxykick.reload"); }

    @Override
    public void execute(CommandSender sender, String[] args) {
        ProxyKick.createFile("config");
        ProxyKick.createFile("locale_fr");
        ProxyKick.createFile("locale_en");
        try {
            // Reload config file
            ProxyKick.config = ProxyKick.getInstance().getConfig("config");
            String locale_string = ProxyKick.config.getString("locale");
            ProxyKick.locale = ProxyKick.getInstance().getConfig("locale_" + locale_string);

            String reloaded_string = ChatColor.WHITE+"[ProxyKick]"+ChatColor.GRAY+" Config and locale files reloaded.";
            sender.sendMessage(new TextComponent(reloaded_string));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
