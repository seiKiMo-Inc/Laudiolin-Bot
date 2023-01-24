package moe.seikimo.laudiolin.objects;

import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import lombok.Getter;
import moe.seikimo.laudiolin.Laudiolin;

@Getter
public final class LaudiolinTrackInfo {
    private String title, artist, icon, url, id;
    private int duration; // in seconds

    /**
     * Converts this object into a Lavaplayer object.
     * @return A Lavaplayer object.
     */
    public AudioTrackInfo toLavaplayer() {
        return new AudioTrackInfo(this.title, this.artist,
            this.duration, this.id, false, this.url);
    }

    @Override
    public String toString() {
        return Laudiolin.getGson().toJson(this);
    }
}
