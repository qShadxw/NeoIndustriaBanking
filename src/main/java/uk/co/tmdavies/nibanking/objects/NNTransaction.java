package uk.co.tmdavies.nibanking.objects;

public class NNTransaction {

    private final String transactionId;
    private final String toUUID;
    private final String fromUUID;
    private final int amount;
    private final String reference;
    private boolean complete;

    public NNTransaction(String transactionId, String toUUID, String fromUUID, int amount, String reference, boolean complete) {
        this.transactionId = transactionId;
        this.toUUID = toUUID;
        this.fromUUID = fromUUID;
        this.amount = amount;
        this.reference = reference;
        this.complete = complete;
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

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    @Override
    public String toString() {
        return String.format("NNTransaction[txID=%s, toUUID=%s, fromUUID=%s, amount=%s, reference=%s, complete=%s]", transactionId, toUUID, fromUUID, amount, reference, complete);
    }
}
