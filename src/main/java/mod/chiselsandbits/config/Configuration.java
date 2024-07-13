package mod.chiselsandbits.config;

import fuzs.forgeconfigapiport.fabric.api.forge.v4.ForgeConfigRegistry;
import mod.chiselsandbits.core.ChiselsAndBits;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Mod root configuration.
 */
public class Configuration {
    private final ClientConfiguration clientConfig;
    private final ServerConfiguration serverConfig;
    private final CommonConfiguration commonConfig;

    private final ForgeConfigSpec clientConfigSpec;
    private final ForgeConfigSpec commonConfigSpec;
    private final ForgeConfigSpec serverConfigSpec;

    public Configuration() {
        final Pair<ClientConfiguration, ForgeConfigSpec> cli =
                new ForgeConfigSpec.Builder().configure(ClientConfiguration::new);
        final Pair<ServerConfiguration, ForgeConfigSpec> ser =
                new ForgeConfigSpec.Builder().configure(ServerConfiguration::new);
        final Pair<CommonConfiguration, ForgeConfigSpec> com =
                new ForgeConfigSpec.Builder().configure(CommonConfiguration::new);

        clientConfig = cli.getLeft();
        serverConfig = ser.getLeft();
        commonConfig = com.getLeft();

        clientConfigSpec = cli.getRight();
        serverConfigSpec = ser.getRight();
        commonConfigSpec = com.getRight();

        ForgeConfigRegistry.INSTANCE.register(ChiselsAndBits.MODID, ModConfig.Type.COMMON, commonConfigSpec);
        ForgeConfigRegistry.INSTANCE.register(ChiselsAndBits.MODID, ModConfig.Type.CLIENT, clientConfigSpec);
        ForgeConfigRegistry.INSTANCE.register(ChiselsAndBits.MODID, ModConfig.Type.SERVER, serverConfigSpec);
    }

    public ClientConfiguration getClient() {
        return clientConfig;
    }

    public ServerConfiguration getServer() {
        return serverConfig;
    }

    public CommonConfiguration getCommon() {
        return commonConfig;
    }

    public ForgeConfigSpec getClientConfigSpec() {
        return clientConfigSpec;
    }

    public ForgeConfigSpec getCommonConfigSpec() {
        return commonConfigSpec;
    }

    public ForgeConfigSpec getServerConfigSpec() {
        return serverConfigSpec;
    }
}
