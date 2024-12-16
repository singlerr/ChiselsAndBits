package mod.chiselsandbits.mixin;

import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.io.IOException;
import java.util.Optional;
import mod.chiselsandbits.chiseledblock.data.VoxelBlob;
import mod.chiselsandbits.chiseledblock.legacy.LegacyVoxelBlob;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.datafix.fixes.ChunkPalettedStorageFix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(targets = "net.minecraft.util.datafix.fixes.ChunkPalettedStorageFix$UpgradeChunk")
public abstract class ChunkPalettedStorageFix$UpgradeMixin {

    @Unique
    private final Logger log = LoggerFactory.getLogger(ChunkPalettedStorageFix.class);

    @ModifyArg(
            method = "method_15665",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lit/unimi/dsi/fastutil/ints/Int2ObjectMap;put(ILjava/lang/Object;)Ljava/lang/Object;"),
            index = 1)
    private Object chiselsandbits$convert(Object obj) {
        Dynamic<?> dynamic = (Dynamic<?>) obj;
        Optional<String> id = dynamic.get("id").asString().result();
        if (id.isPresent()) {
            if (id.get().contains("chiselsandbits")) {
                // legacy chisels and bits block entity
                dynamic = dynamic.set(
                        "id",
                        new Dynamic<>(
                                (DynamicOps<Tag>) dynamic.getOps(), StringTag.valueOf("chiselsandbits:chiseled")));
            }
        }
        return dynamic;
    }

    @Unique
    private byte[] convertLegacyBlob(byte[] old) throws IOException {
        LegacyVoxelBlob blob = new LegacyVoxelBlob();
        blob.blobFromBytes(old);

        return blob.blobToBytes(VoxelBlob.VERSION_COMPACT_PALLETED);
    }
}
