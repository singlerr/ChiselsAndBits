package mod.chiselsandbits.items;

import com.communi.suggestu.saecularia.caudices.core.block.IBlockWithWorldlyProperties;
import java.util.List;
import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.core.ClientSide;
import mod.chiselsandbits.helpers.LocalStrings;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

public class ItemWrench extends Item {

  public ItemWrench(Item.Properties properties) {
    super(properties.stacksTo(1).durability(1));
  }

  @Override
  @Environment(EnvType.CLIENT)
  public void appendHoverText(
      final ItemStack stack, final Level worldIn, final List<Component> tooltip,
      final TooltipFlag advanced) {
    super.appendHoverText(stack, worldIn, tooltip, advanced);
    ChiselsAndBits.getConfig()
        .getCommon()
        .helpText(
            LocalStrings.HelpWrench,
            tooltip,
            ClientSide.instance.getKeyName(Minecraft.getInstance().options.keyUse));
  }

  @Override
  public InteractionResult useOn(final UseOnContext context) {
    final Player player = context.getPlayer();
    final BlockPos pos = context.getClickedPos();
    final Direction side = context.getClickedFace();
    final Level world = context.getLevel();
    final ItemStack stack = context.getItemInHand();
    final InteractionHand hand = context.getHand();

    if (!player.mayUseItemAt(pos, side, stack) || !world.mayInteract(player, pos)) {
      return InteractionResult.FAIL;
    }

    final BlockState b = world.getBlockState(pos);
    if (b != null && !player.isShiftKeyDown()) {
      BlockState nb;

      if (b.getBlock() instanceof IBlockWithWorldlyProperties prop) {
        nb = prop.rotate(b, world, pos, Rotation.CLOCKWISE_90);
      } else {
        nb = b.rotate(Rotation.CLOCKWISE_90);
      }

      if (nb != b) {
        world.setBlockAndUpdate(pos, nb);
        stack.hurtAndBreak(1, player, playerEntity -> {
          playerEntity.broadcastBreakEvent(hand);
        });
        world.updateNeighborsAt(pos, b.getBlock());
        player.swing(hand);
        return InteractionResult.SUCCESS;
      }
    }
    return InteractionResult.FAIL;
  }
}
