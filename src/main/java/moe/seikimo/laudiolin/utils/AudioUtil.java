package moe.seikimo.laudiolin.utils;

import moe.seikimo.laudiolin.audio.LaudiolinAudioManager;
import moe.seikimo.laudiolin.objects.Source;
import tech.xigam.cch.utils.Interaction;

/**
 * Playing audio methods.
 */
public interface AudioUtil {
    /**
     * Plays audio from the interaction.
     * @param interaction The interaction.
     */
    static void play(Interaction interaction) {
        var url = interaction.getArgument("query",
            "https://www.youtube.com/watch?v=dQw4w9WgXcQ", String.class);
        LaudiolinAudioManager.getInstance().play(url, interaction);
    }

    /**
     * Identifies a query from a source.
     * @param query The query.
     * @return The source.
     */
    static Source identify(String query) {
        if (query.contains("youtu.be") ||
            query.contains("youtube.com"))
            return Source.YOUTUBE;

        if (query.contains("open.spotify.com"))
            return Source.SPOTIFY;

        if (query.contains("laudiolin.seikimo.moe"))
            return Source.LAUDIOLIN;

        return Source.UNKNOWN;
    }
}
