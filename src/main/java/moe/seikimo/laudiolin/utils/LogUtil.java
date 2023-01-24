package moe.seikimo.laudiolin.utils;

import moe.seikimo.laudiolin.Laudiolin;
import moe.seikimo.laudiolin.objects.LogEvent;
import tech.xigam.cch.utils.Interaction;

/**
 * Handle logging events.
 */
public interface LogUtil {
    /**
     * Logs an event to the console.
     * @param event The event to log.
     * @param args The arguments to pass to the event.
     */
    static void log(LogEvent event, Object... args) {
        var logger = Laudiolin.getLogger();

        switch (event) {
            case DEPLOY_COMMANDS -> {
                var interaction = (Interaction) args[0];
                logger.info(event.getTemplate().formatted(
                    interaction.getUser().getId(), args[1], args[2]
                ));
            }
        }
    }
}
