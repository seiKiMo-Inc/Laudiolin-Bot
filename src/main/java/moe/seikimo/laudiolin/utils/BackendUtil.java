package moe.seikimo.laudiolin.utils;

import moe.seikimo.laudiolin.Laudiolin;
import moe.seikimo.laudiolin.objects.LogEvent;
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
}
