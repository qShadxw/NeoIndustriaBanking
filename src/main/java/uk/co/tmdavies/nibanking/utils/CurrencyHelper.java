package uk.co.tmdavies.nibanking.utils;

import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import uk.co.tmdavies.nibanking.NIBanking;
import uk.co.tmdavies.nibanking.items.CoinItem;
import uk.co.tmdavies.nibanking.items.NIItems;

import java.util.ArrayList;
import java.util.List;

public class CurrencyHelper {
    public static List<CoinItem> getAllCoins() {
        return NIItems.NIBANKING_ITEMS.values().stream()
                .map(entry -> (CoinItem) entry.get())
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .toList();
    }

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
            if (!isValidCurrency(item)) {
                continue;
            }

            CoinItem coin = (CoinItem) item.getItem();

            amount += coin.getValue() * item.getCount();
        }

        return amount;
    }

    public static boolean removeValueFromInventory(Player player, int amount) {
        int playerAmount = calculateInventoryValue(player);

        if (amount > playerAmount) {
            return false;
        }

        playerAmount -= amount;

        for (ItemStack item : player.getInventory().items) {
            if (item.getItem() instanceof CoinItem) {
                item.setCount(0);
            }
        }

        List<CoinItem> coinItems = getAllCoins();

        for (CoinItem coin : coinItems) {
            while (playerAmount >= coin.getValue()) {
                player.getInventory().add(new ItemStack(coin));
                playerAmount -= coin.getValue();
            }
        }

        return true;
    }

    public static boolean addValueToInventory(Player player, int amount) {
        List<CoinItem> coinItems = getAllCoins();

        for (CoinItem coin : coinItems) {
            while (amount >= coin.getValue()) {
                ItemStack coinStack = new ItemStack(coin);
                boolean added = player.addItem(coinStack);

                if (!added || !coinStack.isEmpty()) {
                    player.drop(coinStack, false);
                }

                amount -= coin.getValue();
            }
        }

        return true;
    }
}
