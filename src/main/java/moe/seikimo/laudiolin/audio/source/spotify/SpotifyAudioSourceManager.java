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

import com.neovisionaries.i18n.CountryCode;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.DataFormatTools;
import com.sedmelluq.discord.lavaplayer.track.*;
import moe.seikimo.laudiolin.Laudiolin;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.enums.ModelObjectType;
import se.michaelthelin.spotify.model_objects.specification.*;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

public final class SpotifyAudioSourceManager implements AudioSourceManager {

    public static final Pattern SPOTIFY_URL_PATTERN = Pattern.compile("(https?://)?(www\\.)?open\\.spotify\\.com/(user/[a-zA-Z0-9-_]+/)?(?<type>track|album|playlist|artist)/(?<identifier>[a-zA-Z0-9-_]+)");
    public static final String SEARCH_PREFIX = "spsearch:";

    private static SpotifyApi spotify;
    private final AudioSourceManager searchAudioSourceManager;

    /**
     * Creates a new Spotify source manager.
     *
     * @param searchSource This is the source which will be used for searching.
     */
    public SpotifyAudioSourceManager(AudioSourceManager searchSource) {
        // Get the Spotify client ID and secret.
        var config = Laudiolin.getConfig();
        var clientId = config.getSpotifyClientId();
        var clientSecret = config.getSpotifyClientSecret();

        // Validate the Spotify client ID and secret.
        if (clientId == null || clientId.isEmpty())
            throw new IllegalArgumentException("Spotify client ID must be set.");
        if (clientSecret == null || clientSecret.isEmpty())
            throw new IllegalArgumentException("Spotify secret must be set.");

        try {
            // Authorize with the Spotify API.
            SpotifyAudioSourceManager.authorize();
        } catch (Exception exception) {
            Laudiolin.getLogger().warn("Unable to authorize with the Spotify API.", exception);
        }

        this.searchAudioSourceManager = searchSource;
    }

    /**
     * Attempts to authorize with the Spotify API.
     *
     * @throws Exception if an error occurs while authorizing.
     */
    public static void authorize() throws Exception {
        var config = Laudiolin.getConfig();
        var clientId = config.getSpotifyClientId();
        var clientSecret = config.getSpotifyClientSecret();

        // Validate the Spotify client ID and secret.
        if (clientId == null || clientId.isEmpty())
            throw new IllegalArgumentException("Spotify client ID must be set.");
        if (clientSecret == null || clientSecret.isEmpty())
            throw new IllegalArgumentException("Spotify secret must be set.");

        spotify = new SpotifyApi.Builder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .build();
        // Get the access token.
        var credRequest = new ClientCredentialsRequest.Builder(
            spotify.getClientId(), spotify.getClientSecret());
        var credentials = credRequest
            .grant_type("client_credentials")
            .build().execute();
        spotify.setAccessToken(credentials.getAccessToken());

        Laudiolin.getLogger().debug("Successfully updated Spotify OAuth access token.");
    }

    public AudioSourceManager getSearchSourceManager() {
        return this.searchAudioSourceManager;
    }

    @Override
    public String getSourceName() {
        return "spotify";
    }

    @Override
    public AudioItem loadItem(AudioPlayerManager manager, AudioReference reference) {
        try {
            if (reference.identifier.startsWith(SEARCH_PREFIX)) {
                return this.getSearch(reference.identifier.substring(SEARCH_PREFIX.length()).trim());
            }

            var matcher = SPOTIFY_URL_PATTERN.matcher(reference.identifier);
            if (!matcher.find()) return null;

            var id = matcher.group("identifier");
            var type = matcher.group("type");
            return switch (type) {
                default -> throw new Exception("Unknown type: " + type);
                case "album" -> this.getAlbum(id);
                case "track" -> this.getTrack(id);
                case "playlist" -> this.getPlaylist(id);
                case "artist" -> this.getArtist(id);
            };
        } catch (Exception ignored) {
            return null;
        }
    }

    @Override
    public boolean isTrackEncodable(AudioTrack track) {
        return true;
    }

    @Override
    public void encodeTrack(AudioTrack track, DataOutput output) {
        if (!(track instanceof SpotifyTrack spotifyTrack)) {
            throw new RuntimeException("Track is not a Spotify track.");
        }

        try {
            DataFormatTools.writeNullableText(output, spotifyTrack.getIsrc());
            DataFormatTools.writeNullableText(output, spotifyTrack.getArtworkURL());
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public AudioTrack decodeTrack(AudioTrackInfo trackInfo, DataInput input) {
        String isrc = null, artworkURL = null;

        try {
            isrc = DataFormatTools.readNullableText(input);
            artworkURL = DataFormatTools.readNullableText(input);
        } catch (IOException ignored) { }

        return new SpotifyTrack(
            trackInfo, isrc,
            artworkURL, this
        );
    }

    @Override
    public void shutdown() {}

    public AudioItem getSearch(String query) throws Exception {
        var searchResult = spotify.searchTracks(query).build().execute();
        if (searchResult.getItems().length == 0) {
            return AudioReference.NO_TRACK;
        }

        var tracks = new ArrayList<AudioTrack>();
        for (var item : searchResult.getItems()) {
            tracks.add(SpotifyTrack.of(item, this));
        }

        return new BasicAudioPlaylist(
            "Search results for: " + query,
            tracks, null, true
        );
    }

    public AudioItem getTrack(String id) throws Exception {
        var track = spotify.getTrack(id).build().execute();
        return SpotifyTrack.of(track, this);
    }

    public AudioItem getAlbum(String id) throws Exception {
        var album = spotify.getAlbum(id).build().execute();
        var tracks = new ArrayList<AudioTrack>();

        Paging<TrackSimplified> paging = null;
        do {
            paging = spotify.getAlbumsTracks(id).limit(50)
                .offset(paging == null ? 0 : paging.getOffset() + 50)
                .build().execute();
            for (var item : paging.getItems()) {
                if (item.getType() != ModelObjectType.TRACK)
                    continue;
                tracks.add(SpotifyTrack.of(item, album, this));
            }
        } while (paging.getNext() != null);

        return new BasicAudioPlaylist(
            album.getName(), tracks,
            null, false
        );
    }

    public AudioItem getPlaylist(String id) throws Exception {
        var playlist = spotify.getPlaylist(id).build().execute();
        var tracks = new ArrayList<AudioTrack>();

        Paging<PlaylistTrack> paging = null;
        do {
            paging = spotify.getPlaylistsItems(id).limit(50)
                .offset(paging == null ? 0 : paging.getOffset() + 50)
                .build().execute();
            for (var item : paging.getItems()) {
                if (item.getIsLocal() || item.getTrack().getType() != ModelObjectType.TRACK)
                    continue;
                tracks.add(SpotifyTrack.of((Track) item.getTrack(), this));
            }
        } while (paging.getNext() != null);

        return new BasicAudioPlaylist(
            playlist.getName(), tracks,
            null, false
        );
    }

    public AudioItem getArtist(String id) throws Exception {
        var artist = spotify.getArtist(id).build().execute();
        var artistTracks = spotify.getArtistsTopTracks(id, CountryCode.US)
            .build().execute();
        var tracks = new ArrayList<AudioTrack>();

        for (var item : artistTracks) {
            if (item.getType() != ModelObjectType.TRACK) continue;
            tracks.add(SpotifyTrack.of(item, this));
        }

        return new BasicAudioPlaylist(
            artist.getName() + "'s Top Tracks", tracks,
            null, false
        );
    }
}
