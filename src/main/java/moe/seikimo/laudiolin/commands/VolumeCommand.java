package moe.seikimo.laudiolin.commands;

import moe.seikimo.laudiolin.audio.LaudiolinAudioManager;
import moe.seikimo.laudiolin.objects.constants.Messages;
import moe.seikimo.laudiolin.objects.enums.MessageType;
import moe.seikimo.laudiolin.utils.PermissionUtil;
import moe.seikimo.laudiolin.utils.VoiceUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import tech.xigam.cch.command.Arguments;
import tech.xigam.cch.command.Command;
import tech.xigam.cch.utils.Argument;
import tech.xigam.cch.utils.Interaction;

import java.util.Collection;
import java.util.List;

public final class VolumeCommand extends Command implements Arguments {
    public VolumeCommand() {
        super("volume", "Change the volume of the audio player.");
    }

    @Override
    public void execute(Interaction interaction) {
        // Check if the interaction was in a server.
        if (!PermissionUtil.isGuild(interaction)) return;

        // Pull parameters.
        var guild = interaction.getGuild(); assert guild != null;
        var member = interaction.getMember(); assert member != null;
        var voiceState = member.getVoiceState(); assert voiceState != null;
        var volume = interaction.getArgument("volume", 100L, Long.class);

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

        // Set the volume of the player.
        audioManager.getAudioPlayer().setVolume(volume.intValue());

        var embed = new EmbedBuilder()
            .setColor(MessageType.INFO.getColor())
            .setDescription("Set the volume to `" + volume + "%`.");
        interaction.reply(embed.build(), false);
    }

    @Override
    public Collection<Argument> getArguments() {
        return List.of(
            Argument.create("volume", "The volume as a percentage.", "volume", OptionType.INTEGER, true, 0)
                .range(0, 150)
        );
    }
}
