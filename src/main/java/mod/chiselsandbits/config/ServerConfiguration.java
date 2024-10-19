package mod.chiselsandbits.config;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Objects;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeConfigSpec;

/**
 * Mod server configuration.
 * Loaded serverside, synced on connection.
 */
public class ServerConfiguration extends AbstractConfiguration {
    public ForgeConfigSpec.BooleanValue logTileErrors;
    public ForgeConfigSpec.BooleanValue logEligibilityErrors;
    public ForgeConfigSpec.BooleanValue blackListRandomTickingBlocks;
    public ForgeConfigSpec.BooleanValue damageTools;
    public ForgeConfigSpec.BooleanValue enableChiselToolHarvestCheck;
    public ForgeConfigSpec.ConfigValue<String> enableChiselToolHarvestCheckTools;
    public ForgeConfigSpec.BooleanValue enableToolHarvestLevels;
    public ForgeConfigSpec.BooleanValue enableBitLightSource;
    public ForgeConfigSpec.DoubleValue bitLightPercentage;
    public ForgeConfigSpec.BooleanValue compatabilityMode;
    public ForgeConfigSpec.IntValue bagStackSize;
    public ForgeConfigSpec.IntValue stoneChiselUses;
    public ForgeConfigSpec.IntValue ironChiselUses;
    public ForgeConfigSpec.IntValue diamondChiselUses;
    public ForgeConfigSpec.IntValue netheriteChiselUses;
    public ForgeConfigSpec.IntValue goldChiselUses;
    public ForgeConfigSpec.IntValue wrenchUses;
    public ForgeConfigSpec.IntValue stoneSawUses;
    public ForgeConfigSpec.IntValue ironSawUses;
    public ForgeConfigSpec.IntValue goldSawUses;
    public ForgeConfigSpec.IntValue diamondSawUses;
    public ForgeConfigSpec.IntValue netheriteSawUses;
    public ForgeConfigSpec.BooleanValue fullBlockCrafting;
    public ForgeConfigSpec.BooleanValue requireBagSpace;
    public ForgeConfigSpec.BooleanValue voidExcessBits;
    public ForgeConfigSpec.IntValue creativeClipboardSize;
    public ForgeConfigSpec.ConfigValue<List<? extends String>> revertibleBlocks;

    public ForgeConfigSpec.BooleanValue lowMemoryMode;

    protected ServerConfiguration(final ForgeConfigSpec.Builder builder) {
        createCategory(builder, "server.troubleshooting");

        logTileErrors = defineBoolean(builder, "server.troubleshooting.logging.tile-errors", true);
        logEligibilityErrors = defineBoolean(builder, "server.troubleshooting.logging.eligibility-errors", true);

        finishCategory(builder);
        createCategory(builder, "server.balancing");

        blackListRandomTickingBlocks =
                defineBoolean(builder, "server.balancing.random-ticking-blocks.blacklisted", false);
        damageTools = defineBoolean(builder, "server.balancing.tools.damage", true);
        enableChiselToolHarvestCheck =
                defineBoolean(builder, "server.balancing.chisel-tool.harvest-check.enabled", false);
        enableChiselToolHarvestCheckTools =
                defineString(builder, "server.balancing.chisel-tool.harvest-check.tools", "");
        enableToolHarvestLevels = defineBoolean(builder, "server.balancing.tools.harvest-levels.enabled", true);
        enableBitLightSource = defineBoolean(builder, "server.balancing.bits.act-as-light-source", true);
        bitLightPercentage = defineDouble(builder, "server.balancing.bits.light-percentage", 6.25);
        compatabilityMode = defineBoolean(builder, "server.balancing.compatibility-mode.enabled", false);
        bagStackSize = defineInteger(builder, "server.balancing.bag.stack-size", 512);
        stoneChiselUses = defineInteger(builder, "server.balancing.chisel-uses.stone", 12288);
        ironChiselUses = defineInteger(builder, "server.balancing.chisel-uses.iron", 110592);
        diamondChiselUses = defineInteger(builder, "server.balancing.chisel-uses.diamond", 995328);
        netheriteChiselUses = defineInteger(builder, "server.balancing.chisel-uses.netherite", 8957952);
        goldChiselUses = defineInteger(builder, "server.balancing.chisel-uses.gold", 1024);
        wrenchUses = defineInteger(builder, "server.balancing.wrench-uses", 1888);
        stoneSawUses = defineInteger(builder, "server.balancing.saw-uses.stone", 512);
        goldSawUses = defineInteger(builder, "server.balancing.saw-uses.gold", 500);
        ironSawUses = defineInteger(builder, "server.balancing.saw-uses.iron", 2048);
        diamondSawUses = defineInteger(builder, "server.balancing.saw-uses.diamond", 8192);
        netheriteSawUses = defineInteger(builder, "server.balancing.saw-uses.netherite", 32768);
        fullBlockCrafting = defineBoolean(builder, "server.balancing.full-block-crafting.enabled", true);
        requireBagSpace = defineBoolean(builder, "server.balancing.bag-space.required", true);
        voidExcessBits = defineBoolean(builder, "server.balancing.bag-space.void-excess", true);
        creativeClipboardSize = defineInteger(builder, "server.balancing.clipboard.size.creative", 10);
        revertibleBlocks = defineList(
                builder, "server.balancing.revertible.blocks", Lists.newArrayList("*"), (o) -> o instanceof String);

        finishCategory(builder);
        createCategory(builder, "server.performance");

        lowMemoryMode = defineBoolean(builder, "server.performance.memory.low-mode.enabled", false);

        finishCategory(builder);
    }

    public boolean canRevertToBlock(BlockState newState) {
        final List<? extends String> blockNames = revertibleBlocks.get();
        return blockNames.contains("*")
                || blockNames.contains(Objects.requireNonNull(BuiltInRegistries.BLOCK.getKey(newState.getBlock()))
                        .toString());
    }
}
