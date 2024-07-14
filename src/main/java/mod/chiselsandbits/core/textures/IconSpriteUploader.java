package mod.chiselsandbits.core.textures;

import mod.chiselsandbits.utils.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.TextureAtlasHolder;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class IconSpriteUploader extends TextureAtlasHolder {
    public static final ResourceLocation TEXTURE_MAP_NAME =
            new ResourceLocation(Constants.MOD_ID, "textures/atlases/icons.png");

    public IconSpriteUploader() {
        super(
                Minecraft.getInstance().getTextureManager(),
                TEXTURE_MAP_NAME,
                new ResourceLocation(Constants.MOD_ID, "icons"));
    }

    /**
     * Overridden to make it public
     */
    @Override
    public @NotNull TextureAtlasSprite getSprite(@NotNull ResourceLocation location) {
        return super.getSprite(location);
    }
}
