package mod.chiselsandbits.debug;

import java.util.HashMap;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import mod.chiselsandbits.api.APIExceptions.CannotBeChiseled;
import mod.chiselsandbits.api.APIExceptions.InvalidBitItem;
import mod.chiselsandbits.api.APIExceptions.SpaceOccupied;
import mod.chiselsandbits.api.BitQueryResults;
import mod.chiselsandbits.api.IBitAccess;
import mod.chiselsandbits.api.IBitBrush;
import mod.chiselsandbits.api.IBitLocation;
import mod.chiselsandbits.api.IBitVisitor;
import mod.chiselsandbits.api.IChiselAndBitsAPI;
import mod.chiselsandbits.api.ItemType;
import mod.chiselsandbits.chiseledblock.data.VoxelBlob;
import mod.chiselsandbits.core.Log;
import mod.chiselsandbits.helpers.ModUtil;
import mod.chiselsandbits.items.ItemChiseledBit;
import mod.chiselsandbits.registry.ModItems;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public abstract class DebugAction {

    public static IChiselAndBitsAPI api;

    protected static void Msg(final Player player, final String msg) {
        final String side = FabricLoader.getInstance().getEnvironmentType().name() + ": ";
        player.sendSystemMessage(Component.literal(side + msg));
    }

    private static void apiAssert(final String name, final Player player, final boolean must_be_true) {
        if (!must_be_true) {
            Msg(player, name + " = false");
        }
    }

    public abstract void run(
            final @Nonnull Level w,
            final @Nonnull BlockPos pos,
            final @Nonnull Direction side,
            final double hitX,
            final double hitY,
            final double hitZ,
            @Nonnull Player player);

    enum Tests {
        canBeChiseled(new DebugAction.canBeChiseled()),
        createBitItem(new DebugAction.createBitItem()),
        getBit(new DebugAction.getBit()),
        listContents(new DebugAction.listContents()),
        getBitAccess(new DebugAction.getBitAccess()),
        setBitAccess(new DebugAction.setBitAccess()),
        isBlockChiseled(new DebugAction.isBlockChiseled()),
        ItemTests(new DebugAction.ItemTests()),
        Randomize(new DebugAction.Randomize()),
        Spin(new DebugAction.Spin()),
        Mirror(new DebugAction.Mirror()),
        getTileClass(new DebugAction.getTileClass()),
        occusionTest(new DebugAction.occlusionTest()),
        queryTest(new DebugAction.queryTest());

        final DebugAction which;

        Tests(final DebugAction action) {
            which = action;
        }
    }

    static class ItemTests extends DebugAction {

        @Override
        public void run(
                final Level w,
                final BlockPos pos,
                final Direction side,
                final double hitX,
                final double hitY,
                final double hitZ,
                final Player player) {
            final IBitAccess access = api.createBitItem(ModUtil.getEmptyStack());
            assert access != null;

            apiAssert(
                    "BIT_BAG",
                    player,
                    api.getItemType(new ItemStack(ModItems.ITEM_BIT_BAG_DEFAULT.get())) == ItemType.BIT_BAG);
            apiAssert(
                    "CHISEL",
                    player,
                    api.getItemType(new ItemStack(ModItems.ITEM_CHISEL_DIAMOND.get())) == ItemType.CHISEL);
            apiAssert(
                    "MIRROR_DESIGN 1",
                    player,
                    api.getItemType(new ItemStack(ModItems.ITEM_MIRROR_PRINT.get())) == ItemType.MIRROR_DESIGN);
            apiAssert(
                    "NEGATIVE_DESIGN 1",
                    player,
                    api.getItemType(new ItemStack(ModItems.ITEM_NEGATIVE_PRINT.get())) == ItemType.NEGATIVE_DESIGN);
            apiAssert(
                    "POSITIVE_DESIGN 1",
                    player,
                    api.getItemType(new ItemStack(ModItems.ITEM_POSITIVE_PRINT.get())) == ItemType.POSITIVE_DESIGN);
            apiAssert("WRENCH", player, api.getItemType(new ItemStack(ModItems.ITEM_WRENCH.get())) == ItemType.WRENCH);
            apiAssert(
                    "CHISLED_BIT-cobblestone",
                    player,
                    api.getItemType(ItemChiseledBit.createStack(
                                    ModUtil.getStateId(Blocks.COBBLESTONE.defaultBlockState()), 1, true))
                            == ItemType.CHISELED_BIT);
            apiAssert(
                    "CHISLED_BLOCK",
                    player,
                    api.getItemType(access.getBitsAsItem(Direction.UP, ItemType.CHISELED_BLOCK, false)) == null);
            apiAssert(
                    "MIRROR_DESIGN 2",
                    player,
                    api.getItemType(access.getBitsAsItem(null, ItemType.MIRROR_DESIGN, false)) == null);
            apiAssert(
                    "NEGATIVE_DESIGN 2",
                    player,
                    api.getItemType(access.getBitsAsItem(null, ItemType.NEGATIVE_DESIGN, false)) == null);
            apiAssert(
                    "POSITIVE_DESIGN 2",
                    player,
                    api.getItemType(access.getBitsAsItem(null, ItemType.POSITIVE_DESIGN, false)) == null);

            try {
                final ItemStack bitItem = api.getBitItem(Blocks.COBBLESTONE.defaultBlockState());
                final IBitBrush brush = api.createBrush(bitItem);
                access.setBitAt(0, 0, 0, brush);
            } catch (final InvalidBitItem e) {
                apiAssert("createBrush/getBitItem", player, false);
            } catch (final SpaceOccupied e) {
                apiAssert("setBitAt", player, false);
            }

            apiAssert(
                    "CHISLED_BLOCK 2",
                    player,
                    api.getItemType(access.getBitsAsItem(null, ItemType.CHISELED_BLOCK, false))
                            == ItemType.CHISELED_BLOCK);
            apiAssert(
                    "MIRROR_DESIGN 3",
                    player,
                    api.getItemType(access.getBitsAsItem(null, ItemType.MIRROR_DESIGN, false))
                            == ItemType.MIRROR_DESIGN);
            apiAssert(
                    "NEGATIVE_DESIGN 3",
                    player,
                    api.getItemType(access.getBitsAsItem(null, ItemType.NEGATIVE_DESIGN, false))
                            == ItemType.NEGATIVE_DESIGN);
            apiAssert(
                    "POSITIVE_DESIGN 3",
                    player,
                    api.getItemType(access.getBitsAsItem(null, ItemType.POSITIVE_DESIGN, false))
                            == ItemType.POSITIVE_DESIGN);
            apiAssert("WRENCH", player, api.getItemType(access.getBitsAsItem(null, ItemType.WRENCH, false)) == null);
        }
    }

    static class getTileClass extends DebugAction {

        @Override
        public void run(
                final Level w,
                final BlockPos pos,
                final Direction side,
                final double hitX,
                final double hitY,
                final double hitZ,
                final Player player) {
            final BlockEntity te = w.getBlockEntity(pos);
            if (te != null) {
                Msg(player, te.getClass().getName());
            }
        }
    }

    static class canBeChiseled extends DebugAction {

        @Override
        public void run(
                final Level w,
                final BlockPos pos,
                final Direction side,
                final double hitX,
                final double hitY,
                final double hitZ,
                final Player player) {
            Msg(player, "canBeChiseled = " + (api.canBeChiseled(w, pos) ? "true" : "false"));
        }
    }

    static class isBlockChiseled extends DebugAction {

        @Override
        public void run(
                final Level w,
                final BlockPos pos,
                final Direction side,
                final double hitX,
                final double hitY,
                final double hitZ,
                final Player player) {
            Msg(player, "isBlockChiseled = " + (api.isBlockChiseled(w, pos) ? "true" : "false"));
        }
    }

    static class getBitAccess extends DebugAction {

        @Override
        public void run(
                final Level w,
                final BlockPos pos,
                final Direction side,
                final double hitX,
                final double hitY,
                final double hitZ,
                final Player player) {
            final IBitLocation loc = api.getBitPos(hitX, hitY, hitZ, side, pos, true);

            try {
                final IBitAccess access = api.getBitAccess(w, loc.getBlockPos());
                final IBitBrush brush = api.createBrush(api.getBitItem(Blocks.COBBLESTONE.defaultBlockState()));

                access.setBitAt(loc.getBitX(), loc.getBitY(), loc.getBitZ(), brush);
                access.commitChanges(true);
            } catch (final CannotBeChiseled e) {
                Log.logError("FAIL", e);
            } catch (final SpaceOccupied e) {
                Log.logError("FAIL", e);
            } catch (final InvalidBitItem e) {
                Log.logError("FAIL", e);
            }
        }
    }

    static class queryTest extends DebugAction {

        @Override
        public void run(
                final Level w,
                final BlockPos pos,
                final Direction side,
                final double hitX,
                final double hitY,
                final double hitZ,
                final Player player) {
            final IBitLocation loc = api.getBitPos(hitX, hitY, hitZ, side, pos, false);

            try {
                final IBitAccess access = api.getBitAccess(w, loc.getBlockPos());

                output(player, access.queryBitRange(new BlockPos(-1, -1, -1), new BlockPos(16, 16, 16)));
                output(player, access.queryBitRange(new BlockPos(1, 1, 1), new BlockPos(14, 14, 14)));
                output(player, access.queryBitRange(new BlockPos(0, 15, 0), new BlockPos(15, 15, 15)));
                output(player, access.queryBitRange(new BlockPos(0, 15, 15), new BlockPos(15, 15, 15)));
                output(player, access.queryBitRange(new BlockPos(0, 0, 0), new BlockPos(15, 15, 0)));
            } catch (final CannotBeChiseled e) {
                Log.logError("FAIL", e);
            }
        }

        private void output(final Player player, final BitQueryResults queryBitRange) {
            Msg(
                    player,
                    queryBitRange.total + " = e" + queryBitRange.empty + " + s" + queryBitRange.solid + " + f"
                            + queryBitRange.fluid);
        }
    }

    static class setBitAccess extends DebugAction {

        @Override
        public void run(
                final Level w,
                final BlockPos pos,
                final Direction side,
                final double hitX,
                final double hitY,
                final double hitZ,
                final Player player) {
            final IBitLocation loc = api.getBitPos(hitX, hitY, hitZ, side, pos, false);

            try {
                final IBitAccess access = api.getBitAccess(w, loc.getBlockPos());
                final IBitBrush brush = api.createBrush(ModUtil.getEmptyStack());

                access.setBitAt(loc.getBitX(), loc.getBitY(), loc.getBitZ(), brush);
                access.commitChanges(true);
            } catch (final CannotBeChiseled e) {
                Log.logError("FAIL", e);
            } catch (final SpaceOccupied e) {
                Log.logError("FAIL", e);
            } catch (final InvalidBitItem e) {
                Log.logError("FAIL", e);
            }
        }
    }

    static class Randomize extends DebugAction {

        @Override
        public void run(
                final Level w,
                final BlockPos pos,
                final Direction side,
                final double hitX,
                final double hitY,
                final double hitZ,
                final Player player) {
            final IBitLocation loc = api.getBitPos(hitX, hitY, hitZ, side, pos, false);

            try {
                final IBitAccess access = api.getBitAccess(w, loc.getBlockPos());

                access.visitBits(new IBitVisitor() {

                    @Override
                    public IBitBrush visitBit(final int x, final int y, final int z, final IBitBrush currentValue) {
                        IBitBrush bit = currentValue;
                        final BlockState state = Blocks.BLACK_WOOL.defaultBlockState();

                        try {
                            bit = api.createBrush(api.getBitItem(state));
                        } catch (final InvalidBitItem e) {
                        }

                        return y % 2 == 0 ? currentValue : bit;
                    }
                });

                access.commitChanges(true);
            } catch (final CannotBeChiseled e) {
                Log.logError("FAIL", e);
            }
        }
    }

    static class Mirror extends DebugAction {

        @Override
        public void run(
                final Level w,
                final BlockPos pos,
                final Direction side,
                final double hitX,
                final double hitY,
                final double hitZ,
                final Player player) {
            final IBitLocation loc = api.getBitPos(hitX, hitY, hitZ, side, pos, false);

            try {
                final IBitAccess access = api.getBitAccess(w, loc.getBlockPos());

                access.mirror(side.getAxis());

                access.commitChanges(true);
            } catch (final CannotBeChiseled e) {
                Log.logError("FAIL", e);
            }
        }
    }

    static class Spin extends DebugAction {

        @Override
        public void run(
                final Level w,
                final BlockPos pos,
                final Direction side,
                final double hitX,
                final double hitY,
                final double hitZ,
                final Player player) {
            final IBitLocation loc = api.getBitPos(hitX, hitY, hitZ, side, pos, false);

            try {
                final IBitAccess access = api.getBitAccess(w, loc.getBlockPos());

                access.rotate(side.getAxis(), Rotation.CLOCKWISE_90);

                access.commitChanges(true);
            } catch (final CannotBeChiseled e) {
                Log.logError("FAIL", e);
            }
        }
    }

    static class listContents extends DebugAction {

        @Override
        public void run(
                final Level w,
                final BlockPos pos,
                final Direction side,
                final double hitX,
                final double hitY,
                final double hitZ,
                final Player player) {
            final IBitLocation loc = api.getBitPos(hitX, hitY, hitZ, side, pos, false);

            try {
                final HashMap<Integer, ItemStack> bucket = new HashMap<Integer, ItemStack>();
                final IBitAccess access = api.getBitAccess(w, loc.getBlockPos());

                access.visitBits(new IBitVisitor() {

                    @Override
                    public IBitBrush visitBit(final int x, final int y, final int z, final IBitBrush currentValue) {
                        if (bucket.containsKey(currentValue.getStateID())) {
                            ModUtil.adjustStackSize(bucket.get(currentValue.getStateID()), 1);
                        } else {
                            bucket.put(currentValue.getStateID(), currentValue.getItemStack(1));
                        }

                        return currentValue;
                    }
                });

                for (Entry<Integer, ItemStack> is : bucket.entrySet()) {
                    player.inventory.add(is.getValue());
                }
            } catch (final CannotBeChiseled e) {
                Log.logError("FAIL", e);
            }
        }
    }

    static class getBit extends DebugAction {

        @Override
        public void run(
                final Level w,
                final BlockPos pos,
                final Direction side,
                final double hitX,
                final double hitY,
                final double hitZ,
                final Player player) {
            final IBitLocation loc = api.getBitPos(hitX, hitY, hitZ, side, pos, false);

            try {
                final IBitAccess access = api.getBitAccess(w, loc.getBlockPos());
                final IBitBrush brush = access.getBitAt(loc.getBitX(), loc.getBitY(), loc.getBitZ());

                if (brush == null) {
                    Msg(player, "AIR!");
                } else {
                    final BlockState state = brush.getState();
                    if (state != null) {
                        final Block blk = state.getBlock();

                        final ItemStack it = brush.getItemStack(1);

                        if (it != null && it.getItem() != null) {
                            player.inventory.add(it);
                        }

                        player.inventory.add(new ItemStack(blk, 1));
                    }
                }
            } catch (final CannotBeChiseled e) {
                Log.logError("FAIL", e);
            }
        }
    }

    static class occlusionTest extends DebugAction {

        @Override
        public void run(
                final Level w,
                final BlockPos pos,
                final Direction side,
                final double hitX,
                final double hitY,
                final double hitZ,
                final Player player) {
            final IBitLocation loc = api.getBitPos(hitX, hitY, hitZ, side, pos, false);

            final VoxelBlob out = new VoxelBlob();

            player.sendSystemMessage(Component.literal(out.filled() + " blocked"));
            player.sendSystemMessage(Component.literal(out.air() + " not-blocked"));
        }
    }

    static class createBitItem extends DebugAction {

        @Override
        public void run(
                final Level w,
                final BlockPos pos,
                final Direction side,
                final double hitX,
                final double hitY,
                final double hitZ,
                final Player player) {
            final IBitLocation loc = api.getBitPos(hitX, hitY, hitZ, side, pos, false);

            try {
                final IBitAccess access = api.getBitAccess(w, loc.getBlockPos());

                player.inventory.add(access.getBitsAsItem(side, ItemType.CHISELED_BLOCK, false));
                player.inventory.add(access.getBitsAsItem(side, ItemType.MIRROR_DESIGN, false));
                player.inventory.add(access.getBitsAsItem(side, ItemType.NEGATIVE_DESIGN, false));
                player.inventory.add(access.getBitsAsItem(side, ItemType.POSITIVE_DESIGN, false));
            } catch (final CannotBeChiseled e) {
                Log.logError("FAIL", e);
            }
        }
    }
}
