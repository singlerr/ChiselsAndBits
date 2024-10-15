package mod.chiselsandbits.utils;

import java.util.List;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class SingleBlockWorldReader extends SingleBlockBlockReader implements LevelReader {
  private final LevelReader reader;

  public SingleBlockWorldReader(final BlockState state, final Block blk, final LevelReader reader) {
    super(state, blk);
    this.reader = reader;
  }

  @Nullable
  @Override
  public ChunkAccess getChunk(final int x, final int z, final ChunkStatus requiredStatus,
                              final boolean nonnull) {
    return this.reader.getChunk(x, z, requiredStatus, nonnull);
  }

  @Override
  public boolean hasChunk(final int chunkX, final int chunkZ) {
    return this.reader.hasChunk(chunkX, chunkZ);
  }

  @Override
  public int getHeight(final Heightmap.Types heightmapType, final int x, final int z) {
    return this.reader.getHeight(heightmapType, x, z);
  }

  @Override
  public int getSkyDarken() {
    return 15;
  }

  @Override
  public BiomeManager getBiomeManager() {
    return this.reader.getBiomeManager();
  }

  @Override
  public Holder<Biome> getNoiseBiome(int i, int j, int k) {
    return reader.getNoiseBiome(i, j, k);
  }

  @Override
  public Holder<Biome> getUncachedNoiseBiome(int i, int j, int k) {
    return this.reader.getUncachedNoiseBiome(i, j, k);
  }

  @Override
  public boolean isClientSide() {
    return this.reader.isClientSide();
  }

  @Override
  public int getSeaLevel() {
    return 64;
  }

  @Override
  public DimensionType dimensionType() {
    return this.reader.dimensionType();
  }

  @Override
  public RegistryAccess registryAccess() {
    return reader.registryAccess();
  }

  @Override
  public FeatureFlagSet enabledFeatures() {
    return reader.enabledFeatures();
  }

  @Override
  public float getShade(final Direction p_230487_1_, final boolean p_230487_2_) {
    return this.reader.getShade(p_230487_1_, p_230487_2_);
  }

  @Override
  public LevelLightEngine getLightEngine() {
    return this.reader.getLightEngine();
  }

  @Override
  public WorldBorder getWorldBorder() {
    return this.reader.getWorldBorder();
  }

  @Override
  public List<VoxelShape> getEntityCollisions(@Nullable Entity entity, AABB aABB) {
    return reader.getEntityCollisions(entity, aABB);
  }
}
