package moe.seikimo.laudiolin.utils;

import moe.seikimo.laudiolin.audio.LaudiolinAudioManager;
import moe.seikimo.laudiolin.objects.enums.Audio;
import moe.seikimo.laudiolin.objects.enums.Source;
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
     * Identifies a source from a query.
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

    /**
     * Identifies the type of audio from a query.
     *
     * @param query The query.
     * @return The audio type.
     */
    static Audio identifyType(String query) {
        return query.contains("playlist") ?
            Audio.PLAYLIST : Audio.TRACK;
    }

    /**
     * Attempts to pull a YouTube video ID.
     * @param query The query.
     * @return The video ID.
     */
    static String pullYouTubeId(String query) {
        if (query.contains("youtu.be"))
            return query.split("youtu.be/")[1];
        return query.split("v=")[1];
    }

    /**
     * Attempts to pull a generic track ID.
     * @param query The query.
     * @return The track ID.
     */
    static String pullGenericId(String query) {
        return query.split("/track/")[1];
    }

    /**
     * Attempts to pull a generic playlist ID.
     *
     * @param query The query.
     * @return The playlist ID.
     */
    static String pullPlaylistId(String query) {
        return query.split("/playlist/")[1];
    }
}
