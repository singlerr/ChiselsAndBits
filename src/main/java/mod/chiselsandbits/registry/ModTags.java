package mod.chiselsandbits.registry;

import mod.chiselsandbits.utils.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags {

    public static void init() {
        Items.init();
        Blocks.init();
    }

    public static final class Items {
        private static void init() {}

        public static TagKey<Item> CHISEL = tag("chisel");
        public static TagKey<Item> BIT_BAG = tag("bit_bag");

        private static TagKey<Item> tag(String name) {
            return ItemTags.bind(Constants.MOD_ID + ":" + name);
        }
    }

    public static final class Blocks {
        private static void init() {}

        public static TagKey<Block> FORCED_CHISELABLE = tag("chiselable/forced");
        public static TagKey<Block> BLOCKED_CHISELABLE = tag("chiselable/blocked");
        public static TagKey<Block> CHISELED_BLOCK = tag("chiseled/block");

        private static TagKey<Block> tag(String name) {
            return TagKey.create(Registries.BLOCK, new ResourceLocation(Constants.MOD_ID + ":" + name));
        }
    }
}
