package mod.chiselsandbits.utils;

import net.minecraft.client.renderer.RenderType;

public final class ModClientHooks {

    private static final ThreadLocal<RenderType> renderType = new ThreadLocal<>();

    public static void setRenderType(RenderType type) {
        renderType.set(type);
    }

    public static RenderType getRenderType() {
        return renderType.get();
    }
}
