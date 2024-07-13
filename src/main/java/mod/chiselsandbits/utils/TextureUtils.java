package mod.chiselsandbits.utils;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.io.IOUtils;

@Environment(EnvType.CLIENT)
public final class TextureUtils {

    private TextureUtils() {
        throw new IllegalStateException("Tried to initialize: TextureUtils but this is a Utility class.");
    }

    public static BufferedImage readBufferedImage(InputStream imageStream) throws IOException {
        BufferedImage bufferedimage;

        try {
            bufferedimage = ImageIO.read(imageStream);
        } finally {
            IOUtils.closeQuietly(imageStream);
        }

        return bufferedimage;
    }
}
