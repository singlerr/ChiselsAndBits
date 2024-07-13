package mod.chiselsandbits.core;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

import fuzs.forgeconfigapiport.fabric.api.forge.v4.ForgeConfigRegistry;
import fuzs.forgeconfigapiport.fabric.api.forge.v4.ForgeModConfigEvents;
import io.github.fabricators_of_create.porting_lib.models.geometry.RegisterGeometryLoadersCallback;
import io.github.fabricators_of_create.porting_lib.util.EnvExecutor;
import mod.chiselsandbits.api.IChiselAndBitsAPI;
import mod.chiselsandbits.chiseledblock.BlockBitInfo;
import mod.chiselsandbits.chiseledblock.data.VoxelBlob;
import mod.chiselsandbits.client.UndoTracker;
import mod.chiselsandbits.config.Configuration;
import mod.chiselsandbits.core.api.ChiselAndBitsAPI;
import mod.chiselsandbits.core.api.IMCHandler;
import mod.chiselsandbits.events.EventPlayerInteract;
import mod.chiselsandbits.events.VaporizeWater;
import mod.chiselsandbits.interfaces.ICacheClearable;
import mod.chiselsandbits.network.NetworkChannel;
import mod.chiselsandbits.registry.*;
import mod.chiselsandbits.render.SmartModelManager;
import mod.chiselsandbits.render.chiseledblock.ChiseledBlockSmartModel;
import mod.chiselsandbits.utils.Constants;
import mod.chiselsandbits.utils.LanguageHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLModIdMappingEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ChiselsAndBits {
    public static final @Nonnull String MODID = Constants.MOD_ID;

    private static ChiselsAndBits instance;
    private Configuration config;
    private final IChiselAndBitsAPI api = new ChiselAndBitsAPI();
    private final NetworkChannel networkChannel = new NetworkChannel(MODID);

    List<ICacheClearable> cacheClearables = new ArrayList<>();

    public ChiselsAndBits() {
        instance = this;

        LanguageHandler.loadLangPath(
                "assets/chiselsandbits/lang/%s.json"); // hotfix config comments, it's ugly bcs it's gonna be replaced
        config = new Configuration();

        EnvExecutor.runWhenOn(
                EnvType.CLIENT,
                () -> () -> ClientLifecycleEvents.CLIENT_STARTED.register(ChiselsAndBitsClient::onClientInit));
        EnvExecutor.runWhenOn(EnvType.CLIENT, () -> () -> RegisterGeometryLoadersCallback.EVENT.register(ChiselsAndBitsClient::onModelRegistry));
//        DistExecutor.unsafeRunWhenOn(
//                Dist.CLIENT,
//                () -> () -> FMLJavaModLoadingContext.get()
//                        .getModEventBus()
//                        .addListener(ChiselsAndBitsClient::registerIconTextures));
//        DistExecutor.unsafeRunWhenOn(
//                Dist.CLIENT,
//                () -> () -> FMLJavaModLoadingContext.get()
//                        .getModEventBus()
//                        .addListener(ChiselsAndBitsClient::retrieveRegisteredIconSprites));
        DistExecutor.unsafeRunWhenOn(
                Dist.CLIENT,
                () -> () -> FMLJavaModLoadingContext.get()
                        .getModEventBus()
                        .addListener(SmartModelManager.getInstance()::onModelBakeEvent));
        DistExecutor.unsafeRunWhenOn(
                Dist.CLIENT,
                () -> () -> FMLJavaModLoadingContext.get()
                        .getModEventBus()
                        .addListener(SmartModelManager.getInstance()::textureStichEvent));
        DistExecutor.unsafeRunWhenOn(
                Dist.CLIENT,
                () -> () -> FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup));

        MinecraftForge.EVENT_BUS.addListener(this::handleIdMapping);
        MinecraftForge.EVENT_BUS.register(new VaporizeWater());
        MinecraftForge.EVENT_BUS.register(new EventPlayerInteract());

        ModBlocks.onModConstruction();
        ModContainerTypes.onModConstruction();
        ModItems.onModConstruction();
        ModRecipeSerializers.onModConstruction();
        ModTileEntityTypes.onModConstruction();

        networkChannel.registerCommonMessages();
    }

    public static ChiselsAndBits getInstance() {
        return instance;
    }

    public static Configuration getConfig() {
        return instance.config;
    }

    public static IChiselAndBitsAPI getApi() {
        return instance.api;
    }

    public static NetworkChannel getNetworkChannel() {
        return instance.networkChannel;
    }

    private void handleIMCEvent() {

    }


    public void clientSetup() {
        EnvExecutor.runWhenOn(
                EnvType.CLIENT,
                () -> () -> ForgeModConfigEvents.reloading(ChiselsAndBits.MODID).register(ChiseledBlockSmartModel::onConfigurationReload));
    }

    boolean idsHaveBeenMapped = false;

    public void handleIdMapping() {
        idsHaveBeenMapped = true;
        BlockBitInfo.recalculate();
        clearCache();
    }

    public void clearCache() {
        if (idsHaveBeenMapped) {
            for (final ICacheClearable clearable : cacheClearables) {
                clearable.clearCache();
            }

            addClearable(UndoTracker.getInstance());
            VoxelBlob.clearCache();
        }
    }

    public void addClearable(final ICacheClearable cache) {
        if (!cacheClearables.contains(cache)) {
            cacheClearables.add(cache);
        }
    }
}
