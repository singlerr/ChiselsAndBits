package mod.chiselsandbits.client.model.baked;

import java.util.Map;
import java.util.Set;
import mod.chiselsandbits.chiseledblock.TileEntityBlockChiseled;
import mod.chiselsandbits.client.model.data.IModelData;
import mod.chiselsandbits.helpers.ModUtil;
import mod.chiselsandbits.render.ModelCombined;
import mod.chiselsandbits.render.chiseledblock.ChiselRenderType;
import mod.chiselsandbits.render.chiseledblock.ChiseledBlockBakedModel;
import mod.chiselsandbits.render.chiseledblock.ChiseledBlockSmartModel;
import mod.chiselsandbits.utils.ModClientHooks;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class DataAwareChiseledBlockBakedModel extends BaseSmartModel {

    @Override
    public boolean usesBlockLight() {
        return true;
    }

    @Override
    public BakedModel handleBlockState(
            BlockState state, RandomSource random, IModelData modelData, ChiselRenderType renderType) {
        if (modelData.getData(TileEntityBlockChiseled.MODEL_PROP) == null) {
            return super.handleBlockState(state, random, modelData, renderType);
        }
        return modelData.getData(TileEntityBlockChiseled.MODEL_PROP).get(renderType);
    }

    //    public void updateModelData(
    //            @NotNull final BlockAndTintGetter world,
    //            @NotNull final BlockPos pos,
    //            @NotNull final BlockState state,
    //            @NotNull final IModelData modelData) {
    //        if (state == null || world.getBlockEntity(pos) == null) {
    //            return;
    //        }
    //
    //        // This seems silly, but it proves to be faster in practice.
    //        VoxelBlobStateReference data = modelData.getData(TileEntityBlockChiseled.MP_VBSR);
    //        Integer blockP = modelData.getData(TileEntityBlockChiseled.MP_PBSI);
    //        blockP = blockP == null ? 0 : blockP;
    //
    //        final RenderType layer = ModClientHooks.getRenderType();
    //
    //        if (layer == null) {
    //            final ChiseledBlockBakedModel[] models = new
    // ChiseledBlockBakedModel[ChiselRenderType.values().length];
    //            int o = 0;
    //
    //            for (final ChiselRenderType l : ChiselRenderType.values()) {
    //                models[o++] = ChiseledBlockSmartModel.getCachedModel(
    //                        (TileEntityBlockChiseled) Objects.requireNonNull(world.getBlockEntity(pos)), l);
    //            }
    //
    //            modelData.setData(TileEntityBlockChiseled.MODEL_PROP, new ModelCombined(models));
    //            return;
    //        }
    //
    //        BakedModel baked;
    //        if (RenderType.chunkBufferLayers().contains(layer)
    //                && ChiseledBlockSmartModel.FLUID_RENDER_TYPES.get(
    //                        RenderType.chunkBufferLayers().indexOf(layer))) {
    //            final ChiseledBlockBakedModel a = ChiseledBlockSmartModel.getCachedModel(
    //                    (TileEntityBlockChiseled) Objects.requireNonNull(world.getBlockEntity(pos)),
    //                    ChiselRenderType.fromLayer(layer, false));
    //            final ChiseledBlockBakedModel b = ChiseledBlockSmartModel.getCachedModel(
    //                    (TileEntityBlockChiseled) Objects.requireNonNull(world.getBlockEntity(pos)),
    //                    ChiselRenderType.fromLayer(layer, true));
    //
    //            if (a.isEmpty()) {
    //                baked = b;
    //            } else if (b.isEmpty()) {
    //                baked = a;
    //            } else {
    //                baked = new ModelCombined(a, b);
    //            }
    //        } else {
    //            baked = ChiseledBlockSmartModel.getCachedModel(
    //                    (TileEntityBlockChiseled) Objects.requireNonNull(world.getBlockEntity(pos)),
    //                    ChiselRenderType.fromLayer(layer, false));
    //        }
    //
    //        modelData.setData(TileEntityBlockChiseled.MODEL_PROP, baked);
    //    }

    @Override
    public void updateModelData(
            @NotNull BlockAndTintGetter world,
            @NotNull BlockPos pos,
            @NotNull BlockState state,
            @NotNull IModelData modelData) {}

    @Override
    public Set<ChiselRenderType> getRenderTypes(
            @NotNull BlockAndTintGetter world,
            @NotNull BlockPos pos,
            @NotNull BlockState state,
            @NotNull IModelData modelData) {
        if (!(world.getBlockEntity(pos) instanceof TileEntityBlockChiseled te)) {
            return Set.of(ChiselRenderType.SOLID);
        }

        if (te.getBlob() == null) {
            return Set.of(ChiselRenderType.SOLID);
        }

        Map<ChiselRenderType, BakedModel> data;
        if ((data = modelData.getData(TileEntityBlockChiseled.MODEL_PROP)) == null) {
            return ModUtil.getAllRenderTypes(te.getBlob());
        }

        return data.keySet();
    }

    @Override
    public BakedModel resolve(BakedModel originalModel, ItemStack stack, Level world, LivingEntity entity) {
        RenderType renderType = ModClientHooks.getRenderType();
        final ChiseledBlockBakedModel a =
                ChiseledBlockSmartModel.getCachedModel(stack, ChiselRenderType.fromLayer(renderType, false));
        final ChiseledBlockBakedModel b =
                ChiseledBlockSmartModel.getCachedModel(stack, ChiselRenderType.fromLayer(renderType, true));

        if (a.isEmpty()) {
            return b;
        } else if (b.isEmpty()) {
            return a;
        } else {
            return new ModelCombined(a, b);
        }
    }
}
