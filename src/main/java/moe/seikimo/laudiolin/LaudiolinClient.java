package moe.seikimo.laudiolin;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import lombok.SneakyThrows;
import moe.seikimo.laudiolin.gateway.GatewayMessage;
import moe.seikimo.laudiolin.gateway.GatewayMessage.DiscordLoadUsersMessage;
import moe.seikimo.laudiolin.gateway.GatewayMessage.InitializationMessage;
import moe.seikimo.laudiolin.gateway.Objects;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.ConnectException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 * Websocket client for the Laudiolin backend.
 */
public final class LaudiolinClient extends WebSocketClient {
    public LaudiolinClient(boolean encrypted, String backend) throws URISyntaxException {
        super(new URI(
            (encrypted ? "wss" : "ws") + "://" + backend
        ));
    }

    /**
     * Sends a serialized message to the gateway.
     * @param message The message to send.
     */
    public void send(GatewayMessage message) {
        if (!this.isOpen()) return;
        super.send(message.toString());
    }

    /**
     * Handles latency messages.
     */
    private void latency() {
        var response = new JsonObject();
        response.add("type", new JsonPrimitive("latency"));
        this.send(response.toString());
    }

    /**
     * Initializes with the gateway.
     */
    private void initialize() {
        // Send the initialization message.
        this.send(InitializationMessage.builder()
            .type("initialize").timestamp(System.currentTimeMillis())
            .token(Laudiolin.getConfig().getToken()).build());
        Laudiolin.getLogger().info("Completed gateway handshake.");
    }

    /**
     * Fetches all members with the bot.
     */
    private void fetchMembers() {
        var instance = Laudiolin.getInstance();
        var users = new ArrayList<Objects.BasicUser>();

        // For-each all guilds.
        for (var guild : instance.getGuilds()) {
            // For-each all members.
            for (var member : guild.getMembers()) {
                // Send the user update message.
                users.add(Objects.BasicUser.from(member.getUser()));
            }
        }

        // Send the user update message.
        Laudiolin.getClient().send(DiscordLoadUsersMessage.builder()
            .type("load-users").timestamp(System.currentTimeMillis())
            .users(users).build());
        // Log the user update message.
        Laudiolin.getLogger().info("Loaded " + users.size() + " users.");
    }

    @Override
    public void onOpen(ServerHandshake handshake) {

    }

    @Override @SneakyThrows
    public void onMessage(String data) {
        // De-serialize the message and process it.
        var message = Laudiolin.getGson().fromJson(data, JsonObject.class);
        var messageType = message.get("type").getAsString();
        switch (messageType) {
            default -> throw new Exception("Invalid message type: " + messageType);
            case "latency" -> this.latency();
            case "initialize" -> this.initialize();
            case "fetch" -> this.fetchMembers();
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        if (code != -1) {
            Laudiolin.getLogger().warn("Disconnected from the backend gateway.");
            Laudiolin.getLogger().info("Disconnected for: " + reason + " (" + code + ")");
        }

        // Attempt to reconnect.
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                this.reconnect();
                Laudiolin.getLogger().info("Attempting to reconnect...");
            } catch (InterruptedException ignored) { }
        }).start();
    }

    @Override
    public void onError(Exception ex) {
        if (!(ex instanceof ConnectException))
            Laudiolin.getLogger().warn("Client exception occurred.", ex);
    }
}
