package fr.stillcraft.proxykick.velocity.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import fr.stillcraft.proxykick.velocity.VelocityMain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ProxyKickCommand implements SimpleCommand {
    private final VelocityMain plugin;
    private final KickCommand kickCommand;
    private final KickAllCommand kickAllCommand;
    private final ReloadCommand reloadCommand;
    private final HelpCommand helpCommand;
    private final VersionCommand versionCommand;

    public ProxyKickCommand(VelocityMain plugin) {
        this.plugin = plugin;
        this.kickCommand = new KickCommand(plugin);
        this.kickAllCommand = new KickAllCommand(plugin);
        this.reloadCommand = new ReloadCommand(plugin);
        this.helpCommand = new HelpCommand(plugin);
        this.versionCommand = new VersionCommand(plugin);
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource sender = invocation.source();
        String[] args = invocation.arguments();

        boolean senderIsPlayer = (sender instanceof Player);
        boolean hasKickallPerm = (!senderIsPlayer || sender.hasPermission("proxykick.kickall"));
        boolean hasReloadPerm = (!senderIsPlayer || sender.hasPermission("proxykick.reload"));

        if (args.length >= 1) {
            String[] rest = Arrays.copyOfRange(args, 1, args.length);
            if (args[0].equals("kick")) kickCommand.execute(subInvocation(sender, rest, "kick"));
            else if (args[0].equals("kickall") && hasKickallPerm) kickAllCommand.execute(subInvocation(sender, rest, "kickall"));
            else if (args[0].equals("reload") && hasReloadPerm) reloadCommand.execute(subInvocation(sender, rest, "reload"));
            else if (args[0].equals("help")) helpCommand.execute(subInvocation(sender, rest, "help"));
            else if (args[0].equals("version")) versionCommand.execute(subInvocation(sender, rest, "version"));
            else if (args[0].equals("info")) versionCommand.execute(subInvocation(sender, rest, "info"));
            else helpCommand.execute(subInvocation(sender, rest, "help"));
        } else {
            helpCommand.execute(subInvocation(sender, new String[0], "help"));
        }
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        CommandSource sender = invocation.source();
        String[] args = invocation.arguments();

        boolean senderIsPlayer = (sender instanceof Player);
        boolean hasKickallPerm = (!senderIsPlayer || sender.hasPermission("proxykick.kickall"));
        boolean hasReloadPerm = (!senderIsPlayer || sender.hasPermission("proxykick.reload"));

        if (args.length > 2 || args.length == 0) {
            return Collections.emptyList();
        }

        List<String> matches = new ArrayList<>();
        if (args.length == 1) {
            String search = args[0].toLowerCase();
            if ("help".startsWith(search)) matches.add("help");
            if ("kick".startsWith(search)) matches.add("kick");
            if ("kickall".startsWith(search) && hasKickallPerm) matches.add("kickall");
            if ("reload".startsWith(search) && hasReloadPerm) matches.add("reload");
            if ("version".startsWith(search)) matches.add("version");
            if ("info".startsWith(search)) matches.add("info");
        }
        if (args.length == 2) {
            String cmd = args[0].toLowerCase();
            String search = args[1].toLowerCase();
            if (cmd.equalsIgnoreCase("kick")) {
                for (Player player : plugin.getServer().getAllPlayers()) {
                    if (player.getUsername().toLowerCase().startsWith(search)) {
                        matches.add(player.getUsername());
                    }
                }
                if ("help".startsWith(search)) matches.add("help");
            }
            if (cmd.equalsIgnoreCase("kickall") && hasKickallPerm) {
                if ("help".startsWith(search)) matches.add("help");
            }
            if (cmd.equalsIgnoreCase("reload") && hasReloadPerm) {
                if ("help".startsWith(search)) matches.add("help");
            }
        }
        return matches;
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("proxykick.kick");
    }

    // Velocity gives no public factory for its own Invocation, so build a tiny one to forward a
    // sub-command's remaining args (mirrors how the BungeeCord proxykick command directly calls
    // e.g. `new kick().execute(sender, Arrays.copyOfRange(args, 1, args.length))`).
    private static Invocation subInvocation(CommandSource source, String[] args, String alias) {
        return new Invocation() {
            @Override public CommandSource source() { return source; }
            @Override public String[] arguments() { return args; }
            @Override public String alias() { return alias; }
        };
    }
}
