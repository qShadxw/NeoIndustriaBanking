package uk.co.tmdavies.nibanking.mixins;

import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.packager.InventorySummary;
import com.simibubi.create.content.logistics.tableCloth.ShoppingListItem;
import net.createmod.catnip.data.Couple;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
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
        NIBanking.LOGGER.info("Injected interactWithShop in StockTickerInteractionHandler");

        ShoppingListItem.ShoppingList list = ShoppingListItem.getList(mainHandItem);

        if (list == null) {
            return;
        }

        Couple<InventorySummary> bakeEntries = list.bakeEntries(level, null);
        InventorySummary paymentEntries = bakeEntries.getSecond();
        boolean isCoins = paymentEntries.getItemMap().containsKey(NIItems.NIBANKING_SYSTEM_ITEMS.get("base_coin").get());

        if (!isCoins) {
            return;
        }

        ci.cancel();

        // if player has pending transaction
        // deal with that first before checking for money

        // if (pendingTransaction {
        // return if not finished
        // } else {
        int amountNeeded = 0;

        for (BigItemStack bigItemStack : paymentEntries.getStacks()) {
            amountNeeded += bigItemStack.count;
        }

        int playerAmount = CurrencyHelper.calculateInventoryValue(player);
        // }

        NIBanking.LOGGER.info("AmountNeeded: {}", amountNeeded);
        NIBanking.LOGGER.info("PlayerAmount: {}", playerAmount);
        NIBanking.LOGGER.info("playerAmount > amountNeeded: {}", playerAmount > amountNeeded);

        if (amountNeeded > playerAmount) {
            NNTransaction transaction = NIBanking.webSocket.getTransactionFromPlayer(player);

            if (transaction == null) {
                return;
            }

            NIBanking.LOGGER.info("Player has pending transaction.");
            NIBanking.LOGGER.info("Info: {} {} {} {}", transaction.transactionId(), transaction.toUUID(), transaction.fromUUID(), transaction.reference());

            if (!NIBanking.neoNetworkIRS.hasCompleted(transaction.transactionId())) {
                return;
            }
        }

        // Complete transaction


    }

}
