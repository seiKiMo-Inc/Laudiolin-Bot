package moe.seikimo.laudiolin.social;

import moe.seikimo.laudiolin.audio.LaudiolinAudioManager;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * Listens for voice audio events.
 */
public final class AudioListener extends ListenerAdapter {
    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
        // Pull arguments.
        var guild = event.getGuild();
        var member = event.getMember();

        // Get the guild audio manager.
        var audioManager = LaudiolinAudioManager.getInstance()
            .getAudioManager(guild);
        // Check if the audio manager is null.
        if (audioManager == null) return;

        // Check if the bot was disconnected.
        if (guild.getSelfMember() == member &&
            event.getChannelLeft() != null &&
            event.getChannelJoined() == null)
            audioManager.stop();
    }
}
