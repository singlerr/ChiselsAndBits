package mod.chiselsandbits.client;

import committee.nova.mkb.api.IKeyConflictContext;
import committee.nova.mkb.keybinding.KeyConflictContext;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;
import mod.chiselsandbits.api.KeyBindingContext;
import mod.chiselsandbits.core.ClientSide;
import mod.chiselsandbits.helpers.ChiselToolType;
import mod.chiselsandbits.helpers.ModUtil;
import mod.chiselsandbits.interfaces.IVoxelBlobItem;
import mod.chiselsandbits.network.packets.PacketAccurateSneakPlace.IItemBlockAccurate;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public enum ModConflictContext implements IKeyConflictContext {
    HOLDING_OFFGRID {
        @Override
        public boolean isActive() {
            if (super.isActive()) {
                return true;
            }

            try {
                final ItemStack held = getPlayer().getMainHandItem();
                return !ModUtil.isEmpty(held) && held.getItem() instanceof IItemBlockAccurate;
            } catch (final NoPlayerException e) {
                // just fail.
            }

            return false;
        }

        @Override
        public boolean conflicts(final IKeyConflictContext other) {
            return this == other || other == KeyConflictContext.IN_GAME;
        }
    },

    HOLDING_ROTATEABLE {
        @Override
        public boolean isActive() {
            if (super.isActive()) {
                return true;
            }

            try {
                final ItemStack held = getPlayer().getMainHandItem();
                return !ModUtil.isEmpty(held) && held.getItem() instanceof IVoxelBlobItem;
            } catch (final NoPlayerException e) {
                // just fail.
            }

            return false;
        }

        @Override
        public boolean conflicts(final IKeyConflictContext other) {
            return this == other || other == KeyConflictContext.IN_GAME || other == HOLDING_MENUITEM;
        }
    },

    HOLDING_MENUITEM {
        @Override
        public boolean isActive() {
            if (super.isActive()) {
                return true;
            }

            final ChiselToolType tool = ClientSide.instance.getHeldToolType(InteractionHand.MAIN_HAND);
            return tool != null && tool.hasMenu();
        }

        @Override
        public boolean conflicts(final IKeyConflictContext other) {
            return this == other
                    || other == KeyConflictContext.IN_GAME
                    || other == HOLDING_POSTIVEPATTERN
                    || other == HOLDING_CHISEL
                    || other == HOLDING_TAPEMEASURE;
        }
    },
    HOLDING_TAPEMEASURE {
        @Override
        public boolean isActive() {
            return super.isActive()
                    || ClientSide.instance.getHeldToolType(InteractionHand.MAIN_HAND) == ChiselToolType.TAPEMEASURE;
        }

        @Override
        public boolean conflicts(final IKeyConflictContext other) {
            return this == other || other == KeyConflictContext.IN_GAME;
        }
    },

    HOLDING_POSTIVEPATTERN {
        @Override
        public boolean isActive() {
            return super.isActive()
                    || ClientSide.instance.getHeldToolType(InteractionHand.MAIN_HAND) == ChiselToolType.POSITIVEPATTERN;
        }

        @Override
        public boolean conflicts(final IKeyConflictContext other) {
            return this == other || other == KeyConflictContext.IN_GAME;
        }
    },

    HOLDING_CHISEL {
        @Override
        public boolean isActive() {
            if (super.isActive()) {
                return true;
            }

            final ChiselToolType tool = ClientSide.instance.getHeldToolType(InteractionHand.MAIN_HAND);
            return tool != null && tool.isBitOrChisel();
        }

        @Override
        public boolean conflicts(final IKeyConflictContext other) {
            return this == other || other == KeyConflictContext.IN_GAME;
        }
    };

    private final Set<Class<? extends Item>> activeItemClasses = new HashSet<Class<? extends Item>>();

    public void setItemActive(final Item item) {
        activeItemClasses.add(item.getClass());
    }

    @Override
    public boolean isActive() {
        try {
            final ItemStack held = getPlayer().getMainHandItem();

            if (ModUtil.isEmpty(held)) {
                return false;
            }

            for (final Class<? extends Item> itemClass : activeItemClasses) {
                if (itemClass.isInstance(held.getItem())) {
                    return true;
                }
            }

            if (held.getItem().getClass().isAnnotationPresent(KeyBindingContext.class)) {
                final Annotation annotation = held.getItem().getClass().getAnnotation(KeyBindingContext.class);

                if (annotation instanceof KeyBindingContext) {
                    for (final String name : ((KeyBindingContext) annotation).value()) {
                        if (name.equals(getName())) {
                            return true;
                        }
                    }
                }
            }
        } catch (final NoPlayerException e) {
            // just fail.
        }

        return false;
    }

    private static Player getPlayer() throws NoPlayerException {
        final Player player = ClientSide.instance.getPlayer();

        if (player == null) {
            throw new NoPlayerException();
        }

        return player;
    }

    public String getName() {
        return toString().toLowerCase().replace("holding_", "");
    }
}
