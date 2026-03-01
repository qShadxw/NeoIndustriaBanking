package uk.co.tmdavies.nibanking.objects;

public record NNTransaction(String transactionId, String toUUID, String fromUUID, int amount, String reference, boolean complete) {
}
