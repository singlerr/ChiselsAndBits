package mod.chiselsandbits.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import java.nio.file.Path;
import mod.chiselsandbits.utils.forge.LegacyForgeBlockMapper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelStorageSource.class)
public abstract class LevelStorageSourceMixin {

    @Inject(
            method = "readLevelDataTagFixed",
            at =
                    @At(
                            value = "INVOKE_ASSIGN",
                            target =
                                    "Lnet/minecraft/world/level/storage/LevelStorageSource;readLevelDataTagRaw(Ljava/nio/file/Path;)Lnet/minecraft/nbt/CompoundTag;",
                            shift = At.Shift.AFTER))
    private static void chiselsandbits$initForgeMapper(
            Path path, DataFixer dataFixer, CallbackInfoReturnable<Dynamic<?>> cir, @Local CompoundTag tag) {
        if (tag.contains("FML")) {
            LegacyForgeBlockMapper.parse(tag.getCompound("FML"));
        }
    }
}
