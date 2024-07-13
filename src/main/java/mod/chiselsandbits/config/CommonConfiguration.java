package mod.chiselsandbits.config;

import java.util.List;
import mod.chiselsandbits.helpers.LocalStrings;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;

public class CommonConfiguration extends AbstractConfiguration {

    public ForgeConfigSpec.BooleanValue enableHelp;
    public ForgeConfigSpec.LongValue collisionBoxCacheSize;

    public CommonConfiguration(ForgeConfigSpec.Builder builder) {
        createCategory(builder, "common.help");

        enableHelp = defineBoolean(builder, "common.help.enabled", true);

        finishCategory(builder);

        createCategory(builder, "common.performance");

        collisionBoxCacheSize = defineLong(builder, "common.performance.collisions.cache.size", 10000L);

        finishCategory(builder);
    }

    public void helpText(final LocalStrings string, final List<Component> tooltip, final String... variables) {
        if (enableHelp.get()) {
            int varOffset = 0;

            final String[] lines = string.getLocal().split(";");
            for (String a : lines) {
                while (a.contains("{}") && variables.length > varOffset) {
                    final int offset = a.indexOf("{}");
                    if (offset >= 0) {
                        final String pre = a.substring(0, offset);
                        final String post = a.substring(offset + 2);
                        a = String.format("%s%s%s", pre, variables[varOffset++], post);
                    }
                }

                tooltip.add(Component.literal(a));
            }
        }
    }
}
