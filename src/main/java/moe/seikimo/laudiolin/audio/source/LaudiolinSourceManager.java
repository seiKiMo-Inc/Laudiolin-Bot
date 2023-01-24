package moe.seikimo.laudiolin.audio.source;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import lombok.SneakyThrows;
import moe.seikimo.laudiolin.objects.LaudiolinTrackInfo;
import moe.seikimo.laudiolin.utils.AudioUtil;
import moe.seikimo.laudiolin.utils.BackendUtil;

import java.io.DataInput;
import java.io.DataOutput;

public final class LaudiolinSourceManager implements AudioSourceManager {
    private final HttpAudioSourceManager httpAudioSource = new HttpAudioSourceManager();

    @Override
    public String getSourceName() {
        return "laudiolin";
    }

    @Override
    @SneakyThrows
    public AudioItem loadItem(AudioPlayerManager manager, AudioReference reference) {
        // Get the query.
        var query = reference.identifier.trim();
        if (query.contains("<") && query.contains(">"))
            query = query.substring(1, query.length() - 1);
        // Find the source.
        var source = AudioUtil.identify(query);

        // Parse the track data.
        var trackId = query;
        LaudiolinTrackInfo trackInfo = null;
        switch (source) {
            case YOUTUBE -> trackId = AudioUtil.pullYouTubeId(query);
            case SPOTIFY, LAUDIOLIN -> trackId = AudioUtil.pullGenericId(query);
            case UNKNOWN -> {
                // Search for the query.
                var results = BackendUtil.search(query);
                if (results == null) throw new Exception("No results found.");

                // Get the top result.
                trackInfo = results.getTop();
                if (trackInfo == null) throw new Exception("No results found.");
                trackId = trackInfo.getId();
            }
        }

        // Fetch the information for the track.
        if (trackInfo == null) trackInfo = BackendUtil.fetch(trackId);
        if (trackInfo == null) throw new Exception("Invalid track info.");

        return new LaudiolinAudioTrack(this.httpAudioSource,
            trackInfo.toLavaplayer(), trackId);
    }

    @Override
    public boolean isTrackEncodable(AudioTrack track) {
        return false;
    }

    @Override
    public void encodeTrack(AudioTrack track, DataOutput output) {

    }

    @Override
    public AudioTrack decodeTrack(AudioTrackInfo trackInfo, DataInput input) {
        return null;
    }

    @Override
    public void shutdown() {

    }
}
