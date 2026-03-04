package uk.co.tmdavies.nibanking.listeners;

import com.simibubi.create.content.logistics.packager.InventorySummary;
import com.simibubi.create.content.logistics.tableCloth.ShoppingListItem;
import net.createmod.catnip.data.Couple;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import uk.co.tmdavies.nibanking.NIBanking;
import uk.co.tmdavies.nibanking.commands.MainCommand;
import uk.co.tmdavies.nibanking.files.ConfigFile;
import uk.co.tmdavies.nibanking.managers.NNWebSocket;
import uk.co.tmdavies.nibanking.managers.NeoNetworkIRS;
import uk.co.tmdavies.nibanking.objects.NNTransaction;
import uk.co.tmdavies.nibanking.utils.TransactionHelper;
import uk.co.tmdavies.nibanking.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@NIListener
public class ServerListener {

    public int timer = 0;

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        NIBanking.LOGGER.info("Server is Starting...");

        // Files
        NIBanking.connectionsFile = new ConfigFile("connections");
        NIBanking.connectionsFile.loadConfig();

        // API
        NIBanking.neoNetworkIRS = new NeoNetworkIRS(NIBanking.connectionsFile.get("NeoNetworkIRS").getAsJsonObject().get("url").getAsString(),
                NIBanking.connectionsFile.get("NeoNetworkIRS").getAsJsonObject().get("apikey").getAsString());
        NIBanking.webSocket = new NNWebSocket(NIBanking.connectionsFile.get("WebSocket").getAsJsonObject().get("url").getAsString(),
                NIBanking.connectionsFile.get("WebSocket").getAsJsonObject().get("apikey").getAsString(),
                event.getServer());

    }

    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {
        NIBanking.LOGGER.info("Server has started.");
        NIBanking.webSocket.connect();
    }

    @SubscribeEvent
    public void onServerTick(ServerTickEvent.Post event) {
        timer++;
        if (timer == 40) {
            if (NIBanking.webSocket.transactionsList.isEmpty()) {
                timer = 0;
                return;
            }

            List<NNTransaction> transactionToRemove = new ArrayList<>();

            for (NNTransaction transaction : NIBanking.webSocket.transactionsList) {
                if (transaction == null) {
                    continue;
                }

                if (!transaction.isComplete()) {
                    continue;
                }

                if (transaction.getLevel() == null) {
                    NIBanking.LOGGER.error("Level is null");
                    transactionToRemove.add(transaction);
                    continue;
                }

                if (transaction.getTickerBE() == null) {
                    NIBanking.LOGGER.error("TickerBE is null");
                    transactionToRemove.add(transaction);
                    continue;
                }

                if (transaction.getShoppingListCopy() == null) {
                    NIBanking.LOGGER.error("ShoppingListCopy is null");
                    transactionToRemove.add(transaction);
                    return;
                }

                ShoppingListItem.ShoppingList list = ShoppingListItem.getList(transaction.getShoppingListCopy());

                if (list == null) {
                    NIBanking.LOGGER.error("List is null");
                    return;
                }

                Couple<InventorySummary> bakeEntries = list.bakeEntries(transaction.getLevel(), null);

                TransactionHelper.handleShop(transaction.getLevel(), transaction.getTickerBE(), bakeEntries, transaction.getLevel().getServer().getPlayerList().getPlayer(Utils.reconstructUUID(transaction.getFromUUID())));
                transactionToRemove.add(transaction);
            }

            transactionToRemove.forEach(transaction -> NIBanking.webSocket.removeTransaction(transaction));

            timer = 0;
        }
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        MainCommand.register(event.getDispatcher());
    }
}