package mod.chiselsandbits.items;

import java.util.List;
import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.helpers.LocalStrings;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class ItemMagnifyingGlass extends Item {

    public ItemMagnifyingGlass(Properties properties) {
        super(properties.stacksTo(1));
    }

    //	@Environment(EnvType.CLIENT)
    //    @Override
    //    public Component getHighlightTip(final ItemStack item, final Component displayName)
    //    {
    //        if (Minecraft.getInstance().hitResult == null)
    //            return displayName;
    //
    //        if (Minecraft.getInstance().hitResult.getType() != HitResult.Type.BLOCK)
    //            return displayName;
    //
    //        final BlockHitResult rayTraceResult = (BlockHitResult) Minecraft.getInstance().hitResult;
    //        final BlockState state = Minecraft.getInstance().level.getBlockState(rayTraceResult.getBlockPos());
    //        final BlockBitInfo.SupportsAnalysisResult result = BlockBitInfo.doSupportAnalysis(state);
    //        return new TextComponent(
    //          result.isSupported() ?
    //            ChatFormatting.GREEN + result.getSupportedReason().getLocal() + ChatFormatting.RESET :
    //            ChatFormatting.RED + result.getUnsupportedReason().getLocal() + ChatFormatting.RESET
    //        );
    //    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendHoverText(
            final ItemStack stack, final Level worldIn, final List<Component> tooltip, final TooltipFlag advanced) {
        super.appendHoverText(stack, worldIn, tooltip, advanced);
        ChiselsAndBits.getConfig().getCommon().helpText(LocalStrings.HelpMagnifyingGlass, tooltip);
    }
}
