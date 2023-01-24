package moe.seikimo.laudiolin.commands;

import moe.seikimo.laudiolin.objects.Messages;
import moe.seikimo.laudiolin.utils.MessageUtil;
import moe.seikimo.laudiolin.utils.PermissionUtil;
import moe.seikimo.laudiolin.utils.VoiceUtil;
import net.dv8tion.jda.api.entities.MessageEmbed;
import tech.xigam.cch.command.Command;
import tech.xigam.cch.utils.Interaction;

public final class LeaveCommand extends Command {
    private static final MessageEmbed LEFT_VOICE = MessageUtil.generic(
        "I've left the voice channel.");

    public LeaveCommand() {
        super("leave", "Leave the voice channel.");
    }

    @Override
    public void execute(Interaction interaction) {
        // Check if the interaction was in a server.
        if (!PermissionUtil.isGuild(interaction)) return;

        // Check if the bot is in a voice channel.
        if (!VoiceUtil.isConnected(interaction)) {
            interaction.reply(Messages.BOT_NOT_IN_VOICE, false);
            return;
        }

        // Disconnect from the voice channel.
        VoiceUtil.disconnect(interaction);

        interaction.reply(LEFT_VOICE, false);
    }
}
