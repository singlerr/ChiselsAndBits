package mod.chiselsandbits.client;

import com.google.common.collect.ImmutableList;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import mod.chiselsandbits.api.IBitAccess;
import mod.chiselsandbits.api.ItemType;
import mod.chiselsandbits.chiseledblock.NBTBlobConverter;
import mod.chiselsandbits.chiseledblock.data.VoxelBlob;
import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.helpers.ModUtil;
import mod.chiselsandbits.interfaces.ICacheClearable;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class CreativeClipboardTab implements ICacheClearable {
  private static final List<ItemStack> myWorldItems = new ArrayList<ItemStack>();
  static boolean renewMappings = true;
  private static List<CompoundTag> myCrossItems = new ArrayList<CompoundTag>();
  private static ClipboardStorage clipStorage = null;

  private static CreativeClipboardTab instance;

  private CreativeClipboardTab() {
    ChiselsAndBits.getInstance().addClearable(this);
  }

  public static CreativeClipboardTab getInstance() {
    if (instance == null) {
      return (instance = new CreativeClipboardTab());
    }
    return instance;
  }

  public void load(final File file) {
    clipStorage = new ClipboardStorage(file);
    myCrossItems = clipStorage.read();
  }

  public void addItem(final ItemStack iss) {
    // this is a client side things.
    if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
      final IBitAccess bitData = ChiselsAndBits.getApi().createBitItem(iss);

      if (bitData == null) {
        return;
      }

      final ItemStack is = bitData.getBitsAsItem(null, ItemType.CHISELED_BLOCK, true);

      if (is == null) {
        return;
      }

      // remove duplicates if they exist...
      for (final CompoundTag isa : myCrossItems) {
        if (isa.equals(is.getTag())) {
          myCrossItems.remove(isa);
          break;
        }
      }

      // add item to front...
      myCrossItems.add(0, is.getTag());

      // remove extra items from back..
      while (myCrossItems.size()
          > ChiselsAndBits.getConfig()
          .getServer()
          .creativeClipboardSize
          .get()
          && !myCrossItems.isEmpty()) {
        myCrossItems.remove(myCrossItems.size() - 1);
      }

      clipStorage.write(myCrossItems);
      myWorldItems.clear();
      renewMappings = true;
    }
  }

  public List<ItemStack> getClipboard() {
    if (renewMappings) {
      myWorldItems.clear();
      renewMappings = false;

      for (final CompoundTag nbt : myCrossItems) {
        final NBTBlobConverter c = new NBTBlobConverter();
        c.readChisleData(nbt.getCompound(ModUtil.NBT_BLOCKENTITYTAG), VoxelBlob.VERSION_ANY);

        // recalculate.
        c.updateFromBlob();

        final ItemStack worldItem = c.getItemStack(false);

        if (worldItem != null) {
          myWorldItems.add(worldItem);
        }
      }
    }

    return ImmutableList.copyOf(myWorldItems);
  }

  @Override
  public void clearCache() {
    renewMappings = true;
  }
}
