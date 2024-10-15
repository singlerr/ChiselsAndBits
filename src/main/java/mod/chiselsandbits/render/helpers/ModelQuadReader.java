package mod.chiselsandbits.render.helpers;

import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import java.util.Arrays;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;

public class ModelQuadReader extends BaseModelReader {

  int minX = 16;
  int minY = 16;
  int minZ = 16;
  int maxX = 0;
  int maxY = 0;
  int maxZ = 0;

  int u;
  int v;

  int[][] pos_uv = new int[4][5];
  TextureAtlasSprite sprite;
  String texture;

  Direction face;
  Direction cull;
  float[] pos;
  float[] uv;
  int index = 0;
  public ModelQuadReader(
      final String textureName, final TextureAtlasSprite texture, final Direction face,
      final Direction cull) {
    sprite = texture;
    this.texture = textureName;
    this.face = face;
    this.cull = cull;
  }

  @Override
  public void put(final int vertexIndex, final int element, final float... data) {
    final VertexFormat format = getVertexFormat();
    final VertexFormatElement ele = format.getElements().get(element);

    if (ele.getUsage() == VertexFormatElement.Usage.UV && ele.getIndex() != 1) {
      uv = Arrays.copyOf(data, data.length);
    } else if (ele.getUsage() == VertexFormatElement.Usage.POSITION) {
      pos = Arrays.copyOf(data, data.length);
    }

    if (element == format.getElements().size() - 1) {
      pos_uv[index][0] = Math.round(pos[0] * 16);
      pos_uv[index][1] = Math.round(pos[1] * 16);
      pos_uv[index][2] = Math.round(pos[2] * 16);
      pos_uv[index][3] =
          Math.round((uv[0] - sprite.getU0()) / (sprite.getU1() - sprite.getU0()) * 16);
      pos_uv[index][4] =
          Math.round((uv[1] - sprite.getV0()) / (sprite.getV1() - sprite.getV0()) * 16);

      minX = Math.min(minX, pos_uv[index][0]);
      minY = Math.min(minY, pos_uv[index][1]);
      minZ = Math.min(minZ, pos_uv[index][2]);
      maxX = Math.max(maxX, pos_uv[index][0]);
      maxY = Math.max(maxY, pos_uv[index][1]);
      maxZ = Math.max(maxZ, pos_uv[index][2]);

      index++;
    }
  }

  public String toString(Direction faceQuad) {
    int U1 = 0, V1 = 16, U2 = 16, V2 = 0;

    for (int idx = 0; idx < 4; idx++) {
      if (matches(minX, minY, minZ, pos_uv[idx])) {
        U1 = pos_uv[idx][3];
        V2 = pos_uv[idx][4];
      } else if (matches(maxX, maxY, maxZ, pos_uv[idx])) {
        U2 = pos_uv[idx][3];
        V1 = pos_uv[idx][4];
      }
    }

    if (faceQuad.get2DDataValue() > 1) {
      final int tempU = U1;
      U1 = U2;
      U2 = tempU;
    } else if (faceQuad == Direction.UP) {
      final int tempV = V1;
      V1 = V2;
      V2 = tempV;
    }

    if (cull == null) {
      return new StringBuilder("{ \"from\": [")
          .append(minX)
          .append(",")
          .append(minY)
          .append(",")
          .append(minZ)
          .append("], \"to\": [")
          .append(maxX)
          .append(",")
          .append(maxY)
          .append(",")
          .append(maxZ)
          .append("], \"faces\": { \"")
          .append(face.getSerializedName())
          .append("\":  { \"uv\": [")
          .append(U1)
          .append(",")
          .append(V1)
          .append(",")
          .append(U2)
          .append(",")
          .append(V2)
          .append("], \"texture\": \"")
          .append(texture)
          .append("\" } } },\n")
          .toString();
    } else {
      return new StringBuilder("{ \"from\": [")
          .append(minX)
          .append(",")
          .append(minY)
          .append(",")
          .append(minZ)
          .append("], \"to\": [")
          .append(maxX)
          .append(",")
          .append(maxY)
          .append(",")
          .append(maxZ)
          .append("], \"faces\": { \"")
          .append(face.getSerializedName())
          .append("\":  { \"uv\": [")
          .append(U1)
          .append(",")
          .append(V1)
          .append(",")
          .append(U2)
          .append(",")
          .append(V2)
          .append("], \"texture\": \"")
          .append(texture)
          .append("\", \"cullface\": \"")
          .append(cull.getSerializedName())
          .append("\" } } },\n")
          .toString();
    }
  }

  private boolean matches(final int x, final int y, final int z, final int[] v) {
    return v[0] == x && v[1] == y && v[2] == z;
  }
}
