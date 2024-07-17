package mod.chiselsandbits.render.chiseledblock;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.io.IOException;
import java.util.BitSet;
import java.util.List;
import java.util.Objects;
import mod.chiselsandbits.chiseledblock.NBTBlobConverter;
import mod.chiselsandbits.chiseledblock.TileEntityBlockChiseled;
import mod.chiselsandbits.chiseledblock.data.VoxelBlob;
import mod.chiselsandbits.chiseledblock.data.VoxelBlobStateInstance;
import mod.chiselsandbits.chiseledblock.data.VoxelBlobStateReference;
import mod.chiselsandbits.client.model.baked.BaseSmartModel;
import mod.chiselsandbits.client.model.baked.DataAwareChiseledBlockBakedModel;
import mod.chiselsandbits.client.model.data.IModelData;
import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.helpers.ModUtil;
import mod.chiselsandbits.interfaces.ICacheClearable;
import mod.chiselsandbits.render.ModelCombined;
import mod.chiselsandbits.render.NullBakedModel;
import mod.chiselsandbits.render.cache.CacheMap;
import mod.chiselsandbits.utils.ModClientHooks;
import mod.chiselsandbits.utils.RenderTypeUtils;
import mod.chiselsandbits.utils.SimpleMaxSizedCache;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fml.config.ModConfig;
import org.jetbrains.annotations.NotNull;

public class ChiseledBlockSmartModel extends BaseSmartModel implements ICacheClearable {
    private static final SimpleMaxSizedCache<ModelCacheKey, ChiseledBlockBakedModel> MODEL_CACHE =
            new SimpleMaxSizedCache<>(
                    ChiselsAndBits.getConfig().getClient().modelCacheSize.get());
    private static final CacheMap<ItemStack, BakedModel> ITEM_TO_MODEL_CACHE = new CacheMap<>();
    private static final CacheMap<VoxelBlobStateInstance, Integer> SIDE_CACHE = new CacheMap<>();

    public static final BitSet FLUID_RENDER_TYPES =
            new BitSet(RenderType.chunkBufferLayers().size());

    public static int getSides(final TileEntityBlockChiseled te) {
        final VoxelBlobStateReference ref = te.getBlobStateReference();
        Integer out;

        if (ref == null) {
            return 0;
        }

        synchronized (SIDE_CACHE) {
            out = SIDE_CACHE.get(ref.getInstance());
            if (out == null) {
                final VoxelBlob blob = ref.getVoxelBlob();

                // ignore non-solid, and fluids.
                blob.filter(RenderType.solid());
                blob.filterFluids(false);

                out = blob.getSideFlags(0, VoxelBlob.dim_minus_one, VoxelBlob.dim2);
                SIDE_CACHE.put(ref.getInstance(), out);
            }
        }

        return out;
    }

    public static ChiseledBlockBakedModel getCachedModel(
            final TileEntityBlockChiseled te, final ChiselRenderType layer) {
        final VoxelBlobStateReference data = te.getBlobStateReference();
        Integer blockP = te.getPrimaryBlockStateId();
        VoxelBlob vBlob = (data != null) ? data.getVoxelBlob() : null;
        return getCachedModel(blockP, vBlob, layer, getModelFormat(), Objects.requireNonNull(te.getLevel()).random);
    }

    public static ChiseledBlockBakedModel getCachedModel(final ItemStack stack, final ChiselRenderType layer) {
        Integer blockP = 0;
        return getCachedModel(
                blockP, ModUtil.getBlobFromStack(stack, null), layer, getModelFormat(), RandomSource.create());
    }

    private static VertexFormat getModelFormat() {
        return DefaultVertexFormat.BLOCK;
    }

    public static boolean ForgePipelineDisabled() {
        return ChiselsAndBits.getConfig().getClient().disableCustomVertexFormats.get();
    }

