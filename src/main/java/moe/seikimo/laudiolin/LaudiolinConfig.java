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
    private String color = "#2771d3";

    /**
     * A collection of bot administrators.
     * Should be a list of Discord user IDs.
     */
    private List<String> admins = List.of("");
}
