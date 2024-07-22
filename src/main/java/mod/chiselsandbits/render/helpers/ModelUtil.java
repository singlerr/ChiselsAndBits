package mod.chiselsandbits.render.helpers;

import com.google.common.collect.Maps;
import io.github.fabricators_of_create.porting_lib.util.client.ClientHooks;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import mod.chiselsandbits.chiseledblock.BlockBitInfo;
import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.helpers.DeprecationHelper;
import mod.chiselsandbits.helpers.ModUtil;
import mod.chiselsandbits.interfaces.ICacheClearable;
import mod.chiselsandbits.render.chiseledblock.ChiselRenderType;
import mod.chiselsandbits.render.chiseledblock.ChiseledBlockBakedModel;
import mod.chiselsandbits.render.helpers.ModelQuadLayer.ModelQuadLayerBuilder;
import mod.chiselsandbits.utils.FluidUtil;
import mod.chiselsandbits.utils.LightUtil;
import mod.chiselsandbits.utils.ModClientHooks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

@SuppressWarnings("unchecked")
public class ModelUtil implements ICacheClearable {
    private static final HashMap<Pair<RenderType, Direction>, HashMap<Integer, String>> blockToTexture =
            new HashMap<>();
    private static HashMap<Triple<Integer, RenderType, Direction>, ModelQuadLayer[]> cache = new HashMap<>();
    private static HashMap<Pair<RenderType, Integer>, ChiseledBlockBakedModel> breakCache = new HashMap<>();

    @SuppressWarnings("unused")
    private static ModelUtil instance = new ModelUtil();

    public static RandomSource MODEL_RANDOM = RandomSource.create();

    @Override
    public void clearCache() {
        blockToTexture.clear();
        cache.clear();
        breakCache.clear();
    }

    public static ModelQuadLayer[] getCachedFace(
            final int stateID, final RandomSource weight, final Direction face, final RenderType layer) {
        if (layer == null) {
            return null;
        }

        final Triple<Integer, RenderType, Direction> cacheVal = Triple.of(stateID, layer, face);

        final ModelQuadLayer[] mpc = cache.get(cacheVal);
        if (mpc != null) {
            return mpc;
        }

        final RenderType original = ModClientHooks.getRenderType();
        try {
            ClientHooks.setRenderType(layer);
            return getInnerCachedFace(cacheVal, stateID, weight, face, layer);
        } finally {
            // restore previous layer.
            ClientHooks.setRenderType(original);
        }
    }

    private static ModelQuadLayer[] getInnerCachedFace(
            final Triple<Integer, RenderType, Direction> cacheVal,
            final int stateID,
            final RandomSource weight,
            final Direction face,
            final RenderType layer) {
        final BlockState state = ModUtil.getStateById(stateID);
        final BakedModel model = ModelUtil.solveModel(
                state,
                weight,
                Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getBlockModel(state),
                layer);
        final int lv = ChiselsAndBits.getConfig().getClient().useGetLightValue.get()
                ? DeprecationHelper.getLightValue(state)
                : 0;

        final Fluid fluid = BlockBitInfo.getFluidFromBlock(state.getBlock());
        if (fluid != null) {
            for (final Direction xf : Direction.values()) {
                final ModelQuadLayer[] mp = new ModelQuadLayer[1];
                mp[0] = new ModelQuadLayer();
                mp[0].color = FluidUtil.getColor(fluid);
                mp[0].light = lv;
                final float V = 0.5f;
                final float Uf = 1.0f;
                final float U = 0.5f;
                final float Vf = 1.0f;

                if (xf.getAxis() == Axis.Y) {
                    mp[0].sprite = Minecraft.getInstance()
                            .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                            .apply(FluidUtil.getStillTexture(fluid).contents().name());
                    mp[0].uvs = new float[] {Uf, Vf, 0, Vf, Uf, 0, 0, 0};
                } else if (xf.getAxis() == Axis.X) {
                    mp[0].sprite = Minecraft.getInstance()
                            .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                            .apply(FluidUtil.getFlowingTexture(fluid).contents().name());
                    mp[0].uvs = new float[] {U, 0, U, V, 0, 0, 0, V};
                } else {
                    mp[0].sprite = Minecraft.getInstance()
                            .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                            .apply(FluidUtil.getFlowingTexture(fluid).contents().name());
                    mp[0].uvs = new float[] {U, 0, 0, 0, U, V, 0, V};
                }

                mp[0].tint = 0;

                final Triple<Integer, RenderType, Direction> k = Triple.of(stateID, layer, xf);
                cache.put(k, mp);
            }

            return cache.get(cacheVal);
        }

        final HashMap<Direction, ArrayList<ModelQuadLayerBuilder>> tmp = new HashMap<>();
        final int color = BlockBitInfo.getColorFor(state, 0);

        for (final Direction f : Direction.values()) {
            tmp.put(f, new ArrayList<>());
        }

        if (model != null) {
            for (final Direction f : Direction.values()) {
                final List<BakedQuad> quads = ModelUtil.getModelQuads(model, state, f, MODEL_RANDOM);
                processFaces(tmp, quads, state);
            }

            processFaces(tmp, ModelUtil.getModelQuads(model, state, null, MODEL_RANDOM), state);
        }

        for (final Direction f : Direction.values()) {
            final Triple<Integer, RenderType, Direction> k = Triple.of(stateID, layer, f);
            final ArrayList<ModelQuadLayerBuilder> x = tmp.get(f);
            final ModelQuadLayer[] mp = new ModelQuadLayer[x.size()];

            for (int z = 0; z < x.size(); z++) {
                mp[z] = x.get(z).build(stateID, color, lv);
            }

            cache.put(k, mp);
        }

        return cache.get(cacheVal);
    }

