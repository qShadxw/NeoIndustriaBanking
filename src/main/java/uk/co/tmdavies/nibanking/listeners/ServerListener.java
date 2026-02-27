package uk.co.tmdavies.nibanking.listeners;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import uk.co.tmdavies.nibanking.NIBanking;
import uk.co.tmdavies.nibanking.commands.MainCommand;
import uk.co.tmdavies.nibanking.files.ConfigFile;
import uk.co.tmdavies.nibanking.managers.NNWebSocket;
import uk.co.tmdavies.nibanking.managers.NeoNetworkIRS;

@NIListener
public class ServerListener {

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        NIBanking.LOGGER.info("Server is Starting...");

        // Files
        NIBanking.configFile = new ConfigFile("config");
        NIBanking.configFile.loadConfig();

        NIBanking.connectionsFile = new ConfigFile("connections");
        NIBanking.connectionsFile.loadConfig();

        // API
        NIBanking.neoNetworkIRS = new NeoNetworkIRS(NIBanking.connectionsFile.get("NeoNetworkIRS").getAsJsonObject().get("apikey").getAsString());
        NIBanking.webSocket = new NNWebSocket(NIBanking.connectionsFile.get("WebSocket").getAsJsonObject().get("url").getAsString(), NIBanking.connectionsFile.get("WebSocket").getAsJsonObject().get("apikey").getAsString());
    }

    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {
        NIBanking.LOGGER.info("Server has started.");
        NIBanking.webSocket.connect();
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        MainCommand.register(event.getDispatcher());
    }
}
