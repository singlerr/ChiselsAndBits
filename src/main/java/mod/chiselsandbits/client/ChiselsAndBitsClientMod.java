package mod.chiselsandbits.client;

import mod.chiselsandbits.ChiselsAndBitsModelLoadingPlugin;
import mod.chiselsandbits.bitstorage.ItemStackSpecialRendererBitStorage;
import mod.chiselsandbits.compat.client.TextureStitchCallback;
import mod.chiselsandbits.core.ChiselsAndBitsClient;
import mod.chiselsandbits.events.extra.ResourceRegistrationEvent;
import mod.chiselsandbits.registry.ModBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;

public class ChiselsAndBitsClientMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModelLoadingPlugin.register(new ChiselsAndBitsModelLoadingPlugin());
        ChiselsAndBitsClient.onClientInit();
        BuiltinItemRendererRegistry.INSTANCE.register(
                ModBlocks.BIT_STORAGE_BLOCK_ITEM.get(), new ItemStackSpecialRendererBitStorage()::renderByItem);
        ResourceRegistrationEvent.EVENT.register(ChiselsAndBitsClient::registerIconTextures);
        TextureStitchCallback.POST.register(ChiselsAndBitsClient::retrieveRegisteredIconSprites);
    }
}
