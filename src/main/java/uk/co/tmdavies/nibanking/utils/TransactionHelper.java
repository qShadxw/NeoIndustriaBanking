package uk.co.tmdavies.nibanking.utils;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.packager.InventorySummary;
import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBehaviour;
import com.simibubi.create.content.logistics.stockTicker.PackageOrder;
import com.simibubi.create.content.logistics.stockTicker.StockTickerBlockEntity;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.data.Couple;
import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class TransactionHelper {

    public static void handleShop(Level level, StockTickerBlockEntity tickerBE, Couple<InventorySummary> bakeEntries, Player player) {
        if (level.isClientSide()) {
            return;
        }

        InventorySummary orderEntries = bakeEntries.getFirst();
        PackageOrder order = new PackageOrder(orderEntries.getStacksByCount());

        // Must be up-to-date
        tickerBE.getAccurateSummary();

        // Check stock levels
        InventorySummary recentSummary = tickerBE.getRecentSummary();

        for (BigItemStack entry : order.stacks()) {
            if (recentSummary.getCountOf(entry.stack) >= entry.count)
                continue;

            if (player == null) {
                AllSoundEvents.DENY.playOnServer(level, tickerBE.getBlockPos());
            } else {
                AllSoundEvents.DENY.playOnServer(level, player.blockPosition());
                CreateLang.translate("stock_keeper.stock_level_too_low")
                        .style(ChatFormatting.RED)
                        .sendStatus(player);
            }
            return;
        }

        tickerBE.broadcastPackageRequest(LogisticallyLinkedBehaviour.RequestType.PLAYER, order, null, "");

        if (!order.isEmpty()) {
            AllSoundEvents.STOCK_TICKER_TRADE.playOnServer(level, tickerBE.getBlockPos());
        }

    }
}
