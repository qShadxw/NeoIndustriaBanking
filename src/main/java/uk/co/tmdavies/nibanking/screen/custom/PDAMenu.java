package uk.co.tmdavies.nibanking.screen.custom;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.co.tmdavies.nibanking.items.PDAItem;
import uk.co.tmdavies.nibanking.screen.ModMenuTypes;

public class PDAMenu extends AbstractContainerMenu {
    public final PDAItem pdaItem;

    public PDAMenu(int containerId, Inventory inventory, RegistryFriendlyByteBuf registryFriendlyByteBuf) {
        this(containerId, inventory, null, null);
    }

    public PDAMenu(int containerId, Inventory inventory, Player player, PDAItem pdaItem) {
        super(ModMenuTypes.PDA_MENU.get(), containerId);

        this.pdaItem = pdaItem;

        this.addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return 0;
            }

            @Override
            public void set(int i) {

            }
        });
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
//        return ItemStack.EMPTY;
        return null;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
