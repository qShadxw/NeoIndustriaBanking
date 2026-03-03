package uk.co.tmdavies.nibanking.utils;

import com.simibubi.create.content.logistics.packagerLink.LogisticsManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import uk.co.tmdavies.nibanking.NIBanking;
import uk.co.tmdavies.nibanking.objects.Pair;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
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

    public static void dumpFields(Object... obj) {
        for (Object object : obj) {
            for (Field field : object.getClass().getFields()) {
                try {
                    NIBanking.LOGGER.info("{}: [{}]", field.getName(), field.get(field.getName()));
                } catch (IllegalAccessException exception) {
                    NIBanking.LOGGER.error("Illegal Access: {}", (Object) exception.getStackTrace());
                }
            }
        }
    }

    @SafeVarargs
    public static void dumpVars(Pair<Object, Object>... objs) {
        for (Pair<Object, Object> pair : objs) {
            NIBanking.LOGGER.info("{}: [{}]", pair.a(), pair.b());
        }
    }
}
