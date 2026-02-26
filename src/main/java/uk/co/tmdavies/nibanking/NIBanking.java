package uk.co.tmdavies.nibanking;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;
import uk.co.tmdavies.nibanking.items.NIItems;
import uk.co.tmdavies.nibanking.listeners.ServerListener;
import uk.co.tmdavies.nibanking.managers.NNWebSocket;

@Mod(NIBanking.MODID)
public class NIBanking {
    // Systems
    public static final String MODID = "nibanking";
    public static final Logger LOGGER = LogUtils.getLogger();

    // WebSocket
    public static NNWebSocket webSocket;

    public NIBanking(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        NIItems.registerItems(modEventBus);
        NIItems.registerCreativeTab(modEventBus);

        NeoForge.EVENT_BUS.register(new ServerListener());
    }

    public void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("Setting up...");

        webSocket = new NNWebSocket("endPoint.here");
        webSocket.connect();
    }
}
