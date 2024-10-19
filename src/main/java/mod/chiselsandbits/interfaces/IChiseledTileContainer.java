package mod.chiselsandbits.interfaces;

import mod.chiselsandbits.chiseledblock.data.VoxelBlob;

public interface IChiseledTileContainer {

    boolean isBlobOccluded(VoxelBlob blob);

    void sendUpdate();

    void saveData();
}
