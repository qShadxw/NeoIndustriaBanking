package uk.co.tmdavies.nibanking.listeners;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import uk.co.tmdavies.nibanking.NIBanking;

@NIListener
public class ServerListener {

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        NIBanking.LOGGER.info("Server is Starting...");
    }

    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {
        NIBanking.LOGGER.info("Server has started.");
    }
}
