package mod.chiselsandbits.render;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import mod.chiselsandbits.client.model.baked.BaseBakedBlockModel;
import mod.chiselsandbits.core.ClientSide;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ModelCombined extends BaseBakedBlockModel {

    private static final RandomSource COMBINED_RANDOM_MODEL = RandomSource.create();

    BakedModel[] merged;

    List<BakedQuad>[] face;
    List<BakedQuad> generic;

    boolean isSideLit;

    @SuppressWarnings("unchecked")
    public ModelCombined(final BakedModel... args) {
        face = new ArrayList[Direction.values().length];

        generic = new ArrayList<>();
        for (final Direction f : Direction.values()) {
            face[f.ordinal()] = new ArrayList<>();
        }

        merged = args;

        for (final BakedModel m : merged) {
            generic.addAll(m.getQuads(null, null, COMBINED_RANDOM_MODEL));
            for (final Direction f : Direction.values()) {
                face[f.ordinal()].addAll(m.getQuads(null, f, COMBINED_RANDOM_MODEL));
            }
        }

        isSideLit = Arrays.stream(args).anyMatch(BakedModel::usesBlockLight);
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        for (final BakedModel a : merged) {
            return a.getParticleIcon();
        }

        return ClientSide.instance.getMissingIcon();
    }

    @Override
    public List<BakedQuad> getQuads(
            @Nullable BlockState blockState, @Nullable Direction side, RandomSource randomSource) {
        if (side != null) {
            return face[side.ordinal()];
        }

        return generic;
    }

    @Override
    public boolean usesBlockLight() {
        return isSideLit;
    }
}
