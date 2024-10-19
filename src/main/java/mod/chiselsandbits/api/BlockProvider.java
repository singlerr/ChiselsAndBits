package mod.chiselsandbits.api;

import java.util.Collection;
import net.minecraft.world.level.block.state.BlockState;

public interface BlockProvider {

    Collection<BlockState> getStates();
}
