package moe.seikimo.laudiolin.audio;

import com.sedmelluq.discord.lavaplayer.container.MediaContainerRegistry;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.beam.BeamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.getyarn.GetyarnAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;
import tech.xigam.cch.utils.Interaction;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages audio.
 */
public final class LaudiolinAudioManager {
    @Getter private static LaudiolinAudioManager instance;

    /**
     * Initializes the audio manager.
     */
    public static void initialize() {
        instance = new LaudiolinAudioManager();
    }

    private final AudioPlayerManager audioPlayerManager = new DefaultAudioPlayerManager();
    private final Map<String, GuildAudioManager> audioManagers = new HashMap<>();

    private LaudiolinAudioManager() {
        // Add default sources to the audio player manager.
        this.audioPlayerManager.registerSourceManager(new BandcampAudioSourceManager());
        this.audioPlayerManager.registerSourceManager(new VimeoAudioSourceManager());
        this.audioPlayerManager.registerSourceManager(new TwitchStreamAudioSourceManager());
        this.audioPlayerManager.registerSourceManager(new BeamAudioSourceManager());
        this.audioPlayerManager.registerSourceManager(new GetyarnAudioSourceManager());
        this.audioPlayerManager.registerSourceManager(new HttpAudioSourceManager(
            MediaContainerRegistry.DEFAULT_REGISTRY));
        AudioSourceManagers.registerLocalSource(this.audioPlayerManager);
    }

    /**
     * Fetches an audio manager for the guild.
     * Creates a new one if it doesn't exist.
     * @param guild The guild to fetch the audio manager for.
     * @return A {@link GuildAudioManager} instance.
     */
    public GuildAudioManager getAudioManager(Guild guild) {
        return this.audioManagers.computeIfAbsent(guild.getId(), id -> {
            var audioManager = new GuildAudioManager(this.audioPlayerManager);
            guild.getAudioManager().setSendingHandler(audioManager.newAudioSender());
            return audioManager;
        });
    }

    /**
     * Removes an audio manager from the cache.
     * @param guild The guild to remove the audio manager for.
     */
    public void removeAudioManager(Guild guild) {
        this.audioManagers.remove(guild.getId());
    }

    /**
     * Plays a track from the provided URL.
     * @param track The track to play.
     * @param interaction The interaction to send the response to.
     */
    public void play(String track, Interaction interaction) {
        var guild = interaction.getGuild(); assert guild != null;
        var audioManager = this.getAudioManager(guild);

        this.audioPlayerManager.loadItemOrdered(audioManager, track,
            new LoadResultHandler(audioManager, interaction, track));
    }
}
