package mod.chiselsandbits.client.gui;

import com.google.common.base.Stopwatch;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import mod.chiselsandbits.api.ReplacementStateHandler;
import mod.chiselsandbits.core.ClientSide;
import mod.chiselsandbits.helpers.ChiselToolType;
import mod.chiselsandbits.helpers.DeprecationHelper;
import mod.chiselsandbits.helpers.LocalStrings;
import mod.chiselsandbits.modes.IToolMode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.DyeColor;
import org.lwjgl.opengl.GL11;

public class ChiselsAndBitsMenu extends Screen {

    private final float TIME_SCALE = 0.01f;
    public static final ChiselsAndBitsMenu instance = new ChiselsAndBitsMenu();

    private float visibility = 0.0f;
    private boolean canRaise = true;
    private Stopwatch lastChange = Stopwatch.createStarted();
    public IToolMode switchTo = null;
    public ButtonAction doAction = null;
    public boolean actionUsed = false;

    protected ChiselsAndBitsMenu() {
        super(Component.literal("Menu"));
    }

    private float clampVis(final float f) {
        return Math.max(0.0f, Math.min(1.0f, f));
    }

    public boolean raiseVisibility() {
        if (!isCanRaise()) return false;

        visibility = clampVis(visibility + lastChange.elapsed(TimeUnit.MILLISECONDS) * TIME_SCALE);
        lastChange = Stopwatch.createStarted();
        return true;
    }

    public void decreaseVisibility() {
        setCanRaise(true);
        visibility = clampVis(visibility - lastChange.elapsed(TimeUnit.MILLISECONDS) * TIME_SCALE);
        lastChange = Stopwatch.createStarted();
    }

    public boolean isCanRaise() {
        return canRaise;
    }

    public void setCanRaise(final boolean canRaise) {
        this.canRaise = canRaise;
    }

    public boolean isVisible() {
        return visibility > 0.001;
    }

    public void configure(final int scaledWidth, final int scaledHeight) {
        width = scaledWidth;
        height = scaledHeight;
    }

    private static class MenuButton {

        public double x1, x2;
        public double y1, y2;
        public boolean highlighted;

        public final ButtonAction action;
        public TextureAtlasSprite icon;
        public int color;
        public String name;
        public Direction textSide;

        public MenuButton(
                final String name,
                final ButtonAction action,
                final double x,
                final double y,
                final TextureAtlasSprite ico,
                final Direction textSide) {
            this.name = name;
            this.action = action;
            x1 = x;
            x2 = x + 18;
            y1 = y;
            y2 = y + 18;
            icon = ico;
            color = 0xffffff;
            this.textSide = textSide;
        }

        public MenuButton(
                final String name,
                final ButtonAction action,
                final double x,
                final double y,
                final int col,
                final Direction textSide) {
            this.name = name;
            this.action = action;
            x1 = x;
            x2 = x + 18;
            y1 = y;
            y2 = y + 18;
            color = col;
            this.textSide = textSide;
        }
    }
    ;

    static class MenuRegion {

        public final IToolMode mode;
        public double x1, x2;
        public double y1, y2;
        public boolean highlighted;