    public static ChiseledBlockBakedModel getCachedModel(
            final Integer blockP,
            final VoxelBlob data,
            final ChiselRenderType layer,
            final VertexFormat format,
            final RandomSource random) {
        if (data == null) {
            return new ChiseledBlockBakedModel(blockP, layer, null, format);
        }

        ChiseledBlockBakedModel out = null;

        if (format == getModelFormat()) {
            out = MODEL_CACHE.get(new ModelCacheKey(data, layer));
        }

        if (out == null) {
            out = new ChiseledBlockBakedModel(blockP, layer, data, format);

            if (out.isEmpty()) {
                out = ChiseledBlockBakedModel.breakingParticleModel(layer, blockP, random);
            }

            if (format == getModelFormat()) {
                MODEL_CACHE.put(new ModelCacheKey(data, layer), out);
            }
        } else {
            return out;
        }

        return out;
    }

    @Override
    public BakedModel handleBlockState(BlockState state, RandomSource random, IModelData modelData) {
        if (state == null) {
            return NullBakedModel.instance;
        }

        // This seems silly, but it proves to be faster in practice.
        VoxelBlobStateReference data = modelData.getData(TileEntityBlockChiseled.MP_VBSR);
        final VoxelBlob blob = data == null ? null : data.getVoxelBlob();
        Integer blockP = modelData.getData(TileEntityBlockChiseled.MP_PBSI);
        blockP = blockP == null ? 0 : blockP;

        final RenderType layer = ModClientHooks.getRenderType();

        if (layer == null) {
            final ChiseledBlockBakedModel[] models = new ChiseledBlockBakedModel[ChiselRenderType.values().length];
            int o = 0;

            for (final ChiselRenderType l : ChiselRenderType.values()) {
                models[o++] = getCachedModel(blockP, blob, l, getModelFormat(), random);
            }

            return new ModelCombined(models);
        }

        BakedModel baked;
        if (RenderType.chunkBufferLayers().contains(layer)
                && FLUID_RENDER_TYPES.get(RenderType.chunkBufferLayers().indexOf(layer))) {
            final ChiseledBlockBakedModel a =
                    getCachedModel(blockP, blob, ChiselRenderType.fromLayer(layer, false), getModelFormat(), random);
            final ChiseledBlockBakedModel b =
                    getCachedModel(blockP, blob, ChiselRenderType.fromLayer(layer, true), getModelFormat(), random);

            if (a.isEmpty()) {
                baked = b;
            } else if (b.isEmpty()) {
                baked = a;
            } else {
                baked = new ModelCombined(a, b);
            }
        } else {
            baked = getCachedModel(blockP, blob, ChiselRenderType.fromLayer(layer, false), getModelFormat(), random);
        }

        return baked;
    }

    @Override
    public BakedModel resolve(
            final BakedModel originalModel, final ItemStack stack, final Level world, final LivingEntity entity) {
        BakedModel mdl = ITEM_TO_MODEL_CACHE.get(stack);

        if (mdl != null) {
            return mdl;
        }

        CompoundTag c = stack.getTag();
        if (c == null) {
            return this;
        }

        c = c.getCompound(ModUtil.NBT_BLOCKENTITYTAG);

        final byte[] data = c.getByteArray(NBTBlobConverter.NBT_LEGACY_VOXEL);
        byte[] vdata = c.getByteArray(NBTBlobConverter.NBT_VERSIONED_VOXEL);
        final Integer blockP = c.getInt(NBTBlobConverter.NBT_PRIMARY_STATE);

        if (vdata.length == 0 && data.length > 0) {
            final VoxelBlob xx = new VoxelBlob();

            try {
                xx.fromLegacyByteArray(data);
            } catch (final IOException e) {
                // :_(
            }

            vdata = xx.blobToBytes(VoxelBlob.VERSION_COMPACT_PALLETED);
        }

        final BakedModel[] models = new BakedModel[ChiselRenderType.values().length];
        for (final ChiselRenderType l : ChiselRenderType.values()) {
            models[l.ordinal()] = getCachedModel(
                    blockP,
                    new VoxelBlobStateReference(vdata, 0L).getVoxelBlob(),
                    l,
                    DefaultVertexFormat.BLOCK,
                    RandomSource.create());
        }

        mdl = new ModelCombined(models);

        ITEM_TO_MODEL_CACHE.put(stack, mdl);

        return mdl;
    }

