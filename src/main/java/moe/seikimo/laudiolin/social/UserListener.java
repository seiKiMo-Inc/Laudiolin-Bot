package moe.seikimo.laudiolin.social;

import moe.seikimo.laudiolin.Laudiolin;
import moe.seikimo.laudiolin.gateway.GatewayMessage;
import moe.seikimo.laudiolin.gateway.Objects.BasicUser;
import moe.seikimo.laudiolin.gateway.UserState;
import net.dv8tion.jda.api.events.user.update.UserUpdateOnlineStatusEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

/**
 * Discord user update listener.
 */
public final class UserListener extends ListenerAdapter {
    @Override
    public void onUserUpdateOnlineStatus(
        @NotNull UserUpdateOnlineStatusEvent event
    ) {
        // Send the user update message.
        Laudiolin.getClient().send(GatewayMessage.DiscordUserUpdateMessage.builder()
            .type("user-update").timestamp(System.currentTimeMillis())
            .userData(BasicUser.from(event.getUser()))
            .status(UserState.from(event.getMember()))
            .build());
        // Log the user update message.
        Laudiolin.getLogger().info("Updated user " + event.getUser().getName() + "#" + event.getUser().getDiscriminator() + ".");
    }
}
