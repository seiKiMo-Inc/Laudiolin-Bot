package moe.seikimo.laudiolin.utils;

import moe.seikimo.laudiolin.Laudiolin;
import moe.seikimo.laudiolin.objects.LaudiolinPlaylist;
import moe.seikimo.laudiolin.objects.LaudiolinSearchResults;
import moe.seikimo.laudiolin.objects.LaudiolinTrackInfo;
import moe.seikimo.laudiolin.objects.enums.LogEvent;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.io.IOException;

/**
 * Methods to interface with the Laudiolin backend.
 */
public interface BackendUtil {
    /* HTTP client for this utility. */
    String ENDPOINT = Laudiolin.getConfig().getEndpoint();
    OkHttpClient CLIENT = Laudiolin.getHttp();

    /**
     * Check if the backend is alive.
     * @return True if the backend is responding.
     */
    static boolean pingBackend() {
        var request = new Request.Builder()
            .url(ENDPOINT + "/")
            .build();

        try (var response = CLIENT.newCall(request).execute()) {
            return response.code() == 404;
        } catch (IOException ignored) {
            LogUtil.log(LogEvent.BACKEND_PING);
        }

        return false;
    }

    /**
     * Fetches information about a track.
     * @param trackId The ID of the track.
     * @return The track information.
     */
    static LaudiolinTrackInfo fetch(String trackId) {
        var request = new Request.Builder()
            .url(ENDPOINT + "/fetch/" + trackId)
            .build();

        try (var response = CLIENT.newCall(request).execute()) {
            var body = response.body();
            if (body == null)
                throw new IOException("No response body.");

            return Laudiolin.getGson().fromJson(body.string(), LaudiolinTrackInfo.class);
        } catch (IOException ignored) {
            LogUtil.log(LogEvent.BACKEND_QUERY_ERROR);
        }

        return null;
    }

    /**
     * Fetches a Laudiolin playlist.
     *
     * @param playlistId The ID of the playlist.
     * @return The playlist.
     */
    static LaudiolinPlaylist fetchPlaylist(String playlistId) {
        var request = new Request.Builder()
            .url(ENDPOINT + "/playlist/" + playlistId)
            .build();

        try (var response = CLIENT.newCall(request).execute()) {
            var body = response.body();
            if (body == null)
                throw new IOException("No response body.");

            return Laudiolin.getGson().fromJson(body.string(), LaudiolinPlaylist.class);
        } catch (IOException ignored) {
            LogUtil.log(LogEvent.BACKEND_QUERY_ERROR);
        }

        return null;
    }

    /**
     * Performs a search for the specified track.
     * @param query The query to search for.
     * @return The search results.
     */
    static LaudiolinSearchResults search(String query) {
        var request = new Request.Builder()
            .url(ENDPOINT + "/search/" + query + "?engine=All")
            .build();

        try (var response = CLIENT.newCall(request).execute()) {
            var body = response.body();
            if (body == null)
                throw new IOException("No response body.");

            return Laudiolin.getGson().fromJson(body.string(), LaudiolinSearchResults.class);
        } catch (IOException ignored) {
            LogUtil.log(LogEvent.BACKEND_PING);
        }

        return null;
    }
}
