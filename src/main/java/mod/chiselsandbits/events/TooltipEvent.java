package mod.chiselsandbits.events;

import java.util.List;
import mod.chiselsandbits.chiseledblock.BlockBitInfo;
import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.items.ItemMagnifyingGlass;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class TooltipEvent {

    public static void register() {
        ItemTooltipCallback.EVENT.register(TooltipEvent::onItemTooltip);
    }

    private static void onItemTooltip(ItemStack stack, TooltipFlag context, List<Component> lines) {
        if (Minecraft.getInstance().player != null
                && ChiselsAndBits.getConfig().getCommon().enableHelp.get())
            if (Minecraft.getInstance().player.getMainHandItem().getItem() instanceof ItemMagnifyingGlass
                    || Minecraft.getInstance().player.getOffhandItem().getItem() instanceof ItemMagnifyingGlass)
                if (stack.getItem() instanceof BlockItem) {
                    final BlockItem blockItem = (BlockItem) stack.getItem();
                    final Block block = blockItem.getBlock();
                    final BlockState blockState = block.defaultBlockState();
                    final BlockBitInfo.SupportsAnalysisResult result = BlockBitInfo.doSupportAnalysis(blockState);

                    lines.add(Component.literal(
                            result.isSupported()
                                    ? ChatFormatting.GREEN
                                            + result.getSupportedReason().getLocal()
                                            + ChatFormatting.RESET
                                    : ChatFormatting.RED
                                            + result.getUnsupportedReason().getLocal()
                                            + ChatFormatting.RESET));
                }
    }
}
