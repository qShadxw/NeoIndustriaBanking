package uk.co.tmdavies.nibanking.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import uk.co.tmdavies.nibanking.TestingFunctions;

@Mixin(com.simibubi.create.content.logistics.stockTicker.StockTickerInteractionHandler.class)
public class StockTickerInteractionHandlerMixin {

    @Inject(
            method = "interactWithShop",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void nibanking$interactWithShop(Player player, Level level, BlockPos targetPos, ItemStack mainHandItem, CallbackInfo ci) {
        ci.cancel();

        TestingFunctions.bareShop(level);
    }
}

