package moe.seikimo.laudiolin.gateway;

import lombok.Builder;
import net.dv8tion.jda.api.entities.User;

/**
 * Collection of gateway objects.
 */
public final class Objects {
    @Builder public static class BasicUser {
        public String username;
        public String discriminator;
        public String userId;
        public String avatar;

        /**
         * Creates a basic user from a JDA user.
         * @param user The JDA user.
         * @return A basic user.
         */
        public static BasicUser from(User user) {
            return BasicUser.builder()
                .username(user.getName())
                .discriminator(user.getDiscriminator())
                .userId(user.getId())
                .avatar(user.getAvatarUrl() == null ? "" : user.getAvatarUrl())
                .build();
        }
    }
}
