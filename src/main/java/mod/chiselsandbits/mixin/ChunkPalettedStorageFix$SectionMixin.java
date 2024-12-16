package mod.chiselsandbits.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.serialization.Dynamic;
import java.nio.ByteBuffer;
import mod.chiselsandbits.utils.forge.LegacyForgeBlockMapper;
import net.minecraft.util.datafix.fixes.BlockStateData;
import net.minecraft.util.datafix.fixes.ChunkPalettedStorageFix;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net.minecraft.util.datafix.fixes.ChunkPalettedStorageFix$Section")
public abstract class ChunkPalettedStorageFix$SectionMixin {

    @Shadow
    public abstract void setBlock(int i, Dynamic<?> dynamic);

    @Redirect(
            method = "upgrade",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/util/datafix/fixes/ChunkPalettedStorageFix$Section;setBlock(ILcom/mojang/serialization/Dynamic;)V"))
    private void chiselsandbits$fixLegacy(
            ChunkPalettedStorageFix.Section instance,
            int i,
            Dynamic<?> dynamic,
            @Local ByteBuffer buffer,
            @Local(ordinal = 1) ChunkPalettedStorageFix.DataLayer dataLayer) {

        if (LegacyForgeBlockMapper.getMap() == null) {
            setBlock(i, dynamic);
            return;
        }

        int k = i & 15;
        int l = i >> 8 & 15;
        int m = i >> 4 & 15;
        int id = ((buffer.get(i) & 255)) + (dataLayer.get(k, l, m) << 8);

        String legacyName = LegacyForgeBlockMapper.getMap()[id];
        if (legacyName != null && legacyName.startsWith("chiselsandbits:chiseled_")) {
            Dynamic<?> block = BlockStateData.parse("{Name:'chiselsandbits:chiseled_block'}");
            setBlock(i, block);
        }
    }
}
