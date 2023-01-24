package moe.seikimo.laudiolin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.xigam.cch.ComplexCommandHandler;

import java.io.FileReader;
import java.io.IOException;
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
                .enableCache(CacheFlag.VOICE_STATE)
                .setHttpClient(Laudiolin.getHttp())
                .build();

            // Create a command handler instance.
            commandHandler = new ComplexCommandHandler(true)
                .setPrefix(config.getPrefix());
            commandHandler.setJda(Laudiolin.getInstance());

            logger.info("Laudiolin is ready!");
        } catch (IOException ignored) {
            logger.error("Failed to load the configuration file.");
        } catch (SecurityException ignored) {
            logger.error("Failed to connect to the Discord API.");
        }
    }
}
