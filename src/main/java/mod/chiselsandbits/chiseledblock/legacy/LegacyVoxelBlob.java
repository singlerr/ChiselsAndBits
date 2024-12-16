package mod.chiselsandbits.chiseledblock.legacy;

import io.netty.buffer.Unpooled;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.zip.InflaterInputStream;
import mod.chiselsandbits.chiseledblock.data.VoxelBlob;
import mod.chiselsandbits.chiseledblock.serialization.BitStream;
import mod.chiselsandbits.chiseledblock.serialization.BlobSerilizationCache;
import net.minecraft.network.FriendlyByteBuf;

public class LegacyVoxelBlob extends VoxelBlob {

    @Override
    public void blobFromBytes(final byte[] bytes) throws IOException {
        final ByteArrayInputStream out = new ByteArrayInputStream(bytes);
        readLegacy(out);
    }

    private void readLegacy(final ByteArrayInputStream o) throws IOException, RuntimeException {
        final InflaterInputStream w = new InflaterInputStream(o);
        final ByteBuffer bb = BlobSerilizationCache.getCacheBuffer();

        int usedBytes = 0;
        int rv = 0;

        do {
            usedBytes += rv;
            rv = w.read(bb.array(), usedBytes, bb.limit() - usedBytes);
        } while (rv > 0);

        final FriendlyByteBuf header = new FriendlyByteBuf(Unpooled.wrappedBuffer(bb));

        int version = header.readVarInt();

        LegacyBlobSerializer bs = null;

        if (version == VERSION_COMPACT) {
            bs = new LegacyBlobSerializer(header);
        }

        final int byteOffset = header.readVarInt();
        final int bytesOfInterest = header.readVarInt();

        final BitStream bits =
                BitStream.valueOf(byteOffset, ByteBuffer.wrap(bb.array(), header.readerIndex(), bytesOfInterest));
        for (int x = 0; x < array_size; x++) {
            values[x] = bs.readVoxelStateID(bits); // src.get();
            noneAir.set(x, values[x] > 0);
        }

        w.close();
    }
}
