package moe.seikimo.laudiolin.utils;

import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import tech.xigam.cch.utils.Interaction;

import java.util.Objects;

/**
 * Handle voice interactions.
 */
public interface VoiceUtil {
    /**
     * Checks to see if the bot is in a voice channel.
     * @param interaction The interaction to check.
     * @return True if the bot is in a voice channel.
     */
    static boolean isConnected(Interaction interaction) {
        return Objects.requireNonNull(interaction.getGuild())
            .getAudioManager().isConnected();
    }

    /**
     * Connects to an audio channel.
     * @param channel The channel to connect to.
     */
    static void connectTo(AudioChannel channel) {
        channel.getGuild().getAudioManager().openAudioConnection(channel);
    }

    /**
     * Disconnects from the current audio channel.
     * @param interaction The interaction to disconnect from.
     */
    static void disconnect(Interaction interaction) {
        Objects.requireNonNull(interaction.getGuild())
            .getAudioManager().closeAudioConnection();
    }
}
