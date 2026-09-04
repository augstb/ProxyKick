package fr.stillcraft.proxykick.velocity;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

/**
 * Small helpers to bridge ProxyKick's '&'-coded locale strings (same files as BungeeCord) to
 * Velocity/Adventure's Component-based text API.
 */
public final class VelocityText {
    private VelocityText() {}

    public static Component of(String legacy) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(legacy);
    }

    public static void send(CommandSource source, String legacy) {
        source.sendMessage(of(legacy));
    }

    // Velocity's CommandSource has no generic getName() (unlike Bungee's CommandSender). Players
    // only have a username (no separate display name), and console has no name at all.
    public static String senderName(CommandSource source) {
        return (source instanceof Player) ? ((Player) source).getUsername() : "CONSOLE";
    }
}
