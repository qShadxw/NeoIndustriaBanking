package uk.co.tmdavies.nibanking.managers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import uk.co.tmdavies.nibanking.NIBanking;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class NNWebSocket implements WebSocket.Listener {
    private WebSocket socket;
    private final String endPoint;
    private final String apiKey;

    public NNWebSocket(String endPoint, String apiKey) {
        this.endPoint = endPoint;
        this.apiKey = apiKey;
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
        /*
        Received: {"event":"auth","valid":true}
        Received: {"event":"migrator","data":{"type":"request","txID":"88","from":"02c0f0725e8f46f8a4004b1722b293f0","to":"795ef1ea3a534c02abb75a62f037440e","amount":"1","reference":"Oi, give me money!"}}
        Received: {"event":"migrator","data":{"type":"approve","txID":88,"from":"02c0f0725e8f46f8a4004b1722b293f0","to":"795ef1ea3a534c02abb75a62f037440e","amount":1,"reference":"Oi, give me money!","approved":false}}
         */
        //NIBanking.LOGGER.info("[NNWebSocket] Received: " + data);

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
            case "approve" -> onMigratorApprove(fromUUID, toUUID, transactionId, data.get("approved").getAsBoolean());
            default -> NIBanking.LOGGER.error("[NNWebSocket] Invalid type from Migrator. [{}]", type);
        }
    }

    public void onWSSError(WebSocket webSocket, JsonObject response) {
        NIBanking.LOGGER.error("[NNWebSocket] WSS encountered an error. {}", response.toString());
    }

    public void onMigratorRequest(String fromUUID, String toUUID, String transactionId, int amount, String reference) {
        NIBanking.LOGGER.info("[NNWebSocket] Request called. {} {} {} {} {}", fromUUID, toUUID, transactionId, amount, reference);
    }

    public void onMigratorApprove(String fromUUID, String toUUID, String transactionId, boolean approved) {
        NIBanking.LOGGER.info("[NNWebSocket] Approve called. {} {} {} {}", fromUUID, toUUID, transactionId, approved);
    }
}
