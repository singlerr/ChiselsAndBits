package mod.chiselsandbits.utils.forge;

import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.EndTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.TagVisitor;

public class LegacyForgeBlockMapper {

    private static String[] legacyIds = null;

    public static String[] getMap() {
        return legacyIds;
    }

    public static void parse(CompoundTag tag) {
        legacyIds = new String[4096];
        var registries = tag.getCompound("Registries");
        var blockRegistry = registries.getCompound("minecraft:blocks");

        var blockIds = blockRegistry.getList("ids", ListTag.TAG_COMPOUND);
        blockIds.forEach((element) -> {
            // this seems overcomplicated
            element.accept(new TagVisitor() {
                @Override
                public void visitCompound(CompoundTag compoundTag) {
                    var key = compoundTag.getString("K");
                    var value = compoundTag.getInt("V");
                    if (key.startsWith("chiselsandbits:")) {
                        legacyIds[value] = key;
                    }
                }

                @Override
                public void visitString(StringTag stringTag) {}

                @Override
                public void visitByte(ByteTag byteTag) {}

                @Override
                public void visitShort(ShortTag shortTag) {}

                @Override
                public void visitInt(IntTag intTag) {}

                @Override
                public void visitLong(LongTag longTag) {}

                @Override
                public void visitFloat(FloatTag floatTag) {}

                @Override
                public void visitDouble(DoubleTag doubleTag) {}

                @Override
                public void visitByteArray(ByteArrayTag byteArrayTag) {}

                @Override
                public void visitIntArray(IntArrayTag intArrayTag) {}

                @Override
                public void visitLongArray(LongArrayTag longArrayTag) {}

                @Override
                public void visitList(ListTag listTag) {}

                @Override
                public void visitEnd(EndTag endTag) {}
            });
        });
    }
}
