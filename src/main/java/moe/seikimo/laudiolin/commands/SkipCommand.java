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

public final class SkipCommand extends Command {
    private static final MessageEmbed SKIPPED = MessageUtil.generic(
        "Skipped the current track.", MessageType.INFO);

    public SkipCommand() {
        super("skip", "Skip the current track.");
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

        // Check if there is something playing.
        if (!audioManager.isPlaying()) {
            interaction.reply(Messages.NOT_PLAYING, false);
            return;
        }

        // Skip the current track.
        audioManager.getScheduler().nextTrack();

        interaction.reply(SKIPPED, false);
    }
}
