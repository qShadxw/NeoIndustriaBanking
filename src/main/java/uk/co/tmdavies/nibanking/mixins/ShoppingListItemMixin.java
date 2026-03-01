package uk.co.tmdavies.nibanking.mixins;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import uk.co.tmdavies.nibanking.NIBanking;
import uk.co.tmdavies.nibanking.objects.NNTransaction;

@Mixin(com.simibubi.create.content.logistics.tableCloth.ShoppingListItem.class)
public class ShoppingListItemMixin {

    @Inject(
            method = "useOn",
            at = @At("HEAD"),
            cancellable = true
    )
    public void nibanking$onUse(UseOnContext pContext, CallbackInfoReturnable<InteractionResult> cir) {
        NIBanking.LOGGER.info("Injected onUse for ShoppingListItem");

        Player player = pContext.getPlayer();
        NNTransaction transaction = NIBanking.webSocket.getTransactionFromPlayer(player);

        if (transaction == null) {
            return;
        }

        NIBanking.LOGGER.info("Player has pending transaction.");
        NIBanking.LOGGER.info("Info: {} {} {} {}", transaction.transactionId(), transaction.toUUID(), transaction.fromUUID(), transaction.reference());
    }

}
