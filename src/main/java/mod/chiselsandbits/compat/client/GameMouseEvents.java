package mod.chiselsandbits.compat.client;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface GameMouseEvents {

  Event<WheelScroll> BEFORE_SCROLL =
      EventFactory.createArrayBacked(WheelScroll.class, (listeners) -> ((deltaX, deltaY) -> {
        for (WheelScroll listener : listeners) {
          listener.wheelScroll(deltaX, deltaY);
        }
      }));

  interface WheelScroll {

    void wheelScroll(double deltaX, double deltaY);
  }
}
