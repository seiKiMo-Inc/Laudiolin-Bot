package moe.seikimo.laudiolin.commands;

import moe.seikimo.laudiolin.objects.Messages;
import moe.seikimo.laudiolin.utils.MessageUtil;
import moe.seikimo.laudiolin.utils.PermissionUtil;
import moe.seikimo.laudiolin.utils.VoiceUtil;
import net.dv8tion.jda.api.entities.MessageEmbed;
import tech.xigam.cch.command.Command;
import tech.xigam.cch.utils.Interaction;

public final class JoinCommand extends Command {
    private static final MessageEmbed JOINED_VOICE = MessageUtil.generic(
        "I've joined your voice channel.");

    public JoinCommand() {
        super("join", "Join your voice channel.");
    }

    @Override
    public void execute(Interaction interaction) {
        // Check if the interaction was in a server.
        if (!PermissionUtil.isGuild(interaction)) return;

        // Pull parameters.
        var member = interaction.getMember(); assert member != null;
        var voiceState = member.getVoiceState(); assert voiceState != null;

        // Check if the member is in a voice channel.
        if (!voiceState.inAudioChannel()) {
            interaction.reply(Messages.USER_NOT_IN_VOICE, false);
            return;
        }

        // Check if the bot is in a voice channel.
        if (VoiceUtil.isConnected(interaction)) {
            interaction.reply(Messages.ALREADY_IN_VOICE, false);
            return;
        }

        // Connect to the voice channel.
        var channel = voiceState.getChannel(); assert channel != null;
        VoiceUtil.connectTo(channel);

        interaction.reply(JOINED_VOICE, false);
    }
}
