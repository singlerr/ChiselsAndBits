package mod.chiselsandbits.registry;

import io.github.fabricators_of_create.porting_lib.util.LazyRegistrar;
import io.github.fabricators_of_create.porting_lib.util.RegistryObject;
import mod.chiselsandbits.bitbag.BagContainer;
import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.printer.ChiselPrinterContainer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.MenuType;

public final class ModContainerTypes {

    private static final LazyRegistrar<MenuType<?>> REGISTRAR =
            LazyRegistrar.create(Registries.MENU, ChiselsAndBits.MODID);

    private ModContainerTypes() {
        throw new IllegalStateException("Tried to initialize: ModContainerTypes but this is a Utility class.");
    }

    public static final RegistryObject<MenuType<BagContainer>> BAG_CONTAINER =
            REGISTRAR.register("bag", () -> new MenuType<>(BagContainer::new, FeatureFlagSet.of()));
    public static final RegistryObject<MenuType<ChiselPrinterContainer>> CHISEL_STATION_CONTAINER = REGISTRAR.register(
            "chisel_station", () -> new MenuType<>(ChiselPrinterContainer::new, FeatureFlagSet.of()));

    public static void onModConstruction() {
        REGISTRAR.register();
    }
}
