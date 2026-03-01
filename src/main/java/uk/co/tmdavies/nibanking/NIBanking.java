package uk.co.tmdavies.nibanking;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;
import uk.co.tmdavies.nibanking.files.ConfigFile;
import uk.co.tmdavies.nibanking.items.NICreativeTab;
import uk.co.tmdavies.nibanking.items.NIItems;
import uk.co.tmdavies.nibanking.listeners.ServerListener;
import uk.co.tmdavies.nibanking.managers.NNWebSocket;
import uk.co.tmdavies.nibanking.managers.NeoNetworkIRS;

@Mod(NIBanking.MODID)
public class NIBanking {
    // Systems
    public static final String MODID = "nibanking";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final NIRegistrate REGISTRATE = NIRegistrate.create(MODID)
            .defaultCreativeTab((ResourceKey<CreativeModeTab>) null);

    // Files
    public static ConfigFile connectionsFile;
    public static ConfigFile configFile;

    // API
    public static NNWebSocket webSocket;
    public static NeoNetworkIRS neoNetworkIRS;

    public NIBanking(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        REGISTRATE.registerEventListeners(modEventBus);
        NIItems.register();
        NICreativeTab.register(modEventBus);

        NeoForge.EVENT_BUS.register(new ServerListener());
    }

    public void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("Setting up...");
    }
}
