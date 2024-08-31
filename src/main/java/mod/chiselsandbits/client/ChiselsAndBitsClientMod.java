package mod.chiselsandbits.client;

import mod.chiselsandbits.ChiselsAndBitsModelLoadingPlugin;
import mod.chiselsandbits.bitstorage.ItemStackSpecialRendererBitStorage;
import mod.chiselsandbits.core.ChiselsAndBitsClient;
import mod.chiselsandbits.events.extra.ResourceRegistrationEvent;
import mod.chiselsandbits.registry.ModBlocks;
import mod.chiselsandbits.utils.Constants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;

public class ChiselsAndBitsClientMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModelLoadingPlugin.register(new ChiselsAndBitsModelLoadingPlugin());
        ChiselsAndBitsClient.onClientInit();
        BuiltinItemRendererRegistry.INSTANCE.register(
                ModBlocks.BIT_STORAGE_BLOCK_ITEM.get(), new ItemStackSpecialRendererBitStorage()::renderByItem);
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES)
                .registerReloadListener(new SimpleSynchronousResourceReloadListener() {
                    @Override
                    public ResourceLocation getFabricId() {
                        return new ResourceLocation(Constants.MOD_ID, "reload_listener");
                    }

                    @Override
                    public void onResourceManagerReload(ResourceManager resourceManager) {
                        ChiselsAndBitsClient.registerIconTextures();
                    }
                });

        ResourceRegistrationEvent.EVENT.register(() -> {
            ChiselsAndBitsClient.registerIconTextures();
            TextureStitchCallback.POST.register(ChiselsAndBitsClient::retrieveRegisteredIconSprites);
        });
    }
}
