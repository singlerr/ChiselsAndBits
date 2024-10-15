package mod.chiselsandbits.config;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * Mod client configuration.
 * Loaded clientside, not synced.
 */
public class ClientConfiguration extends AbstractConfiguration {
  public ForgeConfigSpec.BooleanValue enableRightClickModeChange;
  public ForgeConfigSpec.BooleanValue invertBitBagFullness;
  public ForgeConfigSpec.BooleanValue enableToolbarIcons;
  public ForgeConfigSpec.BooleanValue perChiselMode;
  public ForgeConfigSpec.BooleanValue chatModeNotification;
  public ForgeConfigSpec.BooleanValue itemNameModeDisplay;
  public ForgeConfigSpec.BooleanValue addBrokenBlocksToCreativeClipboard;
  public ForgeConfigSpec.IntValue maxUndoLevel;
  public ForgeConfigSpec.IntValue maxTapeMeasures;
  public ForgeConfigSpec.BooleanValue displayMeasuringTapeInChat;
  public ForgeConfigSpec.DoubleValue radialMenuVolume;
  public ForgeConfigSpec.LongValue bitStorageContentCacheSize;
  public ForgeConfigSpec.DoubleValue maxDrawnRegionSize;
  public ForgeConfigSpec.BooleanValue enableFaceLightmapExtraction;
  public ForgeConfigSpec.BooleanValue useGetLightValue;
  public ForgeConfigSpec.BooleanValue disableCustomVertexFormats;

  public ForgeConfigSpec.LongValue modelCacheSize;

  /**
   * Builds client configuration.
   *
   * @param builder config builder
   */
  protected ClientConfiguration(final ForgeConfigSpec.Builder builder) {
    createCategory(builder, "client.settings");

    enableRightClickModeChange = defineBoolean(builder, "enable-right-click-mode-change", false);
    invertBitBagFullness = defineBoolean(builder, "invert-bit-bag-fullness", false);
    enableToolbarIcons = defineBoolean(builder, "enable.toolbar.icons", true);
    perChiselMode = defineBoolean(builder, "per-chisel-mode", true);
    chatModeNotification = defineBoolean(builder, "chat-mode-notification", true);
    itemNameModeDisplay = defineBoolean(builder, "item-name-mode-display", true);
    addBrokenBlocksToCreativeClipboard =
        defineBoolean(builder, "clipboard.add-broken-blocks", false);
    maxUndoLevel = defineInteger(builder, "undo.max-count", 10);
    maxTapeMeasures = defineInteger(builder, "tape-measure.max-count", 10);
    displayMeasuringTapeInChat = defineBoolean(builder, "tape-measure.display-in-chat", true);
    radialMenuVolume = defineDouble(builder, "radial.menu.volume", 0.1f);

    finishCategory(builder);
    createCategory(builder, "client.performance");

    bitStorageContentCacheSize =
        defineLong(builder, "bit-storage.contents.cache.size", 100, 0, Long.MAX_VALUE);
    maxDrawnRegionSize = defineDouble(builder, "max-drawn-region.size", 4);
    enableFaceLightmapExtraction =
        defineBoolean(builder, "lighting.face-lightmap-extraction", true);
    useGetLightValue = defineBoolean(builder, "lighting.use-value", true);
    disableCustomVertexFormats = defineBoolean(builder, "vertexformats.custom.disabled", true);
    modelCacheSize = defineLong(builder, "models.cache.size", 1000, 0, 2000);

    finishCategory(builder);
  }
}
