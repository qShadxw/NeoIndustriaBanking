package uk.co.tmdavies.nibanking.managers;

import com.google.common.reflect.ClassPath;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.common.NeoForge;
import uk.co.tmdavies.nibanking.NIBanking;
import uk.co.tmdavies.nibanking.listeners.NIListener;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class ModManager {

    private final NIBanking mod;
    private final IEventBus modEventBus;
    private final ModContainer modContainer;
    private final String listenerPackagePath;

    public ModManager(NIBanking mod, IEventBus modEventBus, ModContainer modContainer, String listenerPackagePath) {
        this.mod = mod;
        this.modEventBus = modEventBus;
        this.modContainer = modContainer;
        this.listenerPackagePath = listenerPackagePath;
    }

    public void registerEvents() {
        NIBanking.LOGGER.info("\n\nREGISTER EVENTS CALLED\n\n");
        if (this.listenerPackagePath == null) {
            NIBanking.LOGGER.error("You need to specify the listener package to use ModManager#registerEvents.");
            return;
        }

        ClassLoader classLoader = mod.getClass().getClassLoader();

        NIBanking.LOGGER.info("\n\nCLASS LOADER: {}\n\n", classLoader.toString());

        try {
            ClassPath classPath = ClassPath.from(classLoader);

            NIBanking.LOGGER.info("\n\nCLASS PATH: {}\n\n", classPath.toString());
            NIBanking.LOGGER.info("\n\nLISTENER PACKAGE PATH: {}\n\n", this.listenerPackagePath);
            NIBanking.LOGGER.info("\n\nCLASSES: {}\n\n", classPath.getTopLevelClassesRecursive(this.listenerPackagePath).toString());

            for (ClassPath.ClassInfo info : classPath.getTopLevelClassesRecursive(this.listenerPackagePath)) {
                NIBanking.LOGGER.info("\n\nChecking Listener: {}\n\n", info.getName());

                if (info.getSimpleName().equals("NIListener")) {
                    continue;
                }

                Class<?> clazz = Class.forName(info.getName(), true, classLoader);

                if (!Arrays.stream(clazz.getAnnotations()).toList().contains(NIListener.class)) {
                    return;
                }

                NeoForge.EVENT_BUS.register(clazz.getConstructors()[0].newInstance());
            }
        } catch (IOException | ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