        public MenuRegion(final IToolMode mode) {
            this.mode = mode;
        }
    }
    ;

    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        this.render(guiGraphics, guiGraphics.pose(), i, j, f);
    }

    public void render(
            GuiGraphics graphics,
            final PoseStack matrixStack,
            final int mouseX,
            final int mouseY,
            final float partialTicks) {
        final ChiselToolType tool = ClientSide.instance.getHeldToolType(InteractionHand.MAIN_HAND);

        if (tool == null) {
            return;
        }

        matrixStack.pushPose();
        // matrixStack.translate( 0.0F, 0.0F, 200.0F );

        final int start = (int) (visibility * 98) << 24;
        final int end = (int) (visibility * 128) << 24;

        graphics.fillGradient(0, 0, width, height, start, end);

        //        RenderSystem.disableTexture();
        RenderSystem.enableBlend();

        //        RenderSystem.disableAlphaTest();
        RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        //        RenderSystem.shadeModel(GL11.GL_SMOOTH);
        final Tesselator tessellator = Tesselator.getInstance();
        final BufferBuilder buffer = tessellator.getBuilder();

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        final double vecX = mouseX - width / 2;
        final double vecY = (mouseY - height / 2);
        double radians = Math.atan2(vecY, vecX);

        final double ring_inner_edge = 20;
        final double ring_outer_edge = 50;
        final double text_distnace = 65;
        final double quarterCircle = Math.PI / 2.0;

        if (radians < -quarterCircle) {
            radians = radians + Math.PI * 2;
        }

        final double middle_x = width / 2;
        final double middle_y = height / 2;

        final ArrayList<MenuRegion> modes = new ArrayList<MenuRegion>();
        final ArrayList<MenuButton> btns = new ArrayList<MenuButton>();

        if (tool == ChiselToolType.BIT) {
            if (ReplacementStateHandler.getInstance().isReplacing()) {
                btns.add(new MenuButton(
                        LocalStrings.BitOptionReplace.toString(),
                        ButtonAction.REPLACE_TOGGLE,
                        text_distnace,
                        -44,
                        ClientSide.swapIcon,
                        Direction.EAST));
            } else {
                btns.add(new MenuButton(
                        LocalStrings.BitOptionPlace.toString(),
                        ButtonAction.REPLACE_TOGGLE,
                        text_distnace,
                        -44,
                        ClientSide.placeIcon,
                        Direction.EAST));
            }
        }

        btns.add(new MenuButton(
                "mod.chiselsandbits.other.undo",
                ButtonAction.UNDO,
                text_distnace,
                -20,
                ClientSide.undoIcon,
                Direction.EAST));
        btns.add(new MenuButton(
                "mod.chiselsandbits.other.redo",
                ButtonAction.REDO,
                text_distnace,
                4,
                ClientSide.redoIcon,
                Direction.EAST));

        if (tool == ChiselToolType.CHISELED_BLOCK
                || tool == ChiselToolType.NEGATIVEPATTERN
                || tool == ChiselToolType.POSITIVEPATTERN) {
            btns.add(new MenuButton(
                    "mod.chiselsandbits.other.roll_x",
                    ButtonAction.ROLL_X,
                    -text_distnace - 18,
                    -20,
                    ClientSide.roll_x,
                    Direction.WEST));
            btns.add(new MenuButton(
                    "mod.chiselsandbits.other.roll_z",
                    ButtonAction.ROLL_Z,
                    -text_distnace - 18,
                    4,
                    ClientSide.roll_z,
                    Direction.WEST));
        }

        if (tool == ChiselToolType.TAPEMEASURE) {
            final int colorSize = DyeColor.values().length / 4 * 24 - 4;
            double underring = -ring_outer_edge - 34;
            double bntPos = -colorSize;
            final int bntSize = 24;
            Direction textSide = Direction.UP;
            for (final DyeColor color : DyeColor.values()) {
                final ButtonAction action = ButtonAction.valueOf(color.name());
                if (bntPos > colorSize) {
                    underring = ring_outer_edge;
                    bntPos = -colorSize;
                    textSide = Direction.DOWN;
                }

                btns.add(new MenuButton(
                        "chiselsandbits.color." + color.getName(),
                        action,
                        bntPos,
                        underring,
                        color.getTextColor(),
                        textSide));
                bntPos += bntSize;
            }
        }

        for (final IToolMode mode : tool.getAvailableModes()) {
            if (!mode.isDisabled()) {
                modes.add(new MenuRegion(mode));
            }
        }

        switchTo = null;
        doAction = null;

        if (!modes.isEmpty()) {
            final int totalModes = Math.max(3, modes.size());
            int currentMode = 0;
            final double fragment = Math.PI * 0.005;
            final double fragment2 = Math.PI * 0.0025;
            final double perObject = 2.0 * Math.PI / totalModes;

            for (final MenuRegion mnuRgn : modes) {
                final double begin_rad = currentMode * perObject - quarterCircle;
                final double end_rad = (currentMode + 1) * perObject - quarterCircle;

                mnuRgn.x1 = Math.cos(begin_rad);
                mnuRgn.x2 = Math.cos(end_rad);
                mnuRgn.y1 = Math.sin(begin_rad);
                mnuRgn.y2 = Math.sin(end_rad);

                final double x1m1 = Math.cos(begin_rad + fragment) * ring_inner_edge;
                final double x2m1 = Math.cos(end_rad - fragment) * ring_inner_edge;
                final double y1m1 = Math.sin(begin_rad + fragment) * ring_inner_edge;
                final double y2m1 = Math.sin(end_rad - fragment) * ring_inner_edge;

                final double x1m2 = Math.cos(begin_rad + fragment2) * ring_outer_edge;
                final double x2m2 = Math.cos(end_rad - fragment2) * ring_outer_edge;
                final double y1m2 = Math.sin(begin_rad + fragment2) * ring_outer_edge;
                final double y2m2 = Math.sin(end_rad - fragment2) * ring_outer_edge;

                final float a = 0.5f;
                float f = 0f;

                final boolean quad = inTriangle(
                                x1m1, y1m1,
                                x2m2, y2m2,
                                x2m1, y2m1,
                                vecX, vecY)
                        || inTriangle(
                                x1m1, y1m1,
                                x1m2, y1m2,
                                x2m2, y2m2,
                                vecX, vecY);

                if (begin_rad <= radians && radians <= end_rad && quad) {
                    f = 1;
                    mnuRgn.highlighted = true;
                    switchTo = mnuRgn.mode;
                }

                buffer.vertex(middle_x + x1m1, middle_y + y1m1, 0)
                        .color(f, f, f, a)
                        .endVertex();
                buffer.vertex(middle_x + x2m1, middle_y + y2m1, 0)
                        .color(f, f, f, a)
                        .endVertex();
                buffer.vertex(middle_x + x2m2, middle_y + y2m2, 0)
                        .color(f, f, f, a)
                        .endVertex();
                buffer.vertex(middle_x + x1m2, middle_y + y1m2, 0)
                        .color(f, f, f, a)
                        .endVertex();

                currentMode++;
            }
        }

        for (final MenuButton btn : btns) {
            final float a = 0.5f;
            float f = 0f;

            if (btn.x1 <= vecX && btn.x2 >= vecX && btn.y1 <= vecY && btn.y2 >= vecY) {
                f = 1;
                btn.highlighted = true;
                doAction = btn.action;
            }

            buffer.vertex(middle_x + btn.x1, middle_y + btn.y1, 0)
                    .color(f, f, f, a)
                    .endVertex();
            buffer.vertex(middle_x + btn.x1, middle_y + btn.y2, 0)
                    .color(f, f, f, a)
                    .endVertex();
            buffer.vertex(middle_x + btn.x2, middle_y + btn.y2, 0)
                    .color(f, f, f, a)
                    .endVertex();
            buffer.vertex(middle_x + btn.x2, middle_y + btn.y1, 0)
                    .color(f, f, f, a)
                    .endVertex();
        }

        tessellator.end();

        //        RenderSystem.shadeModel(GL11.GL_FLAT);

        // matrixStack.translate( 0.0F, 0.0F, 5.0F );
        //        RenderSystem.enableTexture();
        graphics.setColor(1, 1, 1, 1);
        RenderSystem.disableBlend();
        //        RenderSystem.enableAlphaTest();
        RenderSystem.bindTexture(Minecraft.getInstance()
                .getTextureManager()
                .getTexture(InventoryMenu.BLOCK_ATLAS)
                .getId());

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        for (final MenuRegion mnuRgn : modes) {
            final double x = (mnuRgn.x1 + mnuRgn.x2) * 0.5 * (ring_outer_edge * 0.6 + 0.4 * ring_inner_edge);
            final double y = (mnuRgn.y1 + mnuRgn.y2) * 0.5 * (ring_outer_edge * 0.6 + 0.4 * ring_inner_edge);

            final SpriteIconPositioning sip = ClientSide.instance.getIconForMode(mnuRgn.mode);

            final double scalex = 15 * sip.width * 0.5;
            final double scaley = 15 * sip.height * 0.5;
            final double x1 = x - scalex;
            final double x2 = x + scalex;
            final double y1 = y - scaley;
            final double y2 = y + scaley;

            final TextureAtlasSprite sprite = sip.sprite;

            final float f = 1.0f;
            final float a = 1.0f;

            final float u1 = (float) (sip.left * 16.0);
            final float u2 = (float) ((sip.left + sip.width) * 16.0);
            final float v1 = (float) (sip.top * 16.0);
            final float v2 = (float) ((sip.top + sip.height) * 16.0);

            buffer.vertex(middle_x + x1, middle_y + y1, 0)
                    .uv(sprite.getU(u1), sprite.getV(v1))
                    .color(f, f, f, a)
                    .endVertex();
            buffer.vertex(middle_x + x1, middle_y + y2, 0)
                    .uv(sprite.getU(u1), sprite.getV(v2))
                    .color(f, f, f, a)
                    .endVertex();
            buffer.vertex(middle_x + x2, middle_y + y2, 0)
                    .uv(sprite.getU(u2), sprite.getV(v2))
                    .color(f, f, f, a)
                    .endVertex();
            buffer.vertex(middle_x + x2, middle_y + y1, 0)
                    .uv(sprite.getU(u2), sprite.getV(v1))
                    .color(f, f, f, a)
                    .endVertex();
        }

        for (final MenuButton btn : btns) {
            final float f = switchTo == null ? 1.0f : 0.5f;
            final float a = 1.0f;

            final float u1 = 0;
            final float u2 = 16;
            final float v1 = 0;
            final float v2 = 16;

            final TextureAtlasSprite sprite = btn.icon == null ? ClientSide.white : btn.icon;

            final double btnx1 = btn.x1 + 1;
            final double btnx2 = btn.x2 - 1;
            final double btny1 = btn.y1 + 1;
            final double btny2 = btn.y2 - 1;

            final float red = f * ((btn.color >> 16 & 0xff) / 255.0f);
            final float green = f * ((btn.color >> 8 & 0xff) / 255.0f);
            final float blue = f * ((btn.color & 0xff) / 255.0f);

            buffer.vertex(middle_x + btnx1, middle_y + btny1, 0)
                    .uv(sprite.getU(u1), sprite.getV(v1))
                    .color(red, green, blue, a)
                    .endVertex();
            buffer.vertex(middle_x + btnx1, middle_y + btny2, 0)
                    .uv(sprite.getU(u1), sprite.getV(v2))
                    .color(red, green, blue, a)
                    .endVertex();
            buffer.vertex(middle_x + btnx2, middle_y + btny2, 0)
                    .uv(sprite.getU(u2), sprite.getV(v2))
                    .color(red, green, blue, a)
                    .endVertex();
            buffer.vertex(middle_x + btnx2, middle_y + btny1, 0)
                    .uv(sprite.getU(u2), sprite.getV(v1))
                    .color(red, green, blue, a)
                    .endVertex();
        }

        tessellator.end();

        for (final MenuRegion mnuRgn : modes) {
            if (mnuRgn.highlighted) {
                final double x = (mnuRgn.x1 + mnuRgn.x2) * 0.5;
                final double y = (mnuRgn.y1 + mnuRgn.y2) * 0.5;

                int fixed_x = (int) (x * text_distnace);
                final int fixed_y = (int) (y * text_distnace);
                final String text = mnuRgn.mode.getName().getLocal();

                if (x <= -0.2) {
                    fixed_x -= font.width(text);
                } else if (-0.2 <= x && x <= 0.2) {
                    fixed_x -= font.width(text) / 2;
                }
                graphics.drawString(font, text, (int) middle_x + fixed_x, (int) middle_y + fixed_y, 0xffffffff);
            }
        }

        for (final MenuButton btn : btns) {
            if (btn.highlighted) {
                final String text = DeprecationHelper.translateToLocal(btn.name);

                if (btn.textSide == Direction.WEST) {
                    graphics.drawString(
                            font,
                            text,
                            (int) (middle_x + btn.x1 - 8) - font.width(text),
                            (int) (middle_y + btn.y1 + 6),
                            0xffffffff);
                } else if (btn.textSide == Direction.EAST) {
                    graphics.drawString(
                            font, text, (int) (middle_x + btn.x2 + 8), (int) (middle_y + btn.y1 + 6), 0xffffffff);
                } else if (btn.textSide == Direction.UP) {
                    graphics.drawString(
                            font,
                            text,
                            (int) (middle_x + (btn.x1 + btn.x2) * 0.5 - font.width(text) * 0.5),
                            (int) (middle_y + btn.y1 - 14),
                            0xffffffff);
                } else if (btn.textSide == Direction.DOWN) {
                    graphics.drawString(
                            font,
                            text,
                            (int) (middle_x + (btn.x1 + btn.x2) * 0.5 - font.width(text) * 0.5),
                            (int) (middle_y + btn.y1 + 24),
                            0xffffffff);
                }
            }
        }

        matrixStack.popPose();
    }

    private boolean inTriangle(
            final double x1,
            final double y1,
            final double x2,
            final double y2,
            final double x3,
            final double y3,
            final double x,
            final double y) {
        final double ab = (x1 - x) * (y2 - y) - (x2 - x) * (y1 - y);
        final double bc = (x2 - x) * (y3 - y) - (x3 - x) * (y2 - y);
        final double ca = (x3 - x) * (y1 - y) - (x1 - x) * (y3 - y);
        return sign(ab) == sign(bc) && sign(bc) == sign(ca);
    }

    private int sign(final double n) {
        return n > 0 ? 1 : -1;
    }

    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        this.visibility = 0f;
        canRaise = false;
        this.minecraft.setScreen(null);

        if (this.minecraft.screen == null) {
            this.minecraft.setWindowActive(true);
            this.minecraft.getSoundManager().resume();
            this.minecraft.mouseHandler.grabMouse();
        }
        return true;
    }
}
