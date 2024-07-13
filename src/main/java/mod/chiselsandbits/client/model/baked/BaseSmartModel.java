package mod.chiselsandbits.client.model.baked;

import java.util.List;
import mod.chiselsandbits.client.model.data.IModelData;
import mod.chiselsandbits.core.ClientSide;
import mod.chiselsandbits.render.NullBakedModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BaseSmartModel implements DataAwareBakedModel {

    private final ItemOverrides overrides;

    private static class OverrideHelper extends ItemOverrides {
        final BaseSmartModel parent;

        public OverrideHelper(final BaseSmartModel p) {
            super();

            parent = p;
        }

        @Nullable
        @Override
        public BakedModel resolve(
                BakedModel bakedModel,
                ItemStack itemStack,
                @Nullable ClientLevel clientLevel,
                @Nullable LivingEntity livingEntity,
                int i) {
            return parent.resolve(bakedModel, itemStack, clientLevel, livingEntity);
        }
    }
    ;

    public BaseSmartModel() {
        overrides = new OverrideHelper(this);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return true;
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        final TextureAtlasSprite sprite = Minecraft.getInstance()
                .getBlockRenderer()
                .getBlockModelShaper()
                .getParticleIcon(Blocks.STONE.defaultBlockState());

        if (sprite == null) {
            return ClientSide.instance.getMissingIcon();
        }

        return sprite;
    }

    @Override
    public ItemTransforms getTransforms() {
        return ItemTransforms.NO_TRANSFORMS;
    }

    @NotNull
    @Override
    public List<BakedQuad> getQuads(
            @Nullable final BlockState state,
            @Nullable final Direction side,
            @NotNull final RandomSource rand,
            @NotNull final IModelData extraData) {

        final DataAwareBakedModel model = (DataAwareBakedModel) handleBlockState(state, rand, extraData);
        return model.getQuads(state, side, rand, extraData);
    }

    @Override
    public List<BakedQuad> getQuads(
            @Nullable final BlockState state, @Nullable final Direction side, final RandomSource rand) {
        final BakedModel model = handleBlockState(state, rand);
        return model.getQuads(state, side, rand);
    }

    public BakedModel handleBlockState(final BlockState state, final RandomSource rand) {
        return NullBakedModel.instance;
    }

    public BakedModel handleBlockState(final BlockState state, final RandomSource random, final IModelData modelData) {
        return NullBakedModel.instance;
    }

    @Override
    public ItemOverrides getOverrides() {
        return overrides;
    }

    public BakedModel resolve(
            final BakedModel originalModel, final ItemStack stack, final Level world, final LivingEntity entity) {
        return originalModel;
    }
}
