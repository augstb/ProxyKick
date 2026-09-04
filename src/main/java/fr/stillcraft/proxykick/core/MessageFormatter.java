package fr.stillcraft.proxykick.core;

/**
 * Platform-agnostic message-building helpers shared by the BungeeCord and Velocity kick/kickall
 * commands, so the reason-parsing and placeholder-substitution logic (and its bug fixes) only
 * ever lives in one place.
 */
public final class MessageFormatter {
    private MessageFormatter() {}

    // Joins args[fromIndex..] into a trimmed reason string ("" if there is none).
    public static String buildReason(String[] args, int fromIndex) {
        StringBuilder builder = new StringBuilder();
        for (int i = fromIndex; i < args.length; i++) {
            builder.append(args[i]).append(" ");
        }
        String reason = builder.toString();
        return reason.trim().isEmpty() ? "" : reason.substring(0, reason.length() - 1);
    }

    // Appends punctuation (no reason given) or separator+reason (reason given) to the kicked/confirm/info templates.
    public static String[] appendReasonOrPunctuation(String kicked, String confirm, String info,
                                                       String reasonString, String reasonTemplate,
                                                       String separator, String punctuation) {
        String suffix = reasonString.isEmpty() ? punctuation : (separator + reasonTemplate);
        return new String[]{ kicked + suffix, confirm + suffix, info + suffix };
    }

    // Safe (non-regex) placeholder substitution. Uses String.replace() rather than replaceAll() so
    // player-supplied text (reason, player name) containing regex-special characters like $ or \
    // can't crash the command. Pass null to skip a placeholder.
    public static String replacePlaceholders(String template, String sender, String player, String reason) {
        String result = template;
        if (sender != null) result = result.replace("%sender%", sender);
        if (player != null) result = result.replace("%player%", player);
        if (reason != null) result = result.replace("%reason%", reason);
        return result;
    }
}
