package mod.chiselsandbits.utils;

import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

public final class EnvExecutor {

    public static void runWhenOn(EnvType type, Supplier<Runnable> task) {
        if (FabricLoader.getInstance().getEnvironmentType() == type) task.get().run();
    }

    public static <T> T callWhenOn(EnvType type, Supplier<Supplier<T>> callable) {
        if (FabricLoader.getInstance().getEnvironmentType() == type)
            return callable.get().get();
        return null;
    }

    public static <T> T unsafeRunForDist(Supplier<Supplier<T>> client, Supplier<Supplier<T>> server) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
            return client.get().get();
        else return server.get().get();
    }
}
