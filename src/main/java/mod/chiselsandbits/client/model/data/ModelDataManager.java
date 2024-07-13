package mod.chiselsandbits.client.model.data;

import com.google.common.base.Preconditions;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.jetbrains.annotations.NotNull;

public class ModelDataManager {
    private static WeakReference<BlockAndTintGetter> currentLevel = new WeakReference<>(null);

    private static final Map<ChunkPos, Set<BlockPos>> needModelDataRefresh = new ConcurrentHashMap<>();

    private static final Map<ChunkPos, Map<BlockPos, IModelData>> modelDataCache = new ConcurrentHashMap<>();

    private static void cleanCaches(BlockAndTintGetter level) {
        Preconditions.checkNotNull(level, "Level must not be null");
        Preconditions.checkArgument(
                level == Minecraft.getInstance().level,
                "Cannot use model data for a level other than the current client level");
        if (level != currentLevel.get()) {
            currentLevel = new WeakReference<>(level);
            needModelDataRefresh.clear();
            modelDataCache.clear();
        }
    }

    public static void requestModelDataRefresh(BlockEntity te) {
        Preconditions.checkNotNull(te, "Tile entity must not be null");
        Level level = te.getLevel();

        cleanCaches(level);
        needModelDataRefresh
                .computeIfAbsent(new ChunkPos(te.getBlockPos()), $ -> Collections.synchronizedSet(new HashSet<>()))
                .add(te.getBlockPos());
    }

    private static void refreshModelData(BlockAndTintGetter level, ChunkPos chunk) {
        cleanCaches(level);
        Set<BlockPos> needUpdate = needModelDataRefresh.remove(chunk);

        if (needUpdate != null) {
            Map<BlockPos, IModelData> data = modelDataCache.computeIfAbsent(chunk, $ -> new ConcurrentHashMap<>());
            for (BlockPos pos : needUpdate) {
                BlockEntity toUpdate = level.getBlockEntity(pos);
                if (toUpdate != null && !toUpdate.isRemoved()) {

                    data.put(pos, (IModelData) level.getBlockEntityRenderData(pos));
                } else {
                    data.remove(pos);
                }
            }
        }
    }

    public static void onChunkUnload(ChunkAccess access, LevelAccessor world) {
        if (!world.isClientSide()) return;

        ChunkPos chunk = access.getPos();
        needModelDataRefresh.remove(chunk);
        modelDataCache.remove(chunk);
    }

    public static @Nullable IModelData getModelData(BlockAndTintGetter level, BlockPos pos) {
        return getModelData(level, new ChunkPos(pos)).get(pos);
    }

    public static @NotNull IModelData getModelDataOrEmpty(BlockAndTintGetter level, BlockPos pos) {
        return getModelData(level, new ChunkPos(pos)).getOrDefault(pos, EmptyModelData.INSTANCE);
    }

    public static Map<BlockPos, IModelData> getModelData(BlockAndTintGetter level, ChunkPos pos) {
        refreshModelData(level, pos);
        return modelDataCache.getOrDefault(pos, Collections.emptyMap());
    }
}
