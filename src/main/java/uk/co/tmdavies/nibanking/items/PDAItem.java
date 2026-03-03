package uk.co.tmdavies.nibanking.items;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import uk.co.tmdavies.nibanking.screen.custom.PDAMenu;
import uk.co.tmdavies.nibanking.utils.Utils;

public class PDAItem extends Item implements MenuProvider {
    public PDAItem(Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        return Utils.Chat(Component.translatable(this.getDescriptionId(stack)));
    }

    @Override
    public Component getDisplayName() {
        return null;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide()) {
            ServerPlayer serverPlayer = (ServerPlayer) player;
            serverPlayer.openMenu(new SimpleMenuProvider(
                    (containerId, playerInventory, playerEntity) -> new PDAMenu(containerId, playerInventory, playerEntity, this), Component.literal("PDA Menu")
            ));
        }
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }

    @Override
    public AbstractContainerMenu createMenu(int i, @NotNull Inventory inventory, @NotNull Player player) {
        return new PDAMenu(i, inventory, player, this);
    }
}
