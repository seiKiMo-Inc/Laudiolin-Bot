package moe.seikimo.laudiolin.audio.source;

import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.*;
import com.sedmelluq.discord.lavaplayer.track.playback.LocalAudioTrackExecutor;
import moe.seikimo.laudiolin.utils.BackendUtil;

public final class LaudiolinAudioTrack extends DelegatedAudioTrack {
    private final HttpAudioSourceManager httpAudioSource;
    private final String trackId;

    /**
     * @param trackInfo Track info
     */
    public LaudiolinAudioTrack(
        HttpAudioSourceManager httpAudioSource,
        AudioTrackInfo trackInfo,
        String trackId
    ) {
        super(trackInfo);

        this.httpAudioSource = httpAudioSource;
        this.trackId = trackId;
    }

    /**
     * Creates a URL for the track.
     * @return A URL for the track.
     */
    private String createUrl() {
        return BackendUtil.ENDPOINT + "/download?id=" + this.trackId;
    }

    @Override
    public void process(LocalAudioTrackExecutor executor) throws Exception {
        processDelegate((InternalAudioTrack) this.httpAudioSource.loadItem(null,
            new AudioReference(this.createUrl(), null)), executor);
    }

    @Override
    protected AudioTrack makeShallowClone() {
        return new LaudiolinAudioTrack(this.httpAudioSource,
            this.trackInfo, this.trackId);
    }
}
