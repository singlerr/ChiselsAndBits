package mod.chiselsandbits.render.helpers;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.concurrent.ConcurrentHashMap;
import mod.chiselsandbits.render.cache.FormatInfo;
import mod.chiselsandbits.render.chiseledblock.IFaceBuilder;
import mod.chiselsandbits.utils.LightUtil;
import mod.chiselsandbits.utils.forge.IVertexConsumer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;

public class BitBakedQuad extends BakedQuad {
    public static final ConcurrentHashMap<VertexFormat, FormatInfo> formatData =
            new ConcurrentHashMap<VertexFormat, FormatInfo>();

    public BitBakedQuad(
            final float[][][] unpackedData,
            final int tint,
            final Direction orientation,
            final TextureAtlasSprite sprite) {
        super(packData(unpackedData), tint, orientation, sprite, true);
    }

    private static int[] packData(VertexFormat format, float[][][] unpackedData) {
        FormatInfo fi = formatData.get(format);

        if (fi == null) {
            fi = new FormatInfo(format);
            formatData.put(format, fi);
        }

        return fi.pack(unpackedData);
    }

    //    @Override
    //    public void pipe(
    //            final IVertexConsumer consumer )
    //    {
    //        final int[] eMap = LightUtil.mapFormats( consumer.getVertexFormat(), DefaultVertexFormats.BLOCK );
    //
    //        consumer.setTexture( sprite );
    //        consumer.setQuadTint( getTintIndex() );
    //        consumer.setQuadOrientation( getFace() );
    //        consumer.setApplyDiffuseLighting( true );
    //
    //        for ( int v = 0; v < 4; v++ )
    //        {
    //            for ( int e = 0; e < consumer.getVertexFormat().getElements().size(); e++ )
    //            {
    //                if ( eMap[e] != consumer.getVertexFormat().getElements().size() )
    //                {
    //                    consumer.put( e, getRawPart( v, eMap[e] ) );
    //                }
    //                else
    //                {
    //                    consumer.put( e );
    //                }
    //            }
    //        }
    //    }

    private static int[] packData(float[][][] unpackedData) {
        int[] packed = new int[DefaultVertexFormat.BLOCK.getIntegerSize() * 4];
        for (int v = 0; v < 4; v++) {
            for (int e = 0; e < DefaultVertexFormat.BLOCK.getElements().size(); e++) {
                LightUtil.pack(unpackedData[v][e], packed, DefaultVertexFormat.BLOCK, v, e);
            }
        }

        return packed;
    }

    private float[] getRawPart(int v, int i) {

        return formatData.get(DefaultVertexFormat.BLOCK).unpack(vertices, v, i);
    }

    private int[] buildProcessedVertexData() {
        int[] packed = new int[DefaultVertexFormat.BLOCK.getIntegerSize() * 4];
        for (int v = 0; v < 4; v++) {
            for (int e = 0; e < DefaultVertexFormat.BLOCK.getElements().size(); e++) {
                LightUtil.pack(getRawPart(v, e), packed, DefaultVertexFormat.BLOCK, v, e);
            }
        }

        return packed;
    }

    public static class Builder implements IVertexConsumer, IFaceBuilder {
        private final VertexFormat format;
        private float[][][] unpackedData;
        private int tint = -1;
        private Direction orientation;
        private int vertices = 0;
        private int elements = 0;

        public Builder(VertexFormat format) {
            this.format = format;
        }

        @Override
        public VertexFormat getVertexFormat() {
            return format;
        }

        @Override
        public void setQuadTint(final int tint) {
            this.tint = tint;
        }

        @Override
        public void setQuadOrientation(final Direction orientation) {
            this.orientation = orientation;
        }

        @Override
        public void put(int vertexIndex, int element, float... data) {
            put(element, data);
        }

        public void put(final int element, final float... data) {
            for (int i = 0; i < 4; i++) {
                if (i < data.length) {
                    unpackedData[vertices][element][i] = data[i];
                } else {
                    unpackedData[vertices][element][i] = 0;
                }
            }

            elements++;

            if (elements == getVertexFormat().getElements().size()) {
                vertices++;
                elements = 0;
            }
        }

        @Override
        public void begin() {
            if (format != getVertexFormat()) {
                throw new RuntimeException("Bad format, can only be CNB.");
            }

            unpackedData = new float[4][getVertexFormat().getElements().size()][4];
            tint = -1;
            orientation = null;

            vertices = 0;
            elements = 0;
        }

        @Override
        public BakedQuad create(final TextureAtlasSprite sprite) {
            return new BitBakedQuad(unpackedData, tint, orientation, sprite);
        }

        @Override
        public void setFace(final Direction myFace, final int tintIndex) {
            setQuadOrientation(myFace);
            setQuadTint(tintIndex);
        }

        @Override
        public void setApplyDiffuseLighting(final boolean diffuse) {}

        @Override
        public void setTexture(final TextureAtlasSprite texture) {}

        @Override
        public VertexFormat getFormat() {
            return format;
        }
    }
}
