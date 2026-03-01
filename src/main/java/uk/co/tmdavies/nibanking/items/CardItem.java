package uk.co.tmdavies.nibanking.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import uk.co.tmdavies.nibanking.utils.Utils;

import java.util.List;

public class CardItem extends Item {
    public CardItem(Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        return Utils.Chat(Component.translatable(this.getDescriptionId(stack)));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> toolTipComponents, TooltipFlag tooltipFlag) {
        toolTipComponents.add(Utils.Chat("&cThis was deprecated as soon as it was created."));
        super.appendHoverText(stack, context, toolTipComponents, tooltipFlag);
    }
}
