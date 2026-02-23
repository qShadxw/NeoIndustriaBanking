package uk.co.tmdavies.nibanking.items;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import uk.co.tmdavies.nibanking.NIBanking;

public class NIItems {
    // Register
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(NIBanking.MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, NIBanking.MODID);
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> NI_TAB = CREATIVE_MODE_TABS.register(NIBanking.MODID + "-creativetab", () ->
            CreativeModeTab
                    .builder()
                    .title(Component.translatable("itemGroup.neoindustriabanking"))
                    .icon(() -> NIItems.NETHERITE_COIN.get().getDefaultInstance())
                    .build()
    );

    // Items
    public static final DeferredItem<Item> COPPER_COIN = ITEMS.registerSimpleItem("copper_coin");
    public static final DeferredItem<Item> GOLD_COIN = ITEMS.registerSimpleItem("gold_coin");
    public static final DeferredItem<Item> IRON_COIN = ITEMS.registerSimpleItem("iron_coin");
    public static final DeferredItem<Item> NETHERITE_COIN = ITEMS.registerSimpleItem("netherite_coin");
    public static final DeferredItem<Item> ZINC_COIN = ITEMS.registerSimpleItem("zinc_coin");
}
