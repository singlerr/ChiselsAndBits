package mod.chiselsandbits.items;

import java.util.List;
import mod.chiselsandbits.chiseledblock.data.BitLocation;
import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.core.ClientSide;
import mod.chiselsandbits.core.ReflectionWrapper;
import mod.chiselsandbits.helpers.BitOperation;
import mod.chiselsandbits.helpers.ChiselToolType;
import mod.chiselsandbits.helpers.LocalStrings;
import mod.chiselsandbits.helpers.ModUtil;
import mod.chiselsandbits.interfaces.IChiselModeItem;
import mod.chiselsandbits.interfaces.IItemScrollWheel;
import mod.chiselsandbits.network.packets.PacketSetColor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;

public class ItemTapeMeasure extends Item implements IChiselModeItem, IItemScrollWheel {
    public ItemTapeMeasure(Item.Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendHoverText(
            final ItemStack stack, final Level worldIn, final List<Component> tooltip, final TooltipFlag advanced) {
        super.appendHoverText(stack, worldIn, tooltip, advanced);
        ChiselsAndBits.getConfig()
                .getCommon()
                .helpText(
                        LocalStrings.HelpTapeMeasure,
                        tooltip,
                        ClientSide.instance.getKeyName(Minecraft.getInstance().options.keyUse),
                        ClientSide.instance.getKeyName(Minecraft.getInstance().options.keyUse),
                        ClientSide.instance.getKeyName(Minecraft.getInstance().options.keyShift),
                        ClientSide.instance.getModeKey());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(
            final Level worldIn, final Player playerIn, final InteractionHand hand) {
        if (playerIn.isShiftKeyDown() && playerIn.getCommandSenderWorld().isClientSide) {
            ClientSide.instance.tapeMeasures.clear();
        }

        final ItemStack itemstack = playerIn.getItemInHand(hand);
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemstack);
    }

    @Override
    public InteractionResult useOn(final UseOnContext context) {
        if (context.getLevel().isClientSide) {
            if (context.getPlayer().isShiftKeyDown()) {
                ClientSide.instance.tapeMeasures.clear();
                return InteractionResult.SUCCESS;
            }

            final Pair<Vec3, Vec3> PlayerRay = ModUtil.getPlayerRay(context.getPlayer());
            final Vec3 ray_from = PlayerRay.getLeft();
            final Vec3 ray_to = PlayerRay.getRight();

            final ClipContext rayTraceContext = new ClipContext(
                    ray_from, ray_to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, context.getPlayer());

            final BlockHitResult mop =
                    context.getPlayer().getCommandSenderWorld().clip(rayTraceContext);
            if (mop.getType() == HitResult.Type.BLOCK) {
                final BitLocation loc = new BitLocation(mop, BitOperation.CHISEL);
                ClientSide.instance.pointAt(ChiselToolType.TAPEMEASURE, loc, context.getHand());
            } else return InteractionResult.FAIL;
        }

        return InteractionResult.SUCCESS;
    }

    //    @Override
    //    public Component getHighlightTip(final ItemStack item, final Component displayName)
    //    {
    //        if (EffectiveSide.get().isClient() && displayName instanceof MutableComponent &&
    // ChiselsAndBits.getConfig().getClient().itemNameModeDisplay.get() )
    //        {
    //            final MutableComponent formattableTextComponent = (MutableComponent) displayName;
    //            return formattableTextComponent.append(" - ").append(TapeMeasureModes.getMode( item
    // ).string.getLocal()).append(" - ").append(DeprecationHelper.translateToLocal( "chiselsandbits.color." +
    // getTapeColor( item ).getName()) );
    //        }
    //
    //        return displayName;
    //    }

    public DyeColor getTapeColor(final ItemStack item) {
        final CompoundTag compound = item.getTag();
        if (compound != null && compound.contains("color")) {
            try {
                return DyeColor.valueOf(compound.getString("color"));
            } catch (final IllegalArgumentException iae) {
                // nope!
            }
        }

        return DyeColor.WHITE;
    }

    @Override
    public void scroll(final Player player, final ItemStack stack, final int dwheel) {
        final DyeColor color = getTapeColor(stack);
        int next = color.ordinal() + (dwheel < 0 ? -1 : 1);

        if (next < 0) {
            next = DyeColor.values().length - 1;
        }

        if (next >= DyeColor.values().length) {
            next = 0;
        }

        final DyeColor col = DyeColor.values()[next];
        setTapeColor(stack, col);

        final PacketSetColor setColor = new PacketSetColor(
                col,
                ChiselToolType.TAPEMEASURE,
                ChiselsAndBits.getConfig().getClient().chatModeNotification.get());

        ChiselsAndBits.getNetworkChannel().sendToServer(setColor);
        ReflectionWrapper.instance.clearHighlightedStack();
    }

    public void setTapeColor(final ItemStack stack, final DyeColor color) {
        stack.addTagElement("color", StringTag.valueOf(color.getSerializedName()));
    }
}
