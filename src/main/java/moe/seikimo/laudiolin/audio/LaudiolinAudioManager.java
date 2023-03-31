package moe.seikimo.laudiolin.audio;

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
import moe.seikimo.laudiolin.audio.source.LaudiolinSourceManager;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.Nullable;
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

        // Create a shutdown hook.
        Runtime.getRuntime().addShutdownHook(new Thread(() ->
            instance.audioManagers.forEach((guild, audioManager) -> {
                // Destroy the audio player.
                audioManager.getAudioPlayer().destroy();
                // Close the audio connection.
                guild.getAudioManager().closeAudioConnection();
        })));
    }

    private final AudioPlayerManager audioPlayerManager = new DefaultAudioPlayerManager();
    private final Map<Guild, GuildAudioManager> audioManagers = new HashMap<>();

    private LaudiolinAudioManager() {
        // Add the Laudiolin source manager.
        this.audioPlayerManager.registerSourceManager(new LaudiolinSourceManager());

        // Add default sources to the audio player manager.
        this.audioPlayerManager.registerSourceManager(new BandcampAudioSourceManager());
        this.audioPlayerManager.registerSourceManager(new VimeoAudioSourceManager());
        this.audioPlayerManager.registerSourceManager(new TwitchStreamAudioSourceManager());
        this.audioPlayerManager.registerSourceManager(new BeamAudioSourceManager());
        this.audioPlayerManager.registerSourceManager(new GetyarnAudioSourceManager());
        this.audioPlayerManager.registerSourceManager(new HttpAudioSourceManager());
        AudioSourceManagers.registerLocalSource(this.audioPlayerManager);
    }

    /**
     * Fetches an audio manager for the guild.
     * Creates a new one if it doesn't exist.
     * @param guild The guild to fetch the audio manager for.
     * @return A {@link GuildAudioManager} instance.
     */
    public GuildAudioManager getAudioManager(Guild guild) {
        return this.audioManagers.computeIfAbsent(guild, g -> {
            var audioManager = new GuildAudioManager(this.audioPlayerManager);
            g.getAudioManager().setSendingHandler(audioManager.newAudioSender());
            return audioManager;
        });
    }

    /**
     * Removes an audio manager from the cache.
     *
     * @param manager The manager to remove.
     * @return The guild the manager was removed from.
     */
    @Nullable
    public Guild removeAudioManager(GuildAudioManager manager) {
        // Get the guild for the manager.
        var guild = this.audioManagers.entrySet().stream()
            .filter(entry -> entry.getValue().equals(manager))
            .map(Map.Entry::getKey)
            .findFirst().orElse(null);

        // Remove the manager.
        if (guild != null)
            this.audioManagers.remove(guild);

        return guild;
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
