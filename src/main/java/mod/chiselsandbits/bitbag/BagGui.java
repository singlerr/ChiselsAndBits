package mod.chiselsandbits.bitbag;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.core.ClientSide;
import mod.chiselsandbits.helpers.LocalStrings;
import mod.chiselsandbits.helpers.ModUtil;
import mod.chiselsandbits.network.packets.PacketBagGui;
import mod.chiselsandbits.network.packets.PacketClearBagGui;
import mod.chiselsandbits.network.packets.PacketSortBagGui;
import mod.chiselsandbits.registry.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class BagGui extends AbstractContainerScreen<BagContainer> {

    private static final ResourceLocation BAG_GUI_TEXTURE =
            new ResourceLocation(ChiselsAndBits.MODID, "textures/gui/container/bitbag.png");

    private static GuiBagFontRenderer specialFontRenderer = null;
    boolean requireConfirm = true;
    boolean dontThrow = false;
    private GuiIconButton trashBtn;
    private GuiIconButton sortBtn;
    private Slot hoveredBitSlot = null;

    public BagGui(final BagContainer container, final Inventory playerInventory, final Component title) {
        super(container, playerInventory, title);
        imageHeight = 239;
    }

    @Override
    protected void init() {
        super.init();
        trashBtn = addWidget(new GuiIconButton(leftPos - 18, topPos, ClientSide.trashIcon, p_onPress_1_ -> {
            if (requireConfirm) {
                dontThrow = true;
                if (isValidBitItem()) {
                    requireConfirm = false;
                }
            } else {
                requireConfirm = true;
                // server side!
                ChiselsAndBits.getNetworkChannel().sendToServer(new PacketClearBagGui(getInHandItem()));
                dontThrow = false;
            }
        }));

        sortBtn = addWidget(new GuiIconButton(leftPos - 18, topPos + 18, ClientSide.sortIcon, new Button.OnPress() {
            @Override
            public void onPress(final Button p_onPress_1_) {
                ChiselsAndBits.getNetworkChannel().sendToServer(new PacketSortBagGui());
            }
        }));
    }

    BagContainer getBagContainer() {
        return menu;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float ticks) {
        renderBackground(guiGraphics, mouseX, mouseY, ticks);
        if (trashBtn.isMouseOver(mouseX, mouseY)) {
            if (isValidBitItem()) {
                final String msgNotConfirm = ModUtil.notEmpty(getInHandItem())
                        ? LocalStrings.TrashItem.getLocal(
                                getInHandItem().getHoverName().getString())
                        : LocalStrings.Trash.getLocal();
                final String msgConfirm = ModUtil.notEmpty(getInHandItem())
                        ? LocalStrings.ReallyTrashItem.getLocal(
                                getInHandItem().getHoverName().getString())
                        : LocalStrings.ReallyTrash.getLocal();
                trashBtn.setTooltip(Tooltip.create(Component.literal(requireConfirm ? msgNotConfirm : msgConfirm)));
            } else {
                trashBtn.setTooltip(Tooltip.create(Component.literal(LocalStrings.TrashInvalidItem.getLocal(
                        getInHandItem().getHoverName().getString()))));
            }
        }

        if (sortBtn.isMouseOver(mouseX, mouseY)) {
            sortBtn.setTooltip(Tooltip.create(Component.literal(LocalStrings.Sort.getLocal())));
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float f, int mouseX, int mouseY) {
        final int xOffset = (width - imageWidth) / 2;
        final int yOffset = (height - imageHeight) / 2;
        guiGraphics.setColor(1f, 1f, 1f, 1f);

        guiGraphics.blit(BAG_GUI_TEXTURE, xOffset, yOffset, 0, 0, imageWidth, imageHeight);

        if (specialFontRenderer == null) {
            specialFontRenderer = new GuiBagFontRenderer(
                    font, ChiselsAndBits.getConfig().getServer().bagStackSize.get());
        }
        PoseStack posestack = RenderSystem.getModelViewStack();
        posestack.pushPose();
        posestack.translate(this.leftPos, this.topPos, 0.0D);
        hoveredBitSlot = null;
        guiGraphics.pose().pushPose();
        for (int slotIdx = 0; slotIdx < getBagContainer().customSlots.size(); ++slotIdx) {
            final Slot slot = getBagContainer().customSlots.get(slotIdx);

            final Font defaultFontRenderer = font;

            try {
                font = specialFontRenderer;
                renderSlot(guiGraphics, slot);
            } finally {
                font = defaultFontRenderer;
            }

            if (isHovering(slot, mouseX, mouseY) && slot.isActive()) {
                final int xDisplayPos = this.leftPos + slot.x;
                final int yDisplayPos = this.topPos + slot.y;
                hoveredBitSlot = slot;

                RenderSystem.disableDepthTest();
                RenderSystem.colorMask(true, true, true, false);
                final int INNER_SLOT_SIZE = 16;

                guiGraphics.fillGradient(
                        xDisplayPos,
                        yDisplayPos,
                        xDisplayPos + INNER_SLOT_SIZE,
                        yDisplayPos + INNER_SLOT_SIZE,
                        -2130706433,
                        -2130706433);
                RenderSystem.colorMask(true, true, true, true);
                RenderSystem.enableDepthTest();
            }
        }
        posestack.popPose();
        RenderSystem.applyModelViewMatrix();
        guiGraphics.pose().popPose();
        if (!trashBtn.isMouseOver(mouseX, mouseY)) {
            requireConfirm = true;
        }
    }

    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        // This is what vanilla does...
        final boolean duplicateButton =
                button == Minecraft.getInstance().options.keyPickItem.key.getValue() + 100;

        Slot slot = hoveredSlot;
        if (slot == null) {
            slot = hoveredBitSlot;
        }
        if (slot != null && slot.container instanceof TargetedInventory) {
            final PacketBagGui bagGuiPacket =
                    new PacketBagGui(slot.index, button, duplicateButton, ClientSide.instance.holdingShift());
            bagGuiPacket.doAction(ClientSide.instance.getPlayer());

            ChiselsAndBits.getNetworkChannel().sendToServer(bagGuiPacket);

            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private ItemStack getInHandItem() {
        return getBagContainer().getCarried();
    }

    private boolean isValidBitItem() {
        return ModUtil.isEmpty(getInHandItem()) || getInHandItem().getItem() == ModItems.ITEM_BLOCK_BIT.get();
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int x, int y) {

        guiGraphics.drawString(
                font,
                Language.getInstance()
                        .getVisualOrder(ModItems.ITEM_BIT_BAG_DEFAULT.get().getName(ModUtil.getEmptyStack())),
                8,
                6,
                0x404040);
        guiGraphics.drawString(font, I18n.get("container.inventory"), 8, imageHeight - 93, 0x404040);
    }
}
