package uk.co.tmdavies.nibanking.items;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import uk.co.tmdavies.nibanking.NIBanking;

public class NICreativeTab {
    private static final DeferredRegister<CreativeModeTab> REGISTER =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, NIBanking.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> NI_CREATIVE_TAB = REGISTER.register(NIBanking.MODID + "-creativetab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.nibanking"))
                    .icon(NIItems.NIBANKING_SYSTEM_ITEMS.get("base_coin")::asStack)
                    .displayItems((param, output) -> {
                        NIItems.NIBANKING_ITEMS.forEach((id, item) -> output.accept(item.asStack()));
                        output.accept(NIItems.NIBANKING_SYSTEM_ITEMS.get("debit_card").asStack());
                    })
                    .build()
            );

    public static void register(IEventBus modEventBus) {
        REGISTER.register(modEventBus);
    }
}
