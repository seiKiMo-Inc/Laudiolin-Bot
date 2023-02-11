package moe.seikimo.laudiolin.gateway;

import com.google.gson.annotations.SerializedName;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Member;

/**
 * The state of the user.
 */
public enum UserState {
    @SerializedName("online") ONLINE,
    @SerializedName("offline") OFFLINE;

    /**
     * Returns an appropriate user state from a JDA user.
     * @param user The JDA guild member.
     * @return An appropriate user state.
     */
    public static UserState from(Member user) {
        return user.getOnlineStatus() == OnlineStatus.OFFLINE ? OFFLINE : ONLINE;
    }
}
