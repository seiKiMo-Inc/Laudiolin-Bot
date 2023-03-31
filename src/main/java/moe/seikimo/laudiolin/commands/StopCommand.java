package moe.seikimo.laudiolin.commands;

import moe.seikimo.laudiolin.audio.LaudiolinAudioManager;
import moe.seikimo.laudiolin.objects.constants.Messages;
import moe.seikimo.laudiolin.objects.enums.MessageType;
import moe.seikimo.laudiolin.utils.MessageUtil;
import moe.seikimo.laudiolin.utils.PermissionUtil;
import moe.seikimo.laudiolin.utils.VoiceUtil;
import net.dv8tion.jda.api.entities.MessageEmbed;
import tech.xigam.cch.command.Command;
import tech.xigam.cch.utils.Interaction;

public final class StopCommand extends Command {
    private static final MessageEmbed STOPPED_QUEUE = MessageUtil.generic(
        "The queue has been cleared.", MessageType.INFO);

    public StopCommand() {
        super("stop", "Clear the queue, stop playing, and leave.");
    }

    @Override
    public void execute(Interaction interaction) {
        // Check if the interaction was in a server.
        if (!PermissionUtil.isGuild(interaction)) return;

        // Pull parameters.
        var guild = interaction.getGuild(); assert guild != null;
        var member = interaction.getMember(); assert member != null;
        var voiceState = member.getVoiceState(); assert voiceState != null;

        // Check if the member is in a voice channel.
        if (!voiceState.inAudioChannel()) {
            interaction.reply(Messages.USER_NOT_IN_VOICE, false);
            return;
        }

        // Check if the bot is in a voice channel.
        if (!VoiceUtil.isConnected(interaction)) {
            interaction.reply(Messages.BOT_NOT_IN_VOICE, false);
            return;
        }

        // Get the audio manager for the guild.
        var audioManager = LaudiolinAudioManager
            .getInstance().getAudioManager(guild);

        // Check if there is something in the queue.
        if (!audioManager.hasQueue()) {
            interaction.reply(Messages.QUEUE_EMPTY, false);
            return;
        }

        // Clear the queue.
        audioManager.getScheduler().clear();
        audioManager.getAudioPlayer().destroy();
        // Remove the audio manager from the map.
        LaudiolinAudioManager.getInstance().removeAudioManager(guild);
        // Disconnect from the voice channel.
        VoiceUtil.disconnect(interaction);

        interaction.reply(STOPPED_QUEUE, false);
    }
}
