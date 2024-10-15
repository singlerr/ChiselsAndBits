package mod.chiselsandbits.events.extra;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface RenderWorldLastEvent {

  Event<RenderWorldLast> EVENT =
      EventFactory.createArrayBacked(RenderWorldLast.class, listeners -> ((stack, partialTicks) -> {
        for (RenderWorldLast listener : listeners) {
          listener.handle(stack, partialTicks);
        }
      }));

  interface RenderWorldLast {
    void handle(PoseStack stack, float partialTicks);
  }
}
