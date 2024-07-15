package mod.chiselsandbits.events.extra;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface ResourceRegistrationEvent {

    Event<ResourceRegistration> EVENT = EventFactory.createArrayBacked(ResourceRegistration.class, listeners -> () -> {
        for (ResourceRegistration listener : listeners) {
            listener.handle();
            ;
        }
    });

    interface ResourceRegistration {
        void handle();
    }
}
