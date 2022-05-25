package fr.stillcraft.proxykick.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

import java.util.Arrays;

public class proxykick extends Command {
    public proxykick() { super("proxykick", "proxykick.kick"); }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length >= 1) {
            if(args[0].equals("kick")) new kick().execute(sender, Arrays.copyOfRange(args, 1, args.length));
            if(args[0].equals("kickall")) new kickall().execute(sender, Arrays.copyOfRange(args, 1, args.length));
            if(args[0].equals("reload")) new reload().execute(sender, Arrays.copyOfRange(args, 1, args.length));
        } else {
            // if(args[0].equals("help")) new help().execute(sender, Arrays.copyOfRange(args, 1, args.length));
        }
    }
}
