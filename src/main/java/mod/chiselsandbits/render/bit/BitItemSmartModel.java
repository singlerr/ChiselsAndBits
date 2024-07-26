package mod.chiselsandbits.render.bit;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import java.util.HashMap;
import java.util.Set;
import mod.chiselsandbits.chiseledblock.data.VoxelBlob;
import mod.chiselsandbits.client.model.baked.BaseSmartModel;
import mod.chiselsandbits.client.model.data.IModelData;
import mod.chiselsandbits.core.ClientSide;
import mod.chiselsandbits.events.TickHandler;
import mod.chiselsandbits.interfaces.ICacheClearable;
import mod.chiselsandbits.items.ItemChiseledBit;
import mod.chiselsandbits.registry.ModItems;
import mod.chiselsandbits.render.ModelCombined;
import mod.chiselsandbits.render.chiseledblock.ChiselRenderType;
import mod.chiselsandbits.render.chiseledblock.ChiseledBlockBakedModel;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BitItemSmartModel extends BaseSmartModel implements ICacheClearable {
    private static final HashMap<Integer, BakedModel> modelCache = new HashMap<Integer, BakedModel>();
    private static final HashMap<Integer, BakedModel> largeModelCache = new HashMap<Integer, BakedModel>();

    private static final NonNullList<ItemStack> alternativeStacks = NonNullList.create();

    private BakedModel getCachedModel(int stateID, final boolean large) {
        if (stateID == 0) {
            // We are running an empty bit, for display purposes.
            // Lets loop:
            if (alternativeStacks.isEmpty()) ModItems.ITEM_BLOCK_BIT.get().fillItemCategory(alternativeStacks);

            final int alternativeIndex =
                    (int) ((Math.floor(TickHandler.getClientTicks() / 20d)) % alternativeStacks.size());

            stateID = ItemChiseledBit.getStackState(alternativeStacks.get(alternativeIndex));
        }

        final HashMap<Integer, BakedModel> target = large ? largeModelCache : modelCache;
        BakedModel out = target.get(stateID);

        if (out == null) {
            if (large) {
                final VoxelBlob blob = new VoxelBlob();
                blob.fill(stateID);
                final BakedModel a = new ChiseledBlockBakedModel(
                        stateID, ChiselRenderType.SOLID, blob, DefaultVertexFormat.BLOCK, true);
                final BakedModel b = new ChiseledBlockBakedModel(
                        stateID, ChiselRenderType.SOLID_FLUID, blob, DefaultVertexFormat.BLOCK, true);
                final BakedModel c = new ChiseledBlockBakedModel(
                        stateID, ChiselRenderType.CUTOUT_MIPPED, blob, DefaultVertexFormat.BLOCK, true);
                final BakedModel d = new ChiseledBlockBakedModel(
                        stateID, ChiselRenderType.CUTOUT, blob, DefaultVertexFormat.BLOCK, true);
                final BakedModel e = new ChiseledBlockBakedModel(
                        stateID, ChiselRenderType.TRANSLUCENT, blob, DefaultVertexFormat.BLOCK, true);
                out = new ModelCombined(a, b, c, d, e);
            } else {
                out = new BitItemBaked(stateID);
            }

            target.put(stateID, out);
        }

        return out;
    }

    public BakedModel func_239290_a_(
            final BakedModel originalModel, final ItemStack stack, final Level world, final LivingEntity entity) {
        return getCachedModel(ItemChiseledBit.getStackState(stack), ClientSide.instance.holdingShift());
    }

    @Override
    public BakedModel resolve(BakedModel originalModel, ItemStack stack, Level world, LivingEntity entity) {
        return func_239290_a_(originalModel, stack, world, entity);
    }

    @Override
    public void clearCache() {
        modelCache.clear();
        largeModelCache.clear();
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
            @NotNull IModelData modelData) {}

    @Override
    public Set<ChiselRenderType> getRenderTypes(
            @NotNull BlockAndTintGetter world,
            @NotNull BlockPos pos,
            @NotNull BlockState state,
            @NotNull IModelData modelData) {
        return Set.of();
    }
}
