package mac.hack.gui.alt;

import mac.hack.gui.widget.MacCheckbox;
import mac.hack.gui.widget.TextPassFieldWidget;
import mac.hack.utils.LoginManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

public class LoginScreen extends Screen {

    public TextFieldWidget userField;
    public TextPassFieldWidget passField;
    public MacCheckbox checkBox;
    public String loginResult = "";

    private TitleScreen titleScreen;

    public LoginScreen(TitleScreen titleScreen) {
        super(new LiteralText("Alts"));
        this.titleScreen = titleScreen;
    }

    public void init() {
        userField = new TextFieldWidget(textRenderer, height/ 2 + 30, height / 4 + 15, 196, 18, LiteralText.EMPTY);
        passField = new TextPassFieldWidget(textRenderer, height/ 2 + 30, height / 4 + 45, 196, 18, LiteralText.EMPTY);
        addButton(new ButtonWidget(width / 2 - 100, height / 2 + 50, 98, 20, new LiteralText("Exit"), button -> {
            client.openScreen(titleScreen);
        }));

        addButton(new ButtonWidget(width / 2 + 2, height / 2 + 50, 98, 20, new LiteralText("Login"), button -> {
            loginResult = LoginManager.login(userField.getText(), passField.getText());
        }));
    }

    public void render(MatrixStack matrix, int mouseX, int mouseY, float delta) {
        renderBackground(matrix);
        drawStringWithShadow(matrix, textRenderer, "Email: ", width / 2 - 130, height / 4 + 15, 0xC0C0C0);
        drawStringWithShadow(matrix, textRenderer, "Password: ", width / 2 - 154, height / 4 + 45, 0xC0C0C0);

        drawStringWithShadow(matrix, textRenderer, loginResult.isEmpty() ? "" : "|  " + loginResult, width / 2 - 24, height / 4 + 65, 0xC0C0C0);
        userField.render(matrix, mouseX, mouseY, 1f);
        passField.render(matrix, mouseX, mouseY, 1f);

        super.render(matrix, mouseX, mouseY, delta);
    }

    public boolean mouseClicked(double double_1, double double_2, int int_1) {
        if (double_1 >= userField.x && double_1 <= userField.x + userField.getWidth()
                && double_2 >= userField.y && double_2 <= userField.y + 18) {
            userField.changeFocus(true);
            passField.setSelected(false);
        }
        if (double_1 >= passField.x && double_1 <= passField.x + passField.getWidth()
                && double_2 >= passField.y && double_2 <= passField.y + 18) {
            userField.setSelected(false);
            passField.changeFocus(true);
        }

        return super.mouseClicked(double_1, double_2, int_1);
    }
    public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
        if (userField.isFocused()) userField.charTyped(p_charTyped_1_, p_charTyped_2_);
        if (passField.isFocused()) passField.charTyped(p_charTyped_1_, p_charTyped_2_);
        return super.charTyped(p_charTyped_1_, p_charTyped_2_);
    }

    public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
        if (userField.isFocused()) userField.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
        if (passField.isFocused()) passField.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
        return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
    }

    public void tick() {
        userField.tick();
        passField.tick();
        super.tick();
    }
}
