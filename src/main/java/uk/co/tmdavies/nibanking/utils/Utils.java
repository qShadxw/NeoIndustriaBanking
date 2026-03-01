package uk.co.tmdavies.nibanking.utils;

import net.minecraft.network.chat.Component;

public class Utils {
    public static Component Chat(String message, Object... args) {
        return Component.literal(String.format(message.replace("&", "§"), args));
    }

    public static Component Chat(Component message, Object... args) {
        return Component.literal(String.format(message.getString().replace("&", "§"), args));
    }
}
