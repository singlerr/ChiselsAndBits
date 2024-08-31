package mod.chiselsandbits.items;

import java.util.ArrayList;
import java.util.List;
import mod.chiselsandbits.chiseledblock.NBTBlobConverter;
import mod.chiselsandbits.chiseledblock.TileEntityBlockChiseled;
import mod.chiselsandbits.chiseledblock.data.VoxelBlob;
import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.core.ClientSide;
import mod.chiselsandbits.helpers.LocalStrings;
import mod.chiselsandbits.helpers.ModUtil;
import mod.chiselsandbits.interfaces.IPatternItem;
import mod.chiselsandbits.registry.ModBlocks;
import mod.chiselsandbits.registry.ModItems;
import mod.chiselsandbits.render.helpers.SimpleInstanceCache;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ItemMirrorPrint extends Item implements IPatternItem {

    public ItemMirrorPrint(Item.Properties properties) {
        super(properties);
    }

    SimpleInstanceCache<ItemStack, List<Component>> toolTipCache = new SimpleInstanceCache<>(null, new ArrayList<>());

    @Override
    @Environment(EnvType.CLIENT)
    public void appendHoverText(
            final ItemStack stack, final Level worldIn, final List<Component> tooltip, final TooltipFlag advanced) {
        super.appendHoverText(stack, worldIn, tooltip, advanced);
        ChiselsAndBits.getConfig()
                .getCommon()
                .helpText(
                        LocalStrings.HelpMirrorPrint,
                        tooltip,
                        ClientSide.instance.getKeyName(Minecraft.getInstance().options.keyUse));

        if (isWritten(stack)) {
            if (ClientSide.instance.holdingShift()) {
                if (toolTipCache.needsUpdate(stack)) {
                    final VoxelBlob blob = ModUtil.getBlobFromStack(stack, null);
                    toolTipCache.updateCachedValue(blob.listContents(new ArrayList<>()));
                }

                tooltip.addAll(toolTipCache.getCached());
            } else {
                tooltip.add(Component.literal(LocalStrings.ShiftDetails.getLocal()));
            }
        }
    }

    @Override
    public String getDescriptionId(final ItemStack stack) {
        return super.getDescriptionId(stack);
    }

    @Override
    public InteractionResult useOn(final UseOnContext context) {
        final ItemStack stack = context.getPlayer().getItemInHand(context.getHand());

        if (!context.getPlayer().mayUseItemAt(context.getClickedPos(), context.getClickedFace(), stack)) {
            return InteractionResult.SUCCESS;
        }

        if (!isWritten(stack)) {
            final CompoundTag comp = getCompoundFromBlock(
                    context.getLevel(), context.getClickedPos(), context.getPlayer(), context.getClickedFace());
            if (comp != null) {
                stack.shrink(1);

                final ItemStack newStack = new ItemStack(ModItems.ITEM_MIRROR_PRINT_WRITTEN.get(), 1);
                newStack.setTag(comp);

                final ItemEntity entity = context.getPlayer().drop(newStack, true);
                entity.setPickUpDelay(0);
                entity.setThrower(context.getPlayer());

                return InteractionResult.SUCCESS;
            }

            return InteractionResult.FAIL;
        }

        return InteractionResult.FAIL;
    }

    protected CompoundTag getCompoundFromBlock(
            final Level world, final BlockPos pos, final Player player, final Direction face) {
        final TileEntityBlockChiseled te = ModUtil.getChiseledTileEntity(world, pos, false);

        if (te != null) {
            final CompoundTag comp = new CompoundTag();
            te.writeChiselData(comp);

            final TileEntityBlockChiseled tmp = new TileEntityBlockChiseled(pos, world.getBlockState(pos));
            tmp.readChiselData(comp);

            final VoxelBlob bestBlob = tmp.getBlob();
            tmp.setBlob(bestBlob.mirror(face.getAxis()));
            tmp.writeChiselData(comp);

            comp.putByte(ModUtil.NBT_SIDE, (byte) ModUtil.getPlaceFace(player).ordinal());
            return comp;
        }

        return null;
    }

    @Override
    public ItemStack getPatternedItem(final ItemStack stack, final boolean wantRealItems) {
        if (!isWritten(stack)) {
            return null;
        }

        final CompoundTag tag = ModUtil.getTagCompound(stack);

        // Detect and provide full blocks if pattern solid full and solid.
        final NBTBlobConverter conv = new NBTBlobConverter();
        conv.readChisleData(tag, VoxelBlob.VERSION_ANY);

        final BlockState blk = conv.getPrimaryBlockState();
        final ItemStack itemstack = new ItemStack(ModBlocks.getChiseledBlock(), 1);

        itemstack.addTagElement(ModUtil.NBT_BLOCKENTITYTAG, tag);
        return itemstack;
    }

    @Override
    public boolean isWritten(final ItemStack stack) {
        return stack.getItem() == ModItems.ITEM_MIRROR_PRINT_WRITTEN.get() && stack.hasTag();
    }
}
