package mod.chiselsandbits.core;

import java.awt.image.BufferedImage;
import java.io.IOException;
import mod.chiselsandbits.bitbag.BagGui;
import mod.chiselsandbits.client.gui.SpriteIconPositioning;
import mod.chiselsandbits.core.textures.IconSpriteUploader;
import mod.chiselsandbits.modes.ChiselMode;
import mod.chiselsandbits.modes.IToolMode;
import mod.chiselsandbits.modes.PositivePatternMode;
import mod.chiselsandbits.modes.TapeMeasureModes;
import mod.chiselsandbits.printer.ChiselPrinterScreen;
import mod.chiselsandbits.registry.ModContainerTypes;
import mod.chiselsandbits.utils.TextureUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.Resource;

public class ChiselsAndBitsClient {

    private static IconSpriteUploader spriteUploader;

    @Environment(EnvType.CLIENT)
    public static void onClientInit() {

        ClientSide.instance.preInit();
        ClientSide.instance.init();
        ClientSide.instance.postInit();
        MenuScreens.register(ModContainerTypes.BAG_CONTAINER.get(), BagGui::new);
        MenuScreens.register(ModContainerTypes.CHISEL_STATION_CONTAINER.get(), ChiselPrinterScreen::new);
    }

    //    @Environment(EnvType.CLIENT)
    //    public static void onModelRegistry(Map<ResourceLocation, IGeometryLoader<?>> loaders) {
    //        loaders.put(new ResourceLocation(Constants.MOD_ID, "chiseled_block"),
    // ChiseledBlockModelLoader.getInstance());
    //    }

    @Environment(EnvType.CLIENT)
    public static void registerIconTextures() {
        spriteUploader = new IconSpriteUploader();
        if (Minecraft.getInstance().getResourceManager() instanceof ReloadableResourceManager resourceManager) {
            resourceManager.registerReloadListener(spriteUploader);
        }
    }

    @Environment(EnvType.CLIENT)
    public static void retrieveRegisteredIconSprites(TextureAtlas map) {
        if (!map.location().equals(IconSpriteUploader.TEXTURE_MAP_NAME)) return;
        ClientSide.swapIcon = spriteUploader.getSprite(new ResourceLocation("chiselsandbits", "swap"));
        ClientSide.placeIcon = spriteUploader.getSprite(new ResourceLocation("chiselsandbits", "place"));
        ClientSide.undoIcon = spriteUploader.getSprite(new ResourceLocation("chiselsandbits", "undo"));
        ClientSide.redoIcon = spriteUploader.getSprite(new ResourceLocation("chiselsandbits", "redo"));
        ClientSide.trashIcon = spriteUploader.getSprite(new ResourceLocation("chiselsandbits", "trash"));
        ClientSide.sortIcon = spriteUploader.getSprite(new ResourceLocation("chiselsandbits", "sort"));
        ClientSide.roll_x = spriteUploader.getSprite(new ResourceLocation("chiselsandbits", "roll_x"));
        ClientSide.roll_z = spriteUploader.getSprite(new ResourceLocation("chiselsandbits", "roll_z"));
        ClientSide.white = spriteUploader.getSprite(new ResourceLocation("chiselsandbits", "white"));

        for (final ChiselMode mode : ChiselMode.values()) {
            loadIcon(spriteUploader, mode);
        }

        for (final PositivePatternMode mode : PositivePatternMode.values()) {
            loadIcon(spriteUploader, mode);
        }

        for (final TapeMeasureModes mode : TapeMeasureModes.values()) {
            loadIcon(spriteUploader, mode);
        }
    }

    @Environment(EnvType.CLIENT)
    private static void loadIcon(final IconSpriteUploader spriteUploader, final IToolMode mode) {
        final SpriteIconPositioning sip = new SpriteIconPositioning();

        final ResourceLocation sprite =
                new ResourceLocation("chiselsandbits", mode.name().toLowerCase());
        final ResourceLocation png = new ResourceLocation(
                "chiselsandbits", "textures/icons/" + mode.name().toLowerCase() + ".png");

        sip.sprite = spriteUploader.getSprite(sprite);

        try {
            final Resource iresource = Minecraft.getInstance()
                    .getResourceManager()
                    .getResource(png)
                    .get();
            final BufferedImage bi = TextureUtils.readBufferedImage(iresource.open());

            int bottom = 0;
            int right = 0;
            sip.left = bi.getWidth();
            sip.top = bi.getHeight();

            for (int x = 0; x < bi.getWidth(); x++) {
                for (int y = 0; y < bi.getHeight(); y++) {
                    final int color = bi.getRGB(x, y);
                    final int a = color >> 24 & 0xff;
                    if (a > 0) {
                        sip.left = Math.min(sip.left, x);
                        right = Math.max(right, x);

                        sip.top = Math.min(sip.top, y);
                        bottom = Math.max(bottom, y);
                    }
                }
            }

            sip.height = bottom - sip.top + 1;
            sip.width = right - sip.left + 1;

            sip.left /= bi.getWidth();
            sip.width /= bi.getWidth();
            sip.top /= bi.getHeight();
            sip.height /= bi.getHeight();
        } catch (final IOException e) {
            sip.height = 1;
            sip.width = 1;
            sip.left = 0;
            sip.top = 0;
        }

        ClientSide.instance.setIconForMode(mode, sip);
    }
}
