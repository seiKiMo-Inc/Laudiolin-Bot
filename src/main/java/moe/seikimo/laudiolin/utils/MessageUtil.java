package moe.seikimo.laudiolin.utils;

import moe.seikimo.laudiolin.objects.enums.MessageType;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

/**
 * Handle messages.
 */
public interface MessageUtil {
    MessageEmbed NOT_SERVER = generic("This command can only be used in a server.", MessageType.ERROR);
    MessageEmbed NO_PERMISSION = generic("You do not have permission to use this command.", MessageType.ERROR);
    MessageEmbed UNKNOWN_ARGUMENT = generic("Unknown argument.", MessageType.ERROR);

    /**
     * Creates a generic embed.
     * @param content The embed text content.
     * @return A MessageEmbed object.
     */
    static MessageEmbed generic(String content) {
        return generic(content, MessageType.INFO);
    }

    /**
     * Creates a generic message embed.
     * @param content The text content of the embed.
     * @param type The type of embed.
     * @return A MessageEmbed object.
     */
    static MessageEmbed generic(String content, MessageType type) {
        return generic(null, content, type);
    }

    /**
     * Creates a generic message embed.
     * @param title The title of the embed.
     * @param content The text content of the embed.
     * @param type The type of embed.
     * @return A MessageEmbed object.
     */
    static MessageEmbed generic(String title, String content, MessageType type) {
        var embed = new EmbedBuilder()
            .setColor(type.getColor())
            .setDescription(content);

        // Add an embed title.
        if (title != null) embed.setTitle(title);

        return embed.build();
    }
}
