package moe.seikimo.laudiolin.gateway;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import moe.seikimo.laudiolin.Laudiolin;
import moe.seikimo.laudiolin.gateway.Objects.BasicUser;

import java.util.List;

public class GatewayMessage {
    /**
     * Converts this message to a JSON string.
     * @return A JSON string.
     */
    @Override public String toString() {
        return Laudiolin.getGson().toJson(this);
    }

    /**
     * To gateway.
     */
    @Builder @AllArgsConstructor
    public static class InitializationMessage extends GatewayMessage {
        public String type;
        public long timestamp;

        public String token;
    }

    /**
     * To gateway.
     */
    @Builder @AllArgsConstructor
    public static class DiscordLoadUsersMessage extends GatewayMessage {
        public String type;
        public long timestamp;

        public List<BasicUser> users;
    }

    /**
     * To gateway.
     */
    @Builder @AllArgsConstructor
    public static class DiscordUserUpdateMessage extends GatewayMessage {
        public String type;
        public long timestamp;

        @SerializedName("user")
        public BasicUser userData;
        @SerializedName("state")
        public UserState status;
    }
}
