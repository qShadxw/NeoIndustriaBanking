package uk.co.tmdavies.nibanking.items;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import uk.co.tmdavies.nibanking.NIBanking;

import java.util.HashMap;

public class NIItems {
    // Register
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(NIBanking.MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, NIBanking.MODID);
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> NI_TAB = CREATIVE_MODE_TABS.register(NIBanking.MODID + "_tab", () ->
            CreativeModeTab
                    .builder()
                    .title(Component.translatable("itemGroup.nibanking"))
                    .icon(() -> NIItems.NIBANKING_ITEMS.get("iron_coin").get().getDefaultInstance())
                    .displayItems(((itemDisplayParameters, output) -> NIItems.NIBANKING_ITEMS.forEach((itemId, item) -> output.accept(item))))
                    .build()
    );

    // Items
    public static final HashMap<String, DeferredItem<Item>> NIBANKING_ITEMS = new HashMap<>() {{
        put("copper_coin", ITEMS.registerSimpleItem("copper_coin"));
        put("iron_coin", ITEMS.registerSimpleItem("iron_coin"));
        put("gold_coin", ITEMS.registerSimpleItem("gold_coin"));
        put("netherite_coin", ITEMS.registerSimpleItem("netherite_coin"));
        put("zinc_coin", ITEMS.registerSimpleItem("zinc_coin"));
        put("debit_card", ITEMS.registerSimpleItem("debit_card"));
    }};

    public static void registerItems(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }

    public static void registerCreativeTab(IEventBus modEventBus) {
        CREATIVE_MODE_TABS.register(modEventBus);
    }
}
