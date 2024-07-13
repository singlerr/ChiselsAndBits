package mod.chiselsandbits.core.api;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import mod.chiselsandbits.api.KeyBindingContext;
import mod.chiselsandbits.client.ModConflictContext;
import mod.chiselsandbits.core.Log;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.registries.ForgeRegistries;

public class IMCHandlerKeyBindingAnnotations implements IMCMessageHandler {

    @Override
    public void excuteIMC(final InterModComms.IMCMessage message) {
        try {
            boolean found = false;
            Class<?> itemClass;
            Annotation annotation;
            List<String> conflictContextNames;
            ResourceLocation regName;

            for (Item item : ForgeRegistries.ITEMS) {
                regName = item.getRegistryName();

                if (regName == null || !regName.getNamespace().equals(message.getSenderModId())) {
                    continue;
                }

                itemClass = item.getClass();

                while (itemClass != Item.class) {
                    if (itemClass.isAnnotationPresent(KeyBindingContext.class)) {
                        annotation = itemClass.getAnnotation(KeyBindingContext.class);

                        if (annotation instanceof KeyBindingContext) {
                            conflictContextNames = Arrays.asList(((KeyBindingContext) annotation).value());

                            for (ModConflictContext conflictContext : ModConflictContext.values()) {
                                if (conflictContextNames.contains(conflictContext.getName())) {
                                    conflictContext.setItemActive(item);
                                    found = true;
                                }
                            }
                        }
                    }

                    itemClass = itemClass.getSuperclass();
                }
            }

            if (!found) {
                throw new RuntimeException(
                        "No item classes were found with a KeyBindingContext annotation that applies to sub-classes. Add one with 'applyToSubClasses = true' to do so.");
            }
        } catch (final Throwable e) {
            Log.logError("IMC initkeybindingannotations From " + message.getSenderModId(), e);
        }
    }
}
