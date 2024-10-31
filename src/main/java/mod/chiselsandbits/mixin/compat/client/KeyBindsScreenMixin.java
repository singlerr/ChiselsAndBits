package mod.chiselsandbits.mixin.compat.client;

import com.mojang.blaze3d.platform.InputConstants;
import committee.nova.mkb.api.IKeyBinding;
import committee.nova.mkb.keybinding.KeyModifier;
import net.minecraft.Util;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.controls.KeyBindsList;
import net.minecraft.client.gui.screens.controls.KeyBindsScreen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = KeyBindsScreen.class, priority = 999)
public abstract class KeyBindsScreenMixin extends OptionsSubScreen {

    @Shadow
    @Nullable
    public KeyMapping selectedKey;

    @Shadow
    public long lastKeySelection;

    @Shadow
    private KeyBindsList keyBindsList;

    @Unique
    private KeyModifier modifier = KeyModifier.NONE;

    public KeyBindsScreenMixin(Screen screen, Options options, Component component) {
        super(screen, options, component);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"))
    private void chisels$resetKeyModifier(double d, double e, int i, CallbackInfoReturnable<Boolean> cir) {
        modifier = KeyModifier.NONE;
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void chisels$keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (selectedKey != null) {
            final IKeyBinding extended = (IKeyBinding) selectedKey;
            if (keyCode == 256) {
                extended.setKeyModifierAndCode(KeyModifier.getActiveModifier(), InputConstants.UNKNOWN);
                options.setKey(selectedKey, InputConstants.UNKNOWN);
            } else {
                extended.setKeyModifierAndCode(
                        KeyModifier.getActiveModifier(), InputConstants.getKey(keyCode, scanCode));
                options.setKey(selectedKey, InputConstants.getKey(keyCode, scanCode));
            }

            if (!KeyModifier.isKeyCodeModifier(((IKeyBinding) selectedKey).getKey())
                    || (KeyModifier.getActiveModifier().matches(extended.getKey()) && modifier != KeyModifier.NONE)) {
                this.selectedKey = null;
            }

            if (KeyModifier.isKeyCodeModifier(extended.getKey())) {
                modifier = KeyModifier.getActiveModifier();
            }

            this.lastKeySelection = Util.getMillis();
            this.keyBindsList.resetMappingAndUpdateButtons();
            cir.setReturnValue(true);
            cir.cancel();
            return;
        }
        cir.setReturnValue(super.keyPressed(keyCode, scanCode, modifiers));
        cir.cancel();
    }
}
