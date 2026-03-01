package uk.co.tmdavies.nibanking.utils;

import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import uk.co.tmdavies.nibanking.items.CoinItem;
import uk.co.tmdavies.nibanking.items.NIItems;

public class CurrencyHelper {
    public static boolean isValidCurrency(ItemStack itemStack) {
        Item item = itemStack.getItem();
        boolean isValid = false;

        for (ItemEntry<?> items : NIItems.NIBANKING_ITEMS.values()) {
            if (items.is(item.builtInRegistryHolder().key().location())) {
                isValid = true;
            }
        }

        return isValid;
    }

    public static int calculateInventoryValue(Player player) {
        int amount = 0;

        for (ItemStack item : player.getInventory().items) {
            if(!isValidCurrency(item)) {
                continue;
            }

            CoinItem coin = (CoinItem) item.getItem();

            amount += coin.getValue() * item.getCount();
        }

        return amount;
    }
}
