package moe.seikimo.laudiolin.commands;

import moe.seikimo.laudiolin.objects.enums.LogEvent;
import moe.seikimo.laudiolin.utils.LogUtil;
import moe.seikimo.laudiolin.utils.MessageUtil;
import moe.seikimo.laudiolin.utils.PermissionUtil;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import tech.xigam.cch.command.Arguments;
import tech.xigam.cch.command.Command;
import tech.xigam.cch.utils.Argument;
import tech.xigam.cch.utils.Interaction;

import java.util.Collection;
import java.util.List;

/**
 * Deploys all slash-commands.
 */
public final class DeployCommand extends Command implements Arguments {
    public DeployCommand() {
        super("deploy", "Deploy slash-commands.");
    }

    @Override
    public void execute(Interaction interaction) {
        // Check for permission.
        if (!PermissionUtil.isAdministrator(interaction)) return;

        // Pull arguments.
        var type = interaction.getArgument("type", "guild", String.class);
        var downsert = interaction.getArgument("downsert", false, Boolean.class);
        var message = (downsert ? "Un-registered" : "Registered") + " all commands";

        switch (type) {
            default -> interaction.reply(MessageUtil.UNKNOWN_ARGUMENT, false);

            case "guild" -> {
                var guild = interaction.getGuild();
                if (guild == null) {
                    interaction.reply(MessageUtil.NOT_SERVER, false);
                    return;
                }

                if (downsert) {
                    interaction.getCommandHandler().downsert(guild);
                } else {
                    interaction.getCommandHandler().deployAll(guild);
                }

                interaction.reply(MessageUtil.generic(message + " in this server."), false);
                LogUtil.log(LogEvent.DEPLOY_COMMANDS, interaction,
                    downsert ? "removed" : "added",
                    "in server " + guild.getName());
            }

            case "global" -> {
                if (downsert) {
                    interaction.getCommandHandler().downsert(null);
                } else {
                    interaction.getCommandHandler().deployAll(null);
                }

                interaction.reply(MessageUtil.generic(message + " globally."), false);
            }
        }
    }

    @Override
    public Collection<Argument> getArguments() {
        return List.of(
            Argument.createWithChoices("type", "The type of deployment.", "type", OptionType.STRING, true, 0, "global", "guild"),
            Argument.createWithChoices("downsert", "Should the commands be un-registered?", "downsert", OptionType.BOOLEAN, false, 1, "true", "false")
        );
    }
}
