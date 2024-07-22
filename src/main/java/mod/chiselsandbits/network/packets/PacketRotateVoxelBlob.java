package mod.chiselsandbits.network.packets;

import mod.chiselsandbits.interfaces.IVoxelBlobItem;
import mod.chiselsandbits.network.ModPacket;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Rotation;

public class PacketRotateVoxelBlob extends ModPacket {

    private Direction.Axis axis;
    private Rotation rotation;

    public PacketRotateVoxelBlob(FriendlyByteBuf buffer) {
        readPayload(buffer);
    }

    public PacketRotateVoxelBlob(final Direction.Axis axis, final Rotation rotation) {
        this.axis = axis;
        this.rotation = rotation;
    }

    @Override
    public void server(final ServerPlayer player) {
        final ItemStack is = player.getMainHandItem();
        if (is != null && is.getItem() instanceof IVoxelBlobItem) {
            ((IVoxelBlobItem) is.getItem()).rotate(is, axis, rotation);
        }
    }

    @Override
    public void getPayload(final FriendlyByteBuf buffer) {
        buffer.writeEnum(axis);
        buffer.writeEnum(rotation);
    }

    @Override
    public void readPayload(final FriendlyByteBuf buffer) {
        axis = buffer.readEnum(Direction.Axis.class);
        rotation = buffer.readEnum(Rotation.class);
    }

    public Direction.Axis getAxis() {
        return axis;
    }

    public void setAxis(final Direction.Axis axis) {
        this.axis = axis;
    }

    public Rotation getRotation() {
        return rotation;
    }

    public void setRotation(final Rotation rotation) {
        this.rotation = rotation;
    }
}
