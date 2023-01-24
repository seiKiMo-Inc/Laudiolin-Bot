package moe.seikimo.laudiolin.utils;

import moe.seikimo.laudiolin.Laudiolin;
import tech.xigam.cch.utils.Interaction;

/**
 * Validate user permissions.
 */
public interface PermissionUtil {
    /**
     * Validates an interaction to see if it was sent in a server.
     * @param interaction The message interaction.
     * @return True if the command can continue executing.
     */
    static boolean isGuild(Interaction interaction) {
        if (!interaction.isFromGuild()) {
            interaction.reply(MessageUtil.NOT_SERVER, false);
            return false;
        }

        return true;
    }

    /**
     * Validates an interaction to see if the user is an administrator.
     * @param interaction The message interaction.
     * @return True if the command can continue executing.
     */
    static boolean isAdministrator(Interaction interaction) {
        // Check if the interaction was in a server.
        if (!interaction.isFromGuild()) {
            interaction.reply(MessageUtil.NOT_SERVER, false);
            return false;
        }

        // Check if the user is an administrator.
        if (!Laudiolin.getConfig().getAdmins()
            .contains(interaction.getUser().getId())) {
            interaction.reply(MessageUtil.NO_PERMISSION, false);
            return false;
        }

        return true;
    }
}
