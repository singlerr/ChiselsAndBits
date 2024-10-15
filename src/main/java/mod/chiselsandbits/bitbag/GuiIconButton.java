package mod.chiselsandbits.bitbag;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;

public class GuiIconButton extends Button {
  TextureAtlasSprite icon;

  public GuiIconButton(final int x, final int y, final TextureAtlasSprite icon,
                       Button.OnPress pressedAction) {
    super(x, y, 18, 18, Component.literal(""), pressedAction, Button.DEFAULT_NARRATION);
    this.icon = icon;
  }

  @Override
  protected void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {
    super.renderWidget(guiGraphics, i, j, f);
    guiGraphics.setColor(1f, 1f, 1f, 1f);
    guiGraphics.blit(x + 1, y + 1, 0, 16, 16, icon);
  }

  public interface OnToolTip {

    void onToolTip(double mouseX, double mouseY);
  }
}
