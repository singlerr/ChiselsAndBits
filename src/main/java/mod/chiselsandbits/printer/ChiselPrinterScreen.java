package mod.chiselsandbits.printer;

import java.util.Objects;
import mod.chiselsandbits.utils.Constants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ChiselPrinterScreen extends AbstractContainerScreen<ChiselPrinterContainer> {

    private static final ResourceLocation GUI_TEXTURES =
            new ResourceLocation(Constants.MOD_ID, "textures/gui/container/chisel_printer.png");

    public ChiselPrinterScreen(
            final ChiselPrinterContainer screenContainer, final Inventory inv, final Component titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    protected void init() {
        this.imageWidth = 176;
        this.imageHeight = 166;

        super.init();

        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float f, int i, int j) {
        renderBackground(guiGraphics, i, j, f);
        guiGraphics.setColor(1, 1, 1, 1);
        guiGraphics.blit(GUI_TEXTURES, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        if (this.menu.getToolStack().isEmpty()) {
            return;
        }

        guiGraphics.renderItem(
                Objects.requireNonNull(this.minecraft.player),
                this.menu.getToolStack(),
                this.leftPos + 81,
                this.topPos + 47,
                0);

        int scaledProgress = this.menu.getChiselProgressionScaled();
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0, 400);
        guiGraphics.blit(
                GUI_TEXTURES,
                this.leftPos + 73 + 10 + scaledProgress,
                this.topPos + 49,
                this.imageWidth + scaledProgress,
                0,
                16 - scaledProgress,
                16);
        guiGraphics.pose().popPose();
    }
}
