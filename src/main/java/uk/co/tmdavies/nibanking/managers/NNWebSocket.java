package uk.co.tmdavies.nibanking.managers;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import org.codehaus.plexus.util.CachedMap;
import uk.co.tmdavies.nibanking.NIBanking;
import uk.co.tmdavies.nibanking.objects.NNTransaction;
import uk.co.tmdavies.nibanking.utils.Utils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class NNWebSocket implements WebSocket.Listener {

    public final Cache<String, NNTransaction> transactionCache;

    private WebSocket socket;
    private final String endPoint;
    private final String apiKey;
    private final MinecraftServer server;

    public NNWebSocket(String endPoint, String apiKey, MinecraftServer server) {
        this.endPoint = endPoint;
        this.apiKey = apiKey;
        this.transactionCache = CacheBuilder.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(2L))
                .removalListener((removalNotification) ->
                        onTransactionTimeout((String) removalNotification.getKey(), (NNTransaction) removalNotification.getValue()))
                .build();
        this.server = server;
    }

    public NNTransaction getTransactionFromPlayer(Player player) {
        for (Map.Entry<String, NNTransaction> entry : transactionCache.asMap().entrySet()) {
            NNTransaction transaction = entry.getValue();
            if (transaction.fromUUID().equals(player.getUUID().toString())) {
                return entry.getValue();
            }
        }

        return null;
    }

    public void connect() {
        HttpClient client = HttpClient.newHttpClient();

        client.newWebSocketBuilder()
                .buildAsync(URI.create(endPoint), this)
                .thenAccept(ws -> {
                    this.socket = ws;
                    NIBanking.LOGGER.info("[NNWebSocket] WebSocket Authenticating...");
                    send(String.format("{\"command\": \"auth\", \"apikey\": \"%s\"}", this.apiKey));
                    NIBanking.LOGGER.info("[NNWebSocket] WebSocket successfully connected.");
                })
                .exceptionally(exception -> {
                    NIBanking.LOGGER.info("[NNWebSocket] WebSocket failed to connect. {}", exception.getMessage());
                    return null;
                });
    }

    public void send(String message) {
        NIBanking.LOGGER.info(String.valueOf(Objects.isNull(socket)));
        if (this.socket != null) {
            NIBanking.LOGGER.info("[NNWebSocket] Sending: {}", message);
            this.socket.sendText(message, true);
        }
    }

    @Override
    public void onOpen(WebSocket webSocket) {
        webSocket.request(1);
    }

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
        JsonObject response = JsonParser.parseString(String.valueOf(data)).getAsJsonObject();

        switch (response.get("event").getAsString()) {
            case "auth" -> onAuth(webSocket, response);
            case "migrator" -> onMigrator(webSocket, response);
            case "error" -> onWSSError(webSocket, response);
            default -> NIBanking.LOGGER.error("Unknown Event. Response: {}", response.toString());
        }

        webSocket.request(1);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void onError(WebSocket webSocket, Throwable exception) {
        NIBanking.LOGGER.error("[NNWebSocket] WebSocket encountered an error. \n{}", exception.toString());
    }

    public void onAuth(WebSocket webSocket, JsonObject response) {
        boolean isValid = response.get("valid").getAsBoolean();

        if (isValid) {
            NIBanking.LOGGER.info("[NNWebSocket] Successfully authenticated.");
            return;
        }

        NIBanking.LOGGER.error("[NNWebSocket] Failed to authenticate.");
    }

    public void onMigrator(WebSocket webSocket, JsonObject response) {
        JsonObject data = response.get("data").getAsJsonObject();
        String type = data.get("type").getAsString();
        String fromUUID = data.get("from").getAsString();
        String toUUID = data.get("to").getAsString();
        String transactionId = data.get("txID").getAsString();
        int amount = data.get("amount").getAsInt();
        String reference = data.get("reference").getAsString();


        switch (type) {
            case "request" -> onMigratorRequest(fromUUID, toUUID, transactionId, amount, reference);
            case "approve" -> onMigratorApprove(transactionId, data.get("approved").getAsBoolean());
            default -> NIBanking.LOGGER.error("[NNWebSocket] Invalid type from Migrator. [{}]", type);
        }
    }

    public void onWSSError(WebSocket webSocket, JsonObject response) {
        NIBanking.LOGGER.error("[NNWebSocket] WSS encountered an error. {}", response.toString());
    }

    public void onMigratorRequest(String fromUUID, String toUUID, String transactionId, int amount, String reference) {
        NIBanking.LOGGER.info("[NNWebSocket] Request called. {} {} {} {} {}", fromUUID, toUUID, transactionId, amount, reference);
        this.transactionCache.put(transactionId, new NNTransaction(transactionId, toUUID, fromUUID, amount, reference, false));
    }

    public void onMigratorApprove(String transactionId, boolean approved) {
        NIBanking.LOGGER.info("[NNWebSocket] Approve called. {} {}", transactionId, approved);

        NNTransaction transaction = this.transactionCache.getIfPresent(transactionId);

        if (transaction == null) {
            return;
        }

        NIBanking.LOGGER.info("[NNWebSocket] Got transaction: [{}] [{}] [{}]", transaction.transactionId(), transaction.toUUID(), transaction.fromUUID());
    }

    public void onTransactionTimeout(String transactionId, NNTransaction transaction) {
        Player toPlayer = this.server.getPlayerList().getPlayer(UUID.fromString(transaction.toUUID()));
        Player fromPlayer = this.server.getPlayerList().getPlayer(UUID.fromString(transaction.fromUUID()));

        if (toPlayer != null) {
            toPlayer.sendSystemMessage(Utils.Chat("&cTransaction has timed out. Please ask customer to replace the order. [txID: %s]", transactionId));;
        } else {
            NIBanking.LOGGER.error("[TransactionTimeout] toPlayer is null for {}. [{}]", transactionId, transaction.toUUID());
        }

        if (fromPlayer != null) {
            fromPlayer.sendSystemMessage(Utils.Chat("&cTransaction has timed out. Please replace your order if you wish to purchase. [txID: %s]", transactionId));
        } else {
            NIBanking.LOGGER.error("[TransactionTimeout] fromPlayer is null for {}. [{}]", transactionId, transaction.fromUUID());
        }
    }
}
