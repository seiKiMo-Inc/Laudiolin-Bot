package moe.seikimo.laudiolin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import moe.seikimo.laudiolin.audio.LaudiolinAudioManager;
import moe.seikimo.laudiolin.commands.*;
import moe.seikimo.laudiolin.objects.constants.Messages;
import moe.seikimo.laudiolin.social.UserListener;
import moe.seikimo.laudiolin.utils.BackendUtil;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.xigam.cch.ComplexCommandHandler;

import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.EnumSet;

public final class Laudiolin {
    @Getter private static final Logger logger =
        LoggerFactory.getLogger("Laudiolin");
    @Getter private static final Gson gson =
        new GsonBuilder().setPrettyPrinting().create();
    @Getter private static final OkHttpClient http
        = new OkHttpClient();

    @Getter private static ComplexCommandHandler commandHandler;
    @Getter private static LaudiolinConfig config;
    @Getter private static LaudiolinClient client;
    @Getter private static JDA instance;

    public static void main(String[] args) {
        // Check for a configuration file.
        if (args.length == 0) {
            logger.error("No configuration specified.");
            return;
        }

        try {
            // Load the configuration file.
            config = gson.fromJson(new FileReader(args[0]), LaudiolinConfig.class);

            // Create a bot instance.
            instance = JDABuilder.createDefault(config.getToken(),
                EnumSet.allOf(GatewayIntent.class))
                .enableCache(
                    CacheFlag.VOICE_STATE,
                    CacheFlag.ONLINE_STATUS)
                .addEventListeners(
                    new UserListener())
                .setMemberCachePolicy(
                    MemberCachePolicy.ALL)
                .setChunkingFilter(
                    ChunkingFilter.ALL)
                .setHttpClient(Laudiolin.getHttp())
                .setActivity(Messages.ACTIVITY)
                .setStatus(OnlineStatus.ONLINE)
                .setAutoReconnect(true)
                .setIdle(false)
                .build();

            // Create a command handler instance.
            commandHandler = new ComplexCommandHandler(true)
                .setPrefix(config.getPrefix());
            Laudiolin.registerAllCommands(commandHandler);
            commandHandler.setJda(Laudiolin.getInstance());

            logger.info("Laudiolin is ready!");

            // Create a Laudiolin client.
            client = new LaudiolinClient(
                config.isEncrypted(),
                config.getEndpoint().split("://")[1]
            );

            // Ping the backend.
            if (!BackendUtil.pingBackend()) {
                logger.error("Unable to ping the Laudiolin backend.");
            } else {
                // Connect to the backend.
                client.connect();
            }

            // Initialize the audio manager.
            LaudiolinAudioManager.initialize();
        } catch (IOException ignored) {
            logger.error("Failed to load the configuration file.");
        } catch (SecurityException ignored) {
            logger.error("Failed to connect to the Discord API.");
        } catch (URISyntaxException ignored) {
            logger.error("Invalid gateway set.");
        }
    }

    /**
     * Registers all known commands.
     * @param handler The command handler.
     */
    private static void registerAllCommands(ComplexCommandHandler handler) {
        handler
            .registerCommand(new DeployCommand())
            .registerCommand(new VolumeCommand())
            .registerCommand(new LeaveCommand())
            .registerCommand(new JoinCommand())
            .registerCommand(new PlayCommand())
            .registerCommand(new SkipCommand())
            .registerCommand(new StopCommand());

        // Set the handler for argument errors.
        handler.onArgumentError = interaction ->
            interaction.reply(Messages.NO_ARGUMENTS, false);
    }
}
