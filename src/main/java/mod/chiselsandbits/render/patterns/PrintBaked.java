package mod.chiselsandbits.render.patterns;

import mod.chiselsandbits.client.model.baked.BaseBakedItemModel;
import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.interfaces.IPatternItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;

public class PrintBaked extends BaseBakedItemModel {

  final String itemName;

  public PrintBaked(final String itname, final IPatternItem item, final ItemStack stack) {
    itemName = itname;

    final ItemStack blockItem = item.getPatternedItem(stack, false);
    BakedModel model =
        Minecraft.getInstance().getItemRenderer().getItemModelShaper().getItemModel(blockItem);

    model = model.getOverrides().resolve(model, blockItem, null, null, 0);

    for (final Direction face : Direction.values()) {
      list.addAll(model.getQuads(null, face, RANDOM));
    }

    list.addAll(model.getQuads(null, null, RANDOM));
  }

  @Override
  public boolean usesBlockLight() {
    return false;
  }

  @Override
  public TextureAtlasSprite getParticleIcon() {
    return Minecraft.getInstance()
        .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
        .apply(new ResourceLocation(ChiselsAndBits.MODID, "item/" + itemName));
  }
}
