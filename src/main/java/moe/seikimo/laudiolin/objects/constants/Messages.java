package moe.seikimo.laudiolin.objects.constants;

import moe.seikimo.laudiolin.objects.enums.MessageType;
import moe.seikimo.laudiolin.utils.MessageUtil;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.MessageEmbed;

/**
 * Pre-made messages.
 */
public interface Messages {
    Activity ACTIVITY = Activity.listening("amazing music");

    MessageEmbed NO_ARGUMENTS = MessageUtil.generic(
        "You must provide arguments for this command.", MessageType.ERROR);

    MessageEmbed USER_NOT_IN_VOICE = MessageUtil.generic(
        "You are not in a voice channel.", MessageType.ERROR);
    MessageEmbed BOT_NOT_IN_VOICE = MessageUtil.generic(
        "I am not in a voice channel.", MessageType.ERROR);
    MessageEmbed ALREADY_IN_VOICE = MessageUtil.generic(
        "I am already in a voice channel.", MessageType.ERROR);

    MessageEmbed NO_RESULTS_FOUND = MessageUtil.generic(
        "Unable to find results for that query.", MessageType.ERROR);
    MessageEmbed UNABLE_TO_PLAY = MessageUtil.generic(
        "Unable to play the requested track.", MessageType.ERROR);

    MessageEmbed NOT_PLAYING = MessageUtil.generic(
        "Nothing is currently playing.", MessageType.ERROR);
    MessageEmbed QUEUE_EMPTY = MessageUtil.generic(
        "The queue is empty.", MessageType.ERROR);
}
