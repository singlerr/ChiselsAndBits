package mod.chiselsandbits;

import mod.chiselsandbits.core.ChiselsAndBits;
import net.fabricmc.api.ModInitializer;

public class ChiselsAndBitsMod implements ModInitializer {
  @Override
  public void onInitialize() {
    new ChiselsAndBits();
  }
}
