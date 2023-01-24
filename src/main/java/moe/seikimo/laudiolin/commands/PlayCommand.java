package moe.seikimo.laudiolin.commands;

import moe.seikimo.laudiolin.objects.Messages;
import moe.seikimo.laudiolin.utils.AudioUtil;
import moe.seikimo.laudiolin.utils.PermissionUtil;
import moe.seikimo.laudiolin.utils.VoiceUtil;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import tech.xigam.cch.command.Arguments;
import tech.xigam.cch.command.Command;
import tech.xigam.cch.utils.Argument;
import tech.xigam.cch.utils.Interaction;

import java.util.Collection;
import java.util.List;

public final class PlayCommand extends Command implements Arguments {
    public PlayCommand() {
        super("play", "Play a song.");
    }

    @Override
    public void execute(Interaction interaction) {
        // Check if the interaction was in a server.
        if (!PermissionUtil.isGuild(interaction)) return;

        // Pull parameters.
        var member = interaction.getMember(); assert member != null;

        // Check if the bot is in a voice channel.
        if (!VoiceUtil.isConnected(interaction)) {
            // Try to connect to the user's voice channel.
            var voiceState = member.getVoiceState(); assert voiceState != null;

            // Check if the member is in a voice channel.
            if (!voiceState.inAudioChannel()) {
                interaction.reply(Messages.USER_NOT_IN_VOICE, false);
                return;
            }

            // Connect to the voice channel.
            var channel = voiceState.getChannel(); assert channel != null;
            VoiceUtil.connectTo(channel);
        }

        // Play the track.
        interaction.deferReply();
        AudioUtil.play(interaction);
    }

    @Override
    public Collection<Argument> getArguments() {
        return List.of(
            Argument.createTrailingArgument("query", "The song to play (or search for).", "query", OptionType.STRING, true, 0)
        );
    }
}
