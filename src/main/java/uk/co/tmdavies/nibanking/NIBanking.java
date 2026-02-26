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
import uk.co.tmdavies.nibanking.managers.ModManager;

@Mod(NIBanking.MODID)
public class NIBanking {
    // Systems
    public static final String MODID = "nibanking";
    public static final Logger LOGGER = LogUtils.getLogger();

    // Managers
    public static ModManager modManager;

    public NIBanking(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        NIItems.ITEMS.register(modEventBus);
        NIItems.CREATIVE_MODE_TABS.register(modEventBus);

//        modManager = new ModManager(this, modEventBus, modContainer, "uk.co.tmdavies.nibanking.listeners");
//        modManager.registerEvents();

        NeoForge.EVENT_BUS.register(new ServerListener());
    }

    public void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("Setting up...");
    }
}
