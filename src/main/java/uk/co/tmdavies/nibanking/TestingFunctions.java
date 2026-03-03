package uk.co.tmdavies.nibanking;

import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.packager.InventorySummary;
import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBehaviour;
import com.simibubi.create.content.logistics.stockTicker.PackageOrder;
import com.simibubi.create.content.logistics.stockTicker.StockTickerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class TestingFunctions {

    public static void bareShop(Level level) {

        NIBanking.LOGGER.info("Mark 4");
        if (level.isClientSide())
            return;
        NIBanking.LOGGER.info("Mark 5");
        if (!(level.getBlockEntity(new BlockPos(23, -60, 17)) instanceof StockTickerBlockEntity tickerBE))
            return;
        NIBanking.LOGGER.info("Mark 6");

        InventorySummary customEntries = new InventorySummary();
        customEntries.add(new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse("minecraft:oak_log")), 1));
        PackageOrder order = new PackageOrder(customEntries.getStacksByCount());

        // Must be up-to-date
        tickerBE.getAccurateSummary();

        // Check stock levels
        InventorySummary recentSummary = tickerBE.getRecentSummary();
        for (BigItemStack entry : order.stacks()) {
            NIBanking.LOGGER.info("Mark 7");
            if (recentSummary.getCountOf(entry.stack) >= entry.count)
                continue;

            NIBanking.LOGGER.error("STOCK TOO LOW");
            return;
        }
        NIBanking.LOGGER.info("Mark 8");
        tickerBE.broadcastPackageRequest(LogisticallyLinkedBehaviour.RequestType.PLAYER, order, null, "");
        if (!order.isEmpty()) {
            NIBanking.LOGGER.info("Mark 9");
        }

    }
}
