package moe.seikimo.laudiolin.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import lombok.AllArgsConstructor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Manages track scheduling for the guild.
 */
@AllArgsConstructor
public final class TrackScheduler extends AudioEventAdapter {
    private final AudioPlayer audioPlayer;

    private final BlockingQueue<AudioTrack> queue =
        new LinkedBlockingQueue<>();

    /**
     * Queues a track to play.
     * @param track The track to queue.
     */
    public void queue(AudioTrack track) {
        // Check if the audio player is playing anything.
        if (this.audioPlayer.getPlayingTrack() == null) {
            // Play the track immediately.
            this.audioPlayer.playTrack(track);
        } else {
            // Queue the track.
            this.queue.add(track);
        }
    }

    /**
     * Skips to the next available track.
     */
    public void nextTrack() {
        // Check the queue's size.
        if (this.queue.isEmpty()) {
            // Stop the player.
            this.audioPlayer.stopTrack();
            return;
        }

        // Start the next track.
        this.audioPlayer.startTrack(
            this.queue.poll(), false);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (this.queue.size() == 0) {
            // Kill the player if done.
            this.audioPlayer.destroy();
        }

        // Check if the player should continue.
        if (endReason.mayStartNext) {
            this.nextTrack();
        }
    }
}
