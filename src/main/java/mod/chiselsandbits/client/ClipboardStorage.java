package mod.chiselsandbits.client;

import com.google.common.collect.Lists;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import mod.chiselsandbits.core.ChiselsAndBits;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;

public class ClipboardStorage {

    private final File file;

    public ClipboardStorage(final File file) {
        this.file = file;
    }

    public void write(final List<CompoundTag> items) throws IOException {
        if (!ChiselsAndBits.getConfig().getClient().persistCreativeClipboard.get()) {
            return;
        }

        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdir();
        }

        CompoundTag root = new CompoundTag();
        root.putInt("size", items.size());
        for (int i = 0; i < items.size(); i++) {
            root.put("clipboard_" + i, items.get(i));
        }
        NbtIo.write(root, file.toPath());
    }

    public List<CompoundTag> read() throws IOException {
        if (!ChiselsAndBits.getConfig().getClient().persistCreativeClipboard.get()) {
            return Lists.newArrayList();
        }

        if (!file.getParentFile().exists()) {
            file.mkdir();
            return Lists.newArrayList();
        }

        if (!file.exists()) {
            return Lists.newArrayList();
        }

        List<CompoundTag> items = new ArrayList<>();
        CompoundTag root = NbtIo.read(file.toPath());
        int size = root.getInt("size");

        for (int i = 0; i < size; i++) {
            try {
                items.add((CompoundTag) root.get("clipboard_" + i));
            } catch (Exception ignore) {

            }
        }

        return items;
    }
}
