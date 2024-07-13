package mod.chiselsandbits.debug;

import mod.chiselsandbits.debug.DebugAction.Tests;
import mod.chiselsandbits.helpers.ModUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;

public class ItemApiDebug extends Item {

    public ItemApiDebug(Item.Properties properties) {
        super(properties.durability(1).stacksTo(1));
    }

    @Override
    public Component getName(final ItemStack stack) {
        final Component parent = super.getName(stack);
        if (!(parent instanceof MutableComponent)) return parent;

        final MutableComponent name = (MutableComponent) parent;
        return name.append(" - " + getAction(stack).name());
    }

    private Tests getAction(final ItemStack stack) {
        return Tests.values()[getActionID(stack)];
    }

    @Override
    public InteractionResult useOn(final UseOnContext context) {
        final ItemStack stack = context.getPlayer().getItemInHand(context.getHand());

        if (context.getPlayer().isShiftKeyDown()) {
            final int newDamage = getActionID(stack) + 1;
            setActionID(stack, newDamage % Tests.values().length);
            DebugAction.Msg(context.getPlayer(), getAction(stack).name());
            return InteractionResult.SUCCESS;
        }

        getAction(stack)
                .which
                .run(
                        context.getLevel(),
                        context.getClickedPos(),
                        context.getClickedFace(),
                        context.getClickLocation().x,
                        context.getClickLocation().y,
                        context.getClickLocation().z,
                        context.getPlayer());
        return InteractionResult.SUCCESS;
    }

    private void setActionID(final ItemStack stack, final int i) {
        final CompoundTag o = new CompoundTag();
        o.putInt("id", i);
        stack.setTag(o);
    }

    private int getActionID(final ItemStack stack) {
        if (stack.hasTag()) {
            return ModUtil.getTagCompound(stack).getInt("id");
        }

        return 0;
    }
}
