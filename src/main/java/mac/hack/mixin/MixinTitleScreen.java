package mac.hack.mixin;

import mac.hack.gui.LoginScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class MixinTitleScreen extends Screen {


    protected MixinTitleScreen(Text text_1) {
        super(text_1);
    }

    @Inject(at = @At("RETURN"), method = "initWidgetsNormal")
    private void addAltScreen(int y, int spacingY, CallbackInfo ci){

        this.addButton(new ButtonWidget(this.width / 2 - 100 + 205, y + spacingY * 2, 98, 20, new LiteralText("Alts"), (buttonWidget) -> {
            this.client.openScreen(new LoginScreen((TitleScreen)client.currentScreen));
        }));
    }
}
