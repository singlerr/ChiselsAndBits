package mod.chiselsandbits.render;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import mod.chiselsandbits.client.model.loader.FabricBakedModelDelegate;
import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.interfaces.ICacheClearable;
import mod.chiselsandbits.registry.ModItems;
import mod.chiselsandbits.render.bit.BitItemSmartModel;
import mod.chiselsandbits.render.chiseledblock.ChiseledBlockSmartModel;
import mod.chiselsandbits.render.patterns.PrintSmartModel;
import mod.chiselsandbits.utils.Constants;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class SmartModelManager {

  private static final SmartModelManager INSTANCE = new SmartModelManager();
  private final HashMap<ResourceLocation, BakedModel> models =
      new HashMap<ResourceLocation, BakedModel>();
  private final List<ModelResourceLocation> res = new ArrayList<ModelResourceLocation>();
  private final List<ICacheClearable> clearable = new ArrayList<ICacheClearable>();
  private boolean setup = false;

  private SmartModelManager() {
  }

  public static SmartModelManager getInstance() {
    return INSTANCE;
  }

  private void setup() {
    if (setup) {
      return;
    }

    setup = true;
    FabricBakedModelDelegate smartModel =
        new FabricBakedModelDelegate(new ChiseledBlockSmartModel());
    add(Constants.DataGenerator.CHISELED_BLOCK_MODEL, smartModel);

    ChiselsAndBits.getInstance().addClearable(smartModel);

    add(new ResourceLocation(ChiselsAndBits.MODID, "block_bit"), new BitItemSmartModel());
    add(
        new ResourceLocation(ChiselsAndBits.MODID, "positiveprint_written_preview"),
        new PrintSmartModel("positiveprint", ModItems.ITEM_POSITIVE_PRINT_WRITTEN.get()));
    add(
        new ResourceLocation(ChiselsAndBits.MODID, "negativeprint_written_preview"),
        new PrintSmartModel("negativeprint", ModItems.ITEM_NEGATIVE_PRINT_WRITTEN.get()));
    add(
        new ResourceLocation(ChiselsAndBits.MODID, "mirrorprint_written_preview"),
        new PrintSmartModel("mirrorprint", ModItems.ITEM_MIRROR_PRINT_WRITTEN.get()));
  }

  private void add(final ResourceLocation modelLocation, final BakedModel modelGen) {
    final ResourceLocation second = new ResourceLocation(
        modelLocation.getNamespace(),
        modelLocation.getPath().substring(1 + modelLocation.getPath().lastIndexOf('/')));

    if (modelGen instanceof ICacheClearable) {
      clearable.add((ICacheClearable) modelGen);
    }

    res.add(new ModelResourceLocation(modelLocation, "normal"));
    res.add(new ModelResourceLocation(second, "normal"));

    res.add(new ModelResourceLocation(modelLocation, "inventory"));
    res.add(new ModelResourceLocation(second, "inventory"));

    res.add(new ModelResourceLocation(modelLocation, "multipart"));
    res.add(new ModelResourceLocation(second, "multipart"));

    models.put(modelLocation, modelGen);
    models.put(second, modelGen);
    models.put(new ModelResourceLocation(modelLocation, "normal"), modelGen);
    models.put(new ModelResourceLocation(second, "normal"), modelGen);

    models.put(new ModelResourceLocation(modelLocation, "inventory"), modelGen);
    models.put(new ModelResourceLocation(second, "inventory"), modelGen);

    models.put(new ModelResourceLocation(modelLocation, "multipart"), modelGen);
    models.put(new ModelResourceLocation(second, "multipart"), modelGen);
  }

  public void textureStitchEvent(final TextureAtlas stitch) {
    ChiselsAndBits.getInstance().clearCache();
  }

  public void onModelBakeEvent(ModelLoadingPlugin.Context context) {
    setup();
    for (final ICacheClearable c : clearable) {
      c.clearCache();
    }

    for (final ModelResourceLocation rl : res) {
      context.modifyModelAfterBake().register(new ModelModifier.AfterBake() {
        @Override
        public @Nullable BakedModel modifyModelAfterBake(@Nullable BakedModel model,
                                                         Context context) {
          if (context.id().equals(rl)) {
            return getModel(rl);
          }
          return model;
        }
      });
    }
  }

  private BakedModel getModel(final ResourceLocation modelLocation) {
    try {
      return models.get(modelLocation);
    } catch (final Exception e) {
      throw new RuntimeException(
          "The Model: " + modelLocation.toString() + " was not available was requested.");
    }
  }

  private static final class Setup {
  }
}
