package moe.seikimo.laudiolin.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import lombok.Getter;
import moe.seikimo.laudiolin.objects.audio.LaudiolinAudioSender;

/**
 * Manages audio playback for one guild.
 */
public final class GuildAudioManager {
    @Getter private final AudioPlayer audioPlayer;
    @Getter private final TrackScheduler scheduler;

    public GuildAudioManager(AudioPlayerManager manager) {
        this.audioPlayer = manager.createPlayer();
        this.scheduler = new TrackScheduler(this.audioPlayer);

        this.audioPlayer.addListener(this.scheduler);
    }

    /**
     * Creates a new audio send handler for this player.
     * @return A {@link LaudiolinAudioSender} instance.
     */
    public LaudiolinAudioSender newAudioSender() {
        return new LaudiolinAudioSender(this.audioPlayer);
    }

    /**
     * Checks if the player is playing anything.
     * @return True if the player is playing.
     */
    public boolean isPlaying() {
        return this.audioPlayer.getPlayingTrack() != null;
    }
}
