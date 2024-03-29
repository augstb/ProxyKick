package fr.stillcraft.proxykick.commands;

import com.google.common.collect.ImmutableSet;
import fr.stillcraft.proxykick.Main;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class proxykick extends Command implements TabExecutor {
    public proxykick() {
        super("proxykick", "proxykick.kick", "pk");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        boolean sender_isplayer = (sender instanceof ProxiedPlayer);
        boolean has_kickall_perm = (!sender_isplayer || sender.hasPermission("proxykick.kickall"));
        boolean has_reload_perm = (!sender_isplayer || sender.hasPermission("proxykick.reload"));

        if (args.length >= 1) {
            if(args[0].equals("kick")) new kick().execute(sender, Arrays.copyOfRange(args, 1, args.length));
            else if(args[0].equals("kickall") && has_kickall_perm) new kickall().execute(sender, Arrays.copyOfRange(args, 1, args.length));
            else if(args[0].equals("reload") && has_reload_perm) new reload().execute(sender, Arrays.copyOfRange(args, 1, args.length));
            else if(args[0].equals("help")) new help().execute(sender, Arrays.copyOfRange(args, 1, args.length));
            else if(args[0].equals("version")) new version().execute(sender, Arrays.copyOfRange(args, 1, args.length));
            else if(args[0].equals("info")) new version().execute(sender, Arrays.copyOfRange(args, 1, args.length));
            else new help().execute(sender, Arrays.copyOfRange(args, 1, args.length));
        } else {
            new help().execute(sender,null);
        }
    }

    public Iterable<String> onTabComplete(CommandSender sender, String[] args){
        boolean sender_isplayer = (sender instanceof ProxiedPlayer);
        boolean has_kickall_perm = (!sender_isplayer || sender.hasPermission("proxykick.kickall"));
        boolean has_reload_perm = (!sender_isplayer || sender.hasPermission("proxykick.reload"));

        if (args.length>2 || args.length==0){
            return ImmutableSet.of();
        }

        Set<String> matches = new HashSet<>();
        if (args.length == 1){
            String search = args[0].toLowerCase();
            if ("help".startsWith(search)) matches.add("help");
            if ("kick".startsWith(search)) matches.add("kick");
            if ("kickall".startsWith(search) && has_kickall_perm) matches.add("kickall");
            if ("reload".startsWith(search) && has_reload_perm) matches.add("reload");
            if ("version".startsWith(search)) matches.add("version");
            if ("info".startsWith(search)) matches.add("info");
        }
        if (args.length == 2){
            String cmd = args[0].toLowerCase();
            String search = args[1].toLowerCase();
            if (cmd.equalsIgnoreCase("kick")){
                for (ProxiedPlayer player: Main.getInstance().getProxy().getPlayers()){
                    if (player.getName().toLowerCase().startsWith(search)){
                        matches.add(player.getName());
                    }
                }
                if ("help".startsWith(search)) matches.add("help");
            }
            if (cmd.equalsIgnoreCase("kickall") && has_kickall_perm){
                if ("help".startsWith(search)) matches.add("help");
            }
            if (cmd.equalsIgnoreCase("reload") && has_reload_perm){
                if ("help".startsWith(search)) matches.add("help");
            }
        }
        return matches;
    }
}
