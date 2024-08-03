package mod.chiselsandbits.client;

import io.github.fabricators_of_create.porting_lib.event.client.TextureStitchCallback;
import mod.chiselsandbits.ChiselsAndBitsModelLoadingPlugin;
import mod.chiselsandbits.core.ChiselsAndBitsClient;
import mod.chiselsandbits.events.extra.ResourceRegistrationEvent;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;

public class ChiselsAndBitsClientMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModelLoadingPlugin.register(new ChiselsAndBitsModelLoadingPlugin());
        ChiselsAndBitsClient.onClientInit();
        ResourceRegistrationEvent.EVENT.register(() -> {
            ChiselsAndBitsClient.registerIconTextures();
            TextureStitchCallback.POST.register(ChiselsAndBitsClient::retrieveRegisteredIconSprites);
        });
    }
}
