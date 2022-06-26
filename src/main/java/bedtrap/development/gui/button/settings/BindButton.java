package bedtrap.development.gui.button.settings;

import bedtrap.development.gui.button.SettingButton;
import bedtrap.development.systems.modules.Module;
import bedtrap.development.systems.modules.other.Colors.Colors;
import bedtrap.development.systems.modules.other.Font.Font;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;

import java.awt.*;

public class BindButton extends SettingButton {
    private final Module module;
    private boolean binding;

    public BindButton(Module module, int x, int y, int w, int h) {
        super(module, x, y, w, h);
        this.module = module;
    }

    @Override
    public void render(int mX, int mY) {
        drawButton();

        drawButton(mX, mY);

        MatrixStack stack = new MatrixStack();
        stack.scale(Colors.s, Colors.s, Colors.s);

        Font.get.renderText(stack, "Bind", (float) (getX() + 6), (float) (getY() + 4),
                new Color(255, 255, 255, 255).getRGB());

        if (binding) {
            Font.get.renderText(stack, "...",
                    (float) ((getX() + 6) + Font.get.getWidth(" Bind")), (float) (getY() + 4),
                    new Color(155, 155, 155, 255).getRGB());
        } else {
            Font.get.renderText(stack,
                    module.getBind() == InputUtil.fromTranslationKey("key.keyboard.unknown").getCode()
                            ? "NONE"
                            : InputUtil.fromKeyCode(module.getBind(), -1).getLocalizedText().asString(),
                    (float) ((getX() + 6) + Font.get.getWidth(" Bind")),
                    (float) (getY() + 4), new Color(155, 155, 155, 255).getRGB());
        }
    }

    @Override
    public void mouseDown(int mX, int mY, int mB) {
        if (isHover(getX(), getY(), getW(), getH() - 1, mX, mY)) {
            mc.player.playSound(SoundEvents.UI_BUTTON_CLICK, 1f, 1f);
            binding = !binding;
        }
    }

    @Override
    public void keyPress(int key) {
        if (binding) {
            if (key == InputUtil.fromTranslationKey("key.keyboard.delete").getCode() || key == InputUtil.fromTranslationKey("key.keyboard.backspace").getCode()) {
                getModule().setBind(InputUtil.fromTranslationKey("key.keyboard.unknown").getCode());
            } else getModule().setBind(key);

            binding = false;
        }
    }
}
