/*
 * Copyright © 2022 Ben Petrillo. All rights reserved.
 *
 * Project licensed under the MIT License: https://www.mit.edu/~amini/LICENSE.md
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * All portions of this software are available for public use, provided that
 * credit is given to the original author(s).
 */

package moe.seikimo.laudiolin.audio.source.spotify;

import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.*;
import com.sedmelluq.discord.lavaplayer.track.playback.LocalAudioTrackExecutor;
import lombok.Getter;
import moe.seikimo.laudiolin.utils.BackendUtil;
import org.jetbrains.annotations.Nullable;
import se.michaelthelin.spotify.model_objects.specification.*;

public final class SpotifyTrack extends DelegatedAudioTrack {
    @Getter private final String isrc;
    @Getter private final String artworkURL;
    private final SpotifyAudioSourceManager spotifySourceManager;

    /**
     * Creates a Spotify track with all metadata.
     *
     * @param spotifySourceManager The source manager that created this track.
     */
    public SpotifyTrack(
        String title, String identifier, String isrc,
        Image[] images, String uri, ArtistSimplified[] artists,
        Integer trackDuration, SpotifyAudioSourceManager spotifySourceManager
    ) {
        this(new AudioTrackInfo(title,
                artists.length == 0 ? "unknown" : artists[0].getName(),
                trackDuration.longValue(),
                identifier,
                false,
                "https://open.spotify.com/track/" + identifier
        ), isrc, images.length == 0 ? null :
            images[0].getUrl(), spotifySourceManager);
    }

    /**
     * Creates a Spotify track with all metadata.
     * Uses a track info object.
     *
     * @param spotifySourceManager The source manager that created this track.
     */
    public SpotifyTrack(
        AudioTrackInfo trackInfo, String isrc, String artworkURL,
        SpotifyAudioSourceManager spotifySourceManager
    ) {
        super(trackInfo);

        this.isrc = isrc;
        this.artworkURL = artworkURL;
        this.spotifySourceManager = spotifySourceManager;
    }

    /**
     * Creates a Spotify track from a simplified track.
     *
     * @param spotifySourceManager The source manager that created this track.
     * @return The Spotify track.
     */
    public static SpotifyTrack of(TrackSimplified track, Album album, SpotifyAudioSourceManager spotifySourceManager) {
        return new SpotifyTrack(track.getName(), track.getId(), null, album.getImages(),
            track.getUri(), track.getArtists(), track.getDurationMs(), spotifySourceManager
        );
    }

    /**
     * Creates a Spotify track from a full track.
     *
     * @param spotifySourceManager The source manager that created this track.
     * @return The Spotify track.
     */
    public static SpotifyTrack of(Track track, SpotifyAudioSourceManager spotifySourceManager) {
        return new SpotifyTrack(
            track.getName(), track.getId(), track.getExternalIds().getExternalIds()
            .getOrDefault("isrc", null), track.getAlbum().getImages(),
            track.getUri(), track.getArtists(), track.getDurationMs(), spotifySourceManager
        );
    }

    /**
     * Transforms the track metadata into a query.
     *
     * @return The query.
     */
    private String getQuery() {
        return this.trackInfo.title + " " + this.trackInfo.author;
    }

    /**
     * Performs a search for the track.
     *
     * @return The track.
     */
    @Nullable private AudioItem searchForTrack() {
        return this.spotifySourceManager.getSearchSourceManager()
            .loadItem(null, new AudioReference("ytsearch:" + this.getQuery(), null));
    }

    @Override
    public void process(LocalAudioTrackExecutor executor) throws Exception {
        AudioItem track = null;
        // Perform a normal search if necessary.
        if (this.isrc != null) track = this.searchForTrack();
        if (track == null) track = this.searchForTrack();

        // Check if the track is just a reference.
        if (track instanceof AudioReference) {
            var query = BackendUtil.search(this.isrc);
            if (query == null) query = BackendUtil.search(this.getQuery());
            if (query == null) throw new RuntimeException("Unable to find track: " + this.getQuery());
            track = this.spotifySourceManager.getSearchSourceManager()
                .loadItem(null, new AudioReference(query.getTop().getUrl(), null));
        }

        // Check if the track is a type of playlist.
        if (track instanceof AudioPlaylist playlist) {
            track = playlist.getTracks().get(0);
        }

        // Check if the track is valid.
        if (track instanceof InternalAudioTrack internal) {
            this.processDelegate(internal, executor);
        } else {
            throw new RuntimeException("Unable to find track: " + this.getQuery());
        }
    }

    @Override
    public AudioSourceManager getSourceManager() {
        return this.spotifySourceManager;
    }

    @Override
    protected AudioTrack makeShallowClone() {
        return new SpotifyTrack(this.trackInfo, this.isrc, this.artworkURL, this.spotifySourceManager);
    }
}
