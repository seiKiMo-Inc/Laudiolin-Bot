package moe.seikimo.laudiolin;

import lombok.Getter;

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
}
