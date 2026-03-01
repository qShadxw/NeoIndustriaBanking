package uk.co.tmdavies.nibanking.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import uk.co.tmdavies.nibanking.utils.Utils;

import java.util.List;

public class CoinItem extends Item {
    private final int value;

    public CoinItem(int value, Properties properties) {
        super(properties);
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    @Override
    public Component getName(ItemStack stack) {
        return Utils.Chat(Component.translatable(this.getDescriptionId(stack)));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> toolTipComponents, TooltipFlag tooltipFlag) {
        String tooltip = Component.translatable("tooltip.nibanking.coin_item").getString().replace("$value", String.valueOf(this.value));
        toolTipComponents.add(Utils.Chat(tooltip));
        super.appendHoverText(stack, context, toolTipComponents, tooltipFlag);
    }

}
