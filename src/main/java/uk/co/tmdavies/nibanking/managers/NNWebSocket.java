package uk.co.tmdavies.nibanking.managers;

import uk.co.tmdavies.nibanking.NIBanking;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class NNWebSocket implements WebSocket.Listener {
    private WebSocket socket;
    private final String endPoint;

    public NNWebSocket(String endPoint) {
        this.endPoint = endPoint;
    }

    public void connect() {
        HttpClient client = HttpClient.newHttpClient();

        client.newWebSocketBuilder()
                .buildAsync(URI.create(endPoint), this)
                .thenAccept(ws -> {
                    this.socket = ws;
                    NIBanking.LOGGER.info("[NNWebSocket] WebSocket sucessfully connected.");
                })
                .exceptionally(exception -> {
                    NIBanking.LOGGER.info("[NNWebSocket] WebSocket failed to connect. {}", exception.getMessage());
                    return null;
                });
    }

    public void send(String message) {
        if (socket != null) {
            socket.sendText(message, true);
        }
    }

    @Override
    public void onOpen(WebSocket webSocket) {
        NIBanking.LOGGER.info("[NNWebSocket] WebSocket open and waiting.");
        webSocket.request(1);
    }

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
        NIBanking.LOGGER.info("[NNWebSocket] Received: " + data);
        webSocket.request(1);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void onError(WebSocket webSocket, Throwable exception) {
        NIBanking.LOGGER.error("[NNWebSocket] WebSocket encountered an error: {}", exception.getMessage());
    }
}
