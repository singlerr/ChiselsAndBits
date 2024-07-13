package mod.chiselsandbits.client.model;

import io.github.fabricators_of_create.porting_lib.models.geometry.IUnbakedGeometry;
import java.util.function.Function;
import mod.chiselsandbits.client.model.baked.DataAwareChiseledBlockBakedModel;
import mod.chiselsandbits.client.model.loader.FabricBakedModelDelegate;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;

public class ChiseledBlockModel implements IUnbakedGeometry<ChiseledBlockModel> {

    @Override
    public BakedModel bake(
            BlockModel context,
            ModelBaker baker,
            Function<Material, TextureAtlasSprite> spriteGetter,
            ModelState modelState,
            ItemOverrides overrides,
            ResourceLocation modelLocation,
            boolean isGui3d) {
        return new FabricBakedModelDelegate(new DataAwareChiseledBlockBakedModel());
    }
}
