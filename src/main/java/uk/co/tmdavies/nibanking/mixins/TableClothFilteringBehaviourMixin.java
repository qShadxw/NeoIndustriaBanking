package uk.co.tmdavies.nibanking.mixins;

import com.simibubi.create.content.logistics.tableCloth.TableClothFilteringBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsFormatter;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import uk.co.tmdavies.nibanking.items.NIItems;
import uk.co.tmdavies.nibanking.utils.CurrencyHelper;
import uk.co.tmdavies.nibanking.utils.Utils;

import java.util.List;

@Mixin(com.simibubi.create.content.logistics.tableCloth.TableClothFilteringBehaviour.class)
public class TableClothFilteringBehaviourMixin {
    // Reference: https://github.com/flaulox/CreateCurrencyShops/blob/master/src/main/java/net/flaulox/create_currency_shops/mixin/TableClothFilteringBehaviourMixin.java#L45
    @Inject(
            method = "setFilter",
            at = @At("HEAD"),
            cancellable = true
    )
    private void nibanking$setFilter(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (!CurrencyHelper.isValidCurrency(stack)) {
            return;
        }

        FilteringBehaviour self = (FilteringBehaviour) (Object) this;
        FilteringBehaviourAccessor accessor = (FilteringBehaviourAccessor) self;

        accessor.setCount(1);

        boolean result = self.setFilter(new ItemStack(NIItems.NIBANKING_SYSTEM_ITEMS.get("base_coin").get()));
        cir.setReturnValue(result);
    }

    @Inject(
            method = "createBoard",
            at = @At("HEAD"),
            cancellable = true
    )
    private void nibanking$createBoard(Player player, BlockHitResult hitResult, CallbackInfoReturnable<ValueSettingsBoard> cir) {
        TableClothFilteringBehaviour self = (TableClothFilteringBehaviour) (Object) this;

        if (!self.getFilter().is(NIItems.NIBANKING_SYSTEM_ITEMS.get("base_coin"))) {
            return;
        }

        List<Component> currencyRows = List.of(
                Utils.Chat("&ax1"),
                Utils.Chat("&ax5"),
                Utils.Chat("&ax10"),
                Utils.Chat("&ax100")
        );
        ValueSettingsBoard board = new ValueSettingsBoard(
                self.getLabel(), 100, 10,
                currencyRows,
                new ValueSettingsFormatter(value -> {
                    int[] steps = { 1, 5, 10, 100 };
                    int[] offsets = { 0, 100, 600, 1000 };
                    int step = steps[value.row()];
                    int offset = offsets[value.row()];
                    int minimum = offsets[value.row()] + steps[value.row()];
                    int actualValue = Math.max(minimum, offset + value.value() * step);

                    return Component.literal(String.valueOf(actualValue));
                })
        );

        cir.setReturnValue(board);
    }

    @Inject(
            method = "setValueSettings",
            at = @At("HEAD"),
            cancellable = true
    )
    private void setValueSettings(Player player, ValueSettingsBehaviour.ValueSettings settings, boolean ctrlDown, CallbackInfo ci) {
        TableClothFilteringBehaviour self = (TableClothFilteringBehaviour) (Object) this;

        if (!self.getFilter().is(NIItems.NIBANKING_SYSTEM_ITEMS.get("base_coin"))) {
            return;
        }

        if (self.getValueSettings().equals(settings)) {
            return;
        }

        int[] steps = { 1, 5, 10, 100 };
        int[] offsets = { 0, 100, 600, 1000 };
        int step = steps[settings.row()];
        int offset = offsets[settings.row()];
        int minimum = offsets[settings.row()] + steps[settings.row()];
        int actualValue = Math.max(minimum, offset + settings.value() * step);

        FilteringBehaviourAccessor accessor = (FilteringBehaviourAccessor) self;
        accessor.setCount(Math.min(actualValue, 11600));

//        self.blockEntity.setChanged();
//        self.blockEntity.sendData();
        ci.cancel();
    }
}
