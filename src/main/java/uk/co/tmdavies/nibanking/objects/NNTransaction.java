package uk.co.tmdavies.nibanking.objects;

import com.simibubi.create.content.logistics.stockTicker.StockTickerBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class NNTransaction {

    private final String transactionId;
    private final String toUUID;
    private final String fromUUID;
    private final int amount;
    private final String reference;
    private Level level;
    private StockTickerBlockEntity tickerBE;
    private ItemStack shoppingListCopy;
    private boolean isComplete;

    public NNTransaction(String transactionId, String toUUID, String fromUUID, int amount, String reference, Level level, StockTickerBlockEntity tickerBE, ItemStack shoppingListCopy, boolean isComplete) {
        this.transactionId = transactionId;
        this.toUUID = toUUID;
        this.fromUUID = fromUUID;
        this.amount = amount;
        this.reference = reference;
        this.level = level;
        this.tickerBE = tickerBE;
        this.shoppingListCopy = shoppingListCopy;
        this.isComplete = isComplete;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getToUUID() {
        return toUUID;
    }

    public String getFromUUID() {
        return fromUUID;
    }

    public int getAmount() {
        return amount;
    }

    public String getReference() {
        return reference;
    }

    public Level getLevel() {
        return level;
    }

    public StockTickerBlockEntity getTickerBE() {
        return tickerBE;
    }

    public ItemStack getShoppingListCopy() {
        return shoppingListCopy;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public void setTickerBE(StockTickerBlockEntity tickerBE) {
        this.tickerBE = tickerBE;
    }

    public void setShoppingListCopy(ItemStack shoppingListCopy) {
        this.shoppingListCopy = shoppingListCopy;
    }

    public void setComplete(boolean isComplete) {
        this.isComplete = isComplete;
    }

    @Override
    public String toString() {
        return String.format("NNTransaction[txID=%s, toUUID=%s, fromUUID=%s, amount=%s, reference=%s, isComplete=%s]", transactionId, toUUID, fromUUID, amount, reference, isComplete);
    }
}
