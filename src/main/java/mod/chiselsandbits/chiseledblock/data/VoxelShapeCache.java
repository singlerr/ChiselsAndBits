package mod.chiselsandbits.chiseledblock.data;

import java.util.BitSet;
import java.util.Objects;
import mod.chiselsandbits.api.BoxType;
import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.utils.SimpleMaxSizedCache;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class VoxelShapeCache {

  private static final VoxelShapeCache INSTANCE = new VoxelShapeCache();
  private final SimpleMaxSizedCache<VoxelShapeCache.CacheKey, VoxelShape> cache =
      new SimpleMaxSizedCache<>(
          ChiselsAndBits.getConfig().getCommon().collisionBoxCacheSize.get());

  private VoxelShapeCache() {
  }

  public static VoxelShapeCache getInstance() {
    return INSTANCE;
  }

  public VoxelShape get(VoxelBlob blob, BoxType type) {
    final CacheKey key = new CacheKey(type, (BitSet) blob.getNoneAir().clone());

    VoxelShape shape = cache.get(key);
    if (shape == null) {
      shape = calculateNewVoxelShape(blob, type);
      cache.put(key, shape);
    }

    return shape;
  }

  private VoxelShape calculateNewVoxelShape(final VoxelBlob data, final BoxType type) {
    return VoxelShapeCalculator.calculate(data, type).optimize();
  }

  private static final class CacheKey {
    private final BoxType type;
    private final BitSet noneAirMap;

    private CacheKey(final BoxType type, final BitSet noneAirMap) {
      this.type = type;
      this.noneAirMap = noneAirMap;
    }

    @Override
    public boolean equals(final Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof CacheKey cacheKey)) {
        return false;
      }
      return type == cacheKey.type && noneAirMap.equals(cacheKey.noneAirMap);
    }

    @Override
    public int hashCode() {
      return Objects.hash(type, noneAirMap);
    }
  }
}
