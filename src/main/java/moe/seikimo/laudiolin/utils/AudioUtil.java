package moe.seikimo.laudiolin.utils;

import moe.seikimo.laudiolin.audio.LaudiolinAudioManager;
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
}
