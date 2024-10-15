package mod.chiselsandbits.render.helpers;

import com.mojang.blaze3d.vertex.VertexFormatElement;

public class ModelUVAverager extends BaseModelReader {
  private int vertCount = 0;
  private float sumU;
  private float sumV;

  public float getU() {
    return sumU / vertCount;
  }

  public float getV() {
    return sumV / vertCount;
  }

  @Override
  public void put(final int vertexIndex, final int element, final float... data) {
    final VertexFormatElement e = getVertexFormat().getElements().get(element);
    if (e.getUsage() == VertexFormatElement.Usage.UV && e.getIndex() != 1) {
      sumU += data[0];
      sumV += data[1];
      ++vertCount;
    }
  }
}