    private static List<BakedQuad> getModelQuads(
            final BakedModel model, final BlockState state, final Direction f, final RandomSource rand) {
        try {
            // try to get block model...
            return model.getQuads(state, f, rand);
        } catch (final Throwable t) {

        }

        try {
            // try to get item model?
            return model.getQuads(null, f, rand);
        } catch (final Throwable t) {

        }

        final ItemStack is = ModUtil.getItemStackFromBlockState(state);
        if (!ModUtil.isEmpty(is)) {
            final BakedModel secondModel = getOverrides(model)
                    .resolve(model, is, Minecraft.getInstance().level, Minecraft.getInstance().player, 0);

            if (secondModel != null) {
                try {
                    return secondModel.getQuads(null, f, rand);
                } catch (final Throwable t) {

                }
            }
        }

        // try to not crash...
        return Collections.emptyList();
    }

    private static ItemOverrides getOverrides(final BakedModel model) {
        if (model != null) {
            final ItemOverrides modelOverrides = model.getOverrides();
            return modelOverrides == null ? ItemOverrides.EMPTY : modelOverrides;
        }
        return ItemOverrides.EMPTY;
    }

    private static void processFaces(
            final HashMap<Direction, ArrayList<ModelQuadLayerBuilder>> tmp,
            final List<BakedQuad> quads,
            final BlockState state) {

        for (final BakedQuad q : quads) {
            final Direction face = q.getDirection();

            if (face == null) {
                continue;
            }

            try {
                final TextureAtlasSprite sprite = findQuadTexture(q, state);
                final ArrayList<ModelQuadLayerBuilder> l = tmp.get(face);

                ModelQuadLayerBuilder b = null;
                for (final ModelQuadLayerBuilder lx : l) {
                    if (lx.cache.sprite == sprite) {
                        b = lx;
                        break;
                    }
                }

                if (b == null) {
                    // top/bottom
                    int uCoord = 0;
                    int vCoord = 2;

                    switch (face) {
                        case NORTH:
                        case SOUTH:
                            uCoord = 0;
                            vCoord = 1;
                            break;
                        case EAST:
                        case WEST:
                            uCoord = 1;
                            vCoord = 2;
                            break;
                        default:
                    }

                    b = new ModelQuadLayerBuilder(sprite, uCoord, vCoord);
                    b.setFace(face);
                    b.setSourceQuad(q);
                    b.cache.tint = q.getTintIndex();
                    l.add(b);
                    LightUtil.put(b.uvr, q);
                }

                //                if (ChiselsAndBits.getConfig()
                //                        .getClient()
                //                        .enableFaceLightmapExtraction
                //                        .get()) {
                //                    // TODO: Check if this works.
                //                    b.lv.setVertexFormat(DefaultVertexFormat.BLOCK);
                //                    LightUtil.put(b.lv, q);
                //                }
            } catch (final Exception e) {

            }
        }
    }

    private ModelUtil() {
        ChiselsAndBits.getInstance().addClearable(this);
    }

    public static TextureAtlasSprite findQuadTexture(final BakedQuad q, final BlockState state)
            throws IllegalArgumentException, IllegalAccessException, NullPointerException {

        if (q.getSprite() == null)
            return Minecraft.getInstance()
                    .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                    .apply(new ResourceLocation("missingno"));
        return q.getSprite();
    }

    public static BakedModel solveModel(
            final BlockState state, final RandomSource weight, final BakedModel originalModel, final RenderType layer) {
        boolean hasFaces = false;

        try {
            hasFaces = hasFaces(originalModel, state, null, weight);

            for (final Direction f : Direction.values()) {
                hasFaces = hasFaces || hasFaces(originalModel, state, f, weight);
            }
        } catch (final Exception e) {
            // an exception was thrown.. use the item model and hope...
            hasFaces = false;
        }

        if (!hasFaces) {
            // if the model is empty then lets grab an item and try that...
            final ItemStack is = ModUtil.getItemStackFromBlockState(state);
            if (!ModUtil.isEmpty(is)) {
                final BakedModel itemModel = Minecraft.getInstance()
                        .getItemRenderer()
                        .getModel(is, Minecraft.getInstance().level, Minecraft.getInstance().player, 0);

                try {
                    hasFaces = hasFaces(originalModel, state, null, weight);

                    for (final Direction f : Direction.values()) {
                        hasFaces = hasFaces || hasFaces(originalModel, state, f, weight);
                    }
                } catch (final Exception e) {
                    // an exception was thrown.. use the item model and hope...
                    hasFaces = false;
                }

                if (hasFaces) {
                    return itemModel;
                } else {
                    return new SimpleGeneratedModel(
                            findTexture(Block.getId(state), originalModel, Direction.UP, layer, weight));
                }
            }
        }

        return originalModel;
    }

