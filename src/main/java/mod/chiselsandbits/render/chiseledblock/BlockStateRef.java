package mod.chiselsandbits.render.chiseledblock;

import mod.chiselsandbits.chiseledblock.data.VoxelBlob;
import mod.chiselsandbits.helpers.IStateRef;

public class BlockStateRef implements IStateRef {
  final int stateID;

  public BlockStateRef(final int sid) {
    stateID = sid;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof BlockStateRef) {
      return stateID == ((BlockStateRef) obj).stateID;
    }

    return false;
  }

  @Override
  public VoxelBlob getVoxelBlob() {
    final VoxelBlob b = new VoxelBlob();
    b.fill(stateID);
    return b;
  }
}
;
