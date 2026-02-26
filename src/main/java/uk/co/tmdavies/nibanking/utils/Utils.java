package uk.co.tmdavies.nibanking.utils;

import net.minecraft.network.chat.Component;

public class Utils {
    public static Component Chat(String message, Object... args) {
        return Component.literal(String.format(message.replace("&", "§"), args));
    }
}