    private static boolean hasFaces(
            final BakedModel model, final BlockState state, final Direction f, final RandomSource weight) {
        final List<BakedQuad> l = getModelQuads(model, state, f, weight);
        if (l == null || l.isEmpty()) {
            return false;
        }

        TextureAtlasSprite texture = null;

        try {
            texture = findTexture(null, l, f);
        } catch (final Exception e) {
        }

        final ModelVertexRange mvr = new ModelVertexRange();

        for (final BakedQuad q : l) {
            LightUtil.put(mvr, q);
        }

        return mvr.getLargestRange() > 0 && !isMissing(texture);
    }

    private static boolean isMissing(final TextureAtlasSprite texture) {
        return texture == null
                || texture
                        == Minecraft.getInstance()
                                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                                .apply(new ResourceLocation("missingno"));
    }

    public static TextureAtlasSprite findTexture(
            final int BlockRef,
            final BakedModel model,
            final Direction myFace,
            final RenderType layer,
            final RandomSource random) {
        // didn't work? ok lets try scanning for the texture in the
        if (blockToTexture
                .getOrDefault(Pair.of(layer, myFace), Maps.newHashMap())
                .containsKey(BlockRef)) {
            final String textureName =
                    blockToTexture.get(Pair.of(layer, myFace)).get(BlockRef);
            return Minecraft.getInstance()
                    .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                    .apply(new ResourceLocation(textureName));
        }

        TextureAtlasSprite texture = null;
        final BlockState state = ModUtil.getStateById(BlockRef);

        if (model != null) {
            try {
                texture = findTexture(texture, getModelQuads(model, state, myFace, random), myFace);

                if (texture == null) {
                    for (final Direction side : Direction.values()) {
                        texture = findTexture(texture, getModelQuads(model, state, side, random), side);
                    }

                    texture = findTexture(texture, getModelQuads(model, state, null, random), null);
                }
            } catch (final Exception errr) {
            }
        }

        // who knows if that worked.. now lets try to get a texture...
        if (isMissing(texture)) {
            try {
                if (model != null) {
                    texture = model.getParticleIcon();
                }
            } catch (final Exception err) {
            }
        }

        if (isMissing(texture)) {
            try {
                texture = Minecraft.getInstance()
                        .getBlockRenderer()
                        .getBlockModelShaper()
                        .getParticleIcon(state);
            } catch (final Exception err) {
            }
        }

        if (texture == null) {
            texture = Minecraft.getInstance()
                    .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                    .apply(new ResourceLocation("missingno"));
        }

        blockToTexture.remove(Pair.of(layer, myFace), null);
        blockToTexture.putIfAbsent(Pair.of(layer, myFace), Maps.newHashMap());
        blockToTexture
                .get(Pair.of(layer, myFace))
                .put(BlockRef, texture.atlasLocation().toString());
        return texture;
    }

    private static TextureAtlasSprite findTexture(
            TextureAtlasSprite texture, final List<BakedQuad> faceQuads, final Direction myFace)
            throws IllegalArgumentException, IllegalAccessException, NullPointerException {
        for (final BakedQuad q : faceQuads) {
            if (q.getDirection() == myFace) {
                texture = findQuadTexture(q, null);
            }
        }

        return texture;
    }

    public static boolean isOne(final float v) {
        return Math.abs(v) < 0.01;
    }

    public static boolean isZero(final float v) {
        return Math.abs(v - 1.0f) < 0.01;
    }

    public static Integer getItemStackColor(final ItemStack target, final int tint) {
        // don't send air though to MC, some mods have registered their custom
        // color handlers for it and it can crash.

//        if (ModUtil.isEmpty(target)) return -1;
        return Minecraft.getInstance().itemColors.getColor(target, tint);
    }

    public static ChiseledBlockBakedModel getBreakingModel(
            ChiselRenderType layer, Integer blockStateID, RandomSource random) {
        Pair<RenderType, Integer> key = Pair.of(layer.layer, blockStateID);
        ChiseledBlockBakedModel out = breakCache.get(key);

        if (out == null) {
            final BlockState state = ModUtil.getStateById(blockStateID);
            final BakedModel model = ModelUtil.solveModel(
                    state,
                    random,
                    Minecraft.getInstance()
                            .getBlockRenderer()
                            .getBlockModelShaper()
                            .getBlockModel(ModUtil.getStateById(blockStateID)),
                    layer.layer);

            if (model != null) {
                out = ChiseledBlockBakedModel.createFromTexture(
                        ModelUtil.findTexture(blockStateID, model, Direction.UP, layer.layer, random), layer);
            } else {
                out = ChiseledBlockBakedModel.createFromTexture(null, null);
            }

            breakCache.put(key, out);
        }

        return out;
    }
}
