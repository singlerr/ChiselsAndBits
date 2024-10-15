package mod.chiselsandbits.render.helpers;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import mod.chiselsandbits.render.chiseledblock.IFaceBuilder;
import mod.chiselsandbits.utils.LightUtil;
import mod.chiselsandbits.utils.forge.IVertexConsumer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;

public final class BakedQuadBuilder implements IVertexConsumer, IFaceBuilder {
  private static final int SIZE = DefaultVertexFormat.BLOCK.getElements().size();

  private float[][][] unpackedData = new float[4][SIZE][4];
  private int tint = -1;
  private Direction orientation;
  private TextureAtlasSprite texture;
  private boolean applyDiffuseLighting = true;

  private int vertices = 0;
  private int elements = 0;
  private boolean full = false;

  private VertexFormat vertexFormat;

  public BakedQuadBuilder(TextureAtlasSprite texture) {
    this.texture = texture;
  }

  public BakedQuadBuilder(VertexFormat vertexFormat) {
    this.vertexFormat = vertexFormat;
  }

  @Override
  public VertexFormat getVertexFormat() {
    return vertexFormat;
  }

  public void setVertexFormat(VertexFormat vertexFormat) {
    this.vertexFormat = vertexFormat;
  }

  @Override
  public void setQuadTint(int tint) {
    this.tint = tint;
  }

  @Override
  public void setQuadOrientation(Direction orientation) {
    this.orientation = orientation;
  }

  @Override
  public void setTexture(TextureAtlasSprite texture) {
    this.texture = texture;
  }

  @Override
  public void setApplyDiffuseLighting(boolean diffuse) {
    this.applyDiffuseLighting = diffuse;
  }

  @Override
  public void put(int vertexIndex, int element, float... data) {
    //        for (int i = 0; i < 4; i++) {
    //            if (i < data.length) {
    //                unpackedData[vertexIndex][element][i] = data[i];
    //            } else {
    //                unpackedData[vertexIndex][element][i] = 0;
    //            }
    //        }
    //        elements++;
    //        if (elements == SIZE) {
    //            elements = 0;
    //        }
    //        if (vertexIndex == 4) {
    //            full = true;
    //        }
    put(element, data);
  }

  @Override
  public void setFace(Direction myFace, int tintIndex) {
    setQuadOrientation(myFace);
    setQuadTint(tintIndex);
  }

  @Override
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
    unpackedData = new float[4][getVertexFormat().getElements().size()][4];
    tint = -1;
    orientation = null;
    texture = null;
    vertices = 0;
    elements = 0;
  }

  @Override
  public BakedQuad create(TextureAtlasSprite sprite) {
    setTexture(sprite);
    return build();
  }

  @Override
  public VertexFormat getFormat() {
    return vertexFormat;
  }

  public BakedQuad build() {
    if (texture == null) {
      throw new IllegalStateException("texture not set");
    }

    int[] packed = new int[DefaultVertexFormat.BLOCK.getIntegerSize() * 4];
    for (int v = 0; v < 4; v++) {
      for (int e = 0; e < SIZE; e++) {
        LightUtil.pack(unpackedData[v][e], packed, DefaultVertexFormat.BLOCK, v, e);
      }
    }

    return new BakedQuad(packed, tint, orientation, texture, applyDiffuseLighting);
  }
}