    @Override
    public void clearCache() {
        SIDE_CACHE.clear();
        MODEL_CACHE.clear();
        ITEM_TO_MODEL_CACHE.clear();

        FLUID_RENDER_TYPES.clear();
        final List<RenderType> blockRenderTypes = RenderType.chunkBufferLayers();
        for (int i = 0; i < blockRenderTypes.size(); i++) {
            final RenderType renderType = blockRenderTypes.get(i);
            for (final Fluid fluid : BuiltInRegistries.FLUID) {
                if (RenderTypeUtils.canRenderInLayer(fluid.defaultFluidState(), renderType)) {
                    FLUID_RENDER_TYPES.set(i);
                    break;
                }
            }
        }
    }

    public static void onConfigurationReload(ModConfig modConfig) {
        MODEL_CACHE.changeMaxSize(
                ChiselsAndBits.getConfig().getClient().modelCacheSize.get());
    }

    @Override
    public boolean usesBlockLight() {
        return true;
    }

    @Override
    public void updateModelData(
            @NotNull BlockAndTintGetter world,
            @NotNull BlockPos pos,
            @NotNull BlockState state,
            @NotNull IModelData modelData) {
        if (state == null || world.getBlockEntity(pos) == null) {
            return;
        }

        // This seems silly, but it proves to be faster in practice.
        VoxelBlobStateReference data = modelData.getData(TileEntityBlockChiseled.MP_VBSR);
        Integer blockP = modelData.getData(TileEntityBlockChiseled.MP_PBSI);
        blockP = blockP == null ? 0 : blockP;

        final RenderType layer = ModClientHooks.getRenderType();

        if (layer == null) {
            final ChiseledBlockBakedModel[] models = new ChiseledBlockBakedModel[ChiselRenderType.values().length];
            int o = 0;

            for (final ChiselRenderType l : ChiselRenderType.values()) {
                models[o++] = ChiseledBlockSmartModel.getCachedModel(
                        (TileEntityBlockChiseled) Objects.requireNonNull(world.getBlockEntity(pos)), l);
            }

            modelData.setData(DataAwareChiseledBlockBakedModel.MODEL_PROP, new ModelCombined(models));
            return;
        }

        BakedModel baked;
        if (RenderType.chunkBufferLayers().contains(layer)
                && ChiseledBlockSmartModel.FLUID_RENDER_TYPES.get(
                        RenderType.chunkBufferLayers().indexOf(layer))) {
            final ChiseledBlockBakedModel a = ChiseledBlockSmartModel.getCachedModel(
                    (TileEntityBlockChiseled) Objects.requireNonNull(world.getBlockEntity(pos)),
                    ChiselRenderType.fromLayer(layer, false));
            final ChiseledBlockBakedModel b = ChiseledBlockSmartModel.getCachedModel(
                    (TileEntityBlockChiseled) Objects.requireNonNull(world.getBlockEntity(pos)),
                    ChiselRenderType.fromLayer(layer, true));

            if (a.isEmpty()) {
                baked = b;
            } else if (b.isEmpty()) {
                baked = a;
            } else {
                baked = new ModelCombined(a, b);
            }
        } else {
            baked = ChiseledBlockSmartModel.getCachedModel(
                    (TileEntityBlockChiseled) Objects.requireNonNull(world.getBlockEntity(pos)),
                    ChiselRenderType.fromLayer(layer, false));
        }

        modelData.setData(DataAwareChiseledBlockBakedModel.MODEL_PROP, baked);
    }

    private static final class ModelCacheKey {
        private final VoxelBlob blob;
        private final ChiselRenderType type;

        private ModelCacheKey(final VoxelBlob blob, final ChiselRenderType type) {
            this.blob = blob;
            this.type = type;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof ModelCacheKey)) {
                return false;
            }
            final ModelCacheKey that = (ModelCacheKey) o;
            return Objects.equals(blob, that.blob) && Objects.equals(type, that.type);
        }

        @Override
        public int hashCode() {
            return Objects.hash(blob, type);
        }
    }
}
