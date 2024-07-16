package mod.chiselsandbits.client.model.baked;

import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;

public abstract class BaseBakedBlockModel extends BaseBakedPerspectiveModel implements BakedModel, DataAwareBakedModel {

    @Override
    public final boolean useAmbientOcclusion() {
        return true;
    }

    @Override
    public final boolean isGui3d() {
        return true;
    }

    @Override
    public final boolean isCustomRenderer() {
        return false;
    }

    @Override
    public final ItemTransforms getTransforms() {
        return ItemTransforms.NO_TRANSFORMS;
    }

    @Override
    public ItemOverrides getOverrides() {
        return ItemOverrides.EMPTY;
    }
}
