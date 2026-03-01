package uk.co.tmdavies.nibanking.mixins;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.packager.InventorySummary;
import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBehaviour;
import com.simibubi.create.content.logistics.stockTicker.PackageOrder;
import com.simibubi.create.content.logistics.stockTicker.StockTickerBlockEntity;
import com.simibubi.create.content.logistics.tableCloth.ShoppingListItem;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.data.Couple;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import uk.co.tmdavies.nibanking.NIBanking;
import uk.co.tmdavies.nibanking.items.NIItems;
import uk.co.tmdavies.nibanking.objects.NNTransaction;
import uk.co.tmdavies.nibanking.utils.CurrencyHelper;

@Mixin(com.simibubi.create.content.logistics.stockTicker.StockTickerInteractionHandler.class)
public class StockTickerInteractionHandlerMixin {

    @Inject(
            method = "interactWithShop",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void nibanking$interactWithShop(Player player, Level level, BlockPos targetPos, ItemStack mainHandItem, CallbackInfo ci) {
        if (level.isClientSide()) {
            return;
        }

        if (!(level.getBlockEntity(targetPos) instanceof StockTickerBlockEntity tickerBE)) {
            return;
        }

        ShoppingListItem.ShoppingList list = ShoppingListItem.getList(mainHandItem);

        if (list == null) {
            return;
        }

        if (!tickerBE.behaviour.freqId.equals(list.shopNetwork())) {
            AllSoundEvents.DENY.playOnServer(level, player.blockPosition());
            CreateLang.translate("stock_keeper.wrong_network")
                    .style(ChatFormatting.RED)
                    .sendStatus(player);
            return;
        }

        Couple<InventorySummary> bakeEntries = list.bakeEntries(level, null);
        InventorySummary paymentEntries = bakeEntries.getSecond();

        boolean isCoins = paymentEntries.getItemMap().containsKey(NIItems.NIBANKING_SYSTEM_ITEMS.get("base_coin").get());

        if (!isCoins) {
            return;
        }

        boolean isBankTransfer = false;
        NNTransaction transaction = NIBanking.webSocket.getTransactionFromPlayer(player);
        int amountNeeded = 0;

        for (BigItemStack bigItemStack : paymentEntries.getStacks()) {
            amountNeeded += bigItemStack.count;
        }

        int playerAmount = CurrencyHelper.calculateInventoryValue(player);

        if (transaction != null) {
            NIBanking.LOGGER.info(String.valueOf(transaction.isComplete()));
            if (!NIBanking.webSocket.isTransactionComplete(transaction)) {
                return;
            }

            isBankTransfer = true;
            NIBanking.webSocket.transactionCache.invalidate(transaction.getTransactionId());
        } else {
            if (amountNeeded > playerAmount) {
                //NIBanking.neoNetworkIRS.requestMoney(list.shopOwner().toString(), player,getUUID().toString(), amountNeeded, list.shopNetwork().toString());
                NIBanking.neoNetworkIRS.requestMoney("795ef1ea-3a53-4c02-abb7-5a62f037440e", player.getName().getString(), amountNeeded, list.shopNetwork().toString());

                new Thread(() -> {
                    NNTransaction threadTransaction = NIBanking.webSocket.getTransactionFromPlayer(player);
                    Couple<InventorySummary> bakeCopy = bakeEntries.copy();

                    if (threadTransaction == null) {
                        NIBanking.LOGGER.error("Transaction is null for unknown reason.");
                        return;
                    }

                    while (!NIBanking.webSocket.isTransactionComplete(threadTransaction)) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException exception) {
                            NIBanking.LOGGER.error("Error sleeping thread for websocket await.");
                        }
                    }

                    completePurchase(bakeCopy, tickerBE, level, player, mainHandItem);
                }).start();

                ci.cancel();

                return;
            }
        }

        if (!isBankTransfer) {
            CurrencyHelper.removeValueFromInventory(player, amountNeeded);
        }

        // Complete transaction

        completePurchase(bakeEntries, tickerBE, level, player, mainHandItem);

        ci.cancel();
    }

    @Unique
    private static void completePurchase(Couple<InventorySummary> bakeEntries, StockTickerBlockEntity tickerBE, Level level, Player player, ItemStack mainHandItem) {
        InventorySummary orderEntries = bakeEntries.getFirst();
        PackageOrder order = new PackageOrder(orderEntries.getStacksByCount());

        tickerBE.getAccurateSummary();

        InventorySummary recentSummary = tickerBE.getRecentSummary();

        // tickerBE summery is on a cache and expires after 1tick. this function being called from a thread is outside that tick even though its grabbing current.
        // TODO: what to do?
        for (BigItemStack entry : order.stacks()) {
            if (recentSummary.getCountOf(entry.stack) >= entry.count)
                continue;

            AllSoundEvents.DENY.playOnServer(level, player.blockPosition());
            CreateLang.translate("stock_keeper.stock_level_too_low")
                    .style(ChatFormatting.RED)
                    .sendStatus(player);

            // TODO: REFUND PLAYER WHEN THIS HAPPENS
            return;
        }

        tickerBE.broadcastPackageRequest(LogisticallyLinkedBehaviour.RequestType.PLAYER, order, null, ShoppingListItem.getAddress(mainHandItem));

        if (!order.isEmpty()) {
            AllSoundEvents.STOCK_TICKER_TRADE.playOnServer(level, tickerBE.getBlockPos());
        }
    }

}
