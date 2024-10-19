package mod.chiselsandbits.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface ChiselsAndBitsEvents {

    Event<BlockBitModification> BLOCK_BIT_MODIFICATION =
            EventFactory.createArrayBacked(BlockBitModification.class, listeners -> (event -> {
                for (BlockBitModification listener : listeners) {
                    listener.handle(event);
                }
            }));

    Event<BlockBitPostModification> BLOCK_BIT_POST_MODIFICATION =
            EventFactory.createArrayBacked(BlockBitPostModification.class, listeners -> (event -> {
                for (BlockBitPostModification listener : listeners) {
                    listener.handle(event);
                }
            }));

    Event<FullBlockRestoration> FULL_BLOCK_RESTORATION =
            EventFactory.createArrayBacked(FullBlockRestoration.class, listeners -> (event -> {
                for (FullBlockRestoration listener : listeners) {
                    listener.handle(event);
                }
            }));

    interface BlockBitModification {
        void handle(EventBlockBitModification event);
    }

    interface BlockBitPostModification {
        void handle(EventBlockBitPostModification event);
    }

    interface FullBlockRestoration {
        void handle(EventFullBlockRestoration event);
    }
}
