package mod.chiselsandbits.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import mod.chiselsandbits.utils.forge.LegacyForgeBlockMapper;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.WorldData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelStorageSource.LevelStorageAccess.class)
public abstract class LevelStorageAccessMixin {

    @Inject(
            method =
                    "saveDataTag(Lnet/minecraft/core/RegistryAccess;Lnet/minecraft/world/level/storage/WorldData;Lnet/minecraft/nbt/CompoundTag;)V",
            at =
                    @At(
                            value = "INVOKE_ASSIGN",
                            target =
                                    "Lnet/minecraft/nbt/CompoundTag;put(Ljava/lang/String;Lnet/minecraft/nbt/Tag;)Lnet/minecraft/nbt/Tag;"))
    private void chiselsandbits$saveLegacyData(
            RegistryAccess registryAccess,
            WorldData worldData,
            CompoundTag compoundTag,
            CallbackInfo ci,
            @Local(ordinal = 1) CompoundTag tag) {
        if (LegacyForgeBlockMapper.getMap() != null) {
            String[] ids = LegacyForgeBlockMapper.getMap();
            ListTag idList = new ListTag();

            for (int i = 0; i < ids.length; i++) {
                if (ids[i] == null) {
                    continue;
                }

                CompoundTag idCompound = new CompoundTag();
                idCompound.putString("K", ids[i]);
                idCompound.putInt("V", i);

                idList.add(idCompound);
            }

            CompoundTag blocksCompound = new CompoundTag();
            blocksCompound.put("ids", idList);

            CompoundTag registriesCompound = new CompoundTag();
            registriesCompound.put("minecraft:blocks", blocksCompound);

            CompoundTag fmlCompound = new CompoundTag();
            fmlCompound.put("Registries", registriesCompound);

            tag.put("FML", fmlCompound);
        }
    }
}
