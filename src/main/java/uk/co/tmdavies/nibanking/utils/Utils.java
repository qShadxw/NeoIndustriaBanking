package uk.co.tmdavies.nibanking.utils;

import net.minecraft.network.chat.Component;
import uk.co.tmdavies.nibanking.NIBanking;

import java.util.UUID;

public class Utils {
    public static Component Chat(String message, Object... args) {
        return Component.literal(String.format(message.replace("&", "§"), args));
    }

    public static Component Chat(Component message, Object... args) {
        return Component.literal(String.format(message.getString().replace("&", "§"), args));
    }

    public static UUID reconstructUUID(String uuidString) {
        if (uuidString == null || uuidString.length() != 32) {
            NIBanking.LOGGER.error("Invalid UUID length. Expected 32 characters.");
            return null;
        }

        return UUID.fromString(uuidString.substring(0, 8) + "-" +
                uuidString.substring(8, 12) + "-" +
                uuidString.substring(12, 16) + "-" +
                uuidString.substring(16, 20) + "-" +
                uuidString.substring(20, 32));
    }
}
