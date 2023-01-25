package moe.seikimo.laudiolin;

import lombok.Getter;

import java.util.List;

/**
 * Configuration holder for Laudiolin.
 */
@Getter
public final class LaudiolinConfig {
    /**
     * The Discord bot token to use.
     * This should be an 'authorization: bot ...' token.
     */
    private String token = "";

    /**
     * The URL to a Discord webhook to send error messages to.
     * This should be a Discord webhook URL.
     */
    private String errorWebhook = "";

    /**
     * This is the HTTP endpoint the bot will use.
     * Should be pointed to a Laudiolin Backend.
     */
    private String endpoint = "https://app.seikimo.moe";

    /**
     * This is the bot's command prefix for message commands.
     * Should not contain spaces.
     */
    private String prefix = "l!";

    /**
     * The color to use for embeds.
     * Should be a hex color code.
     */
    private String color = "#cbe5ed";

    /**
     * A collection of bot administrators.
     * Should be a list of Discord user IDs.
     */
    private List<String> admins = List.of("");
}
