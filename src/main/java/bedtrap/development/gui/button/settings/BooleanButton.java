package bedtrap.development.gui.button.settings;

import bedtrap.development.BedTrap;
import bedtrap.development.ic.Setting;
import bedtrap.development.gui.button.SettingButton;
import bedtrap.development.systems.modules.Module;
import bedtrap.development.systems.modules.other.Colors.Colors;
import bedtrap.development.systems.modules.other.Font.Font;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;

import java.awt.*;

public class BooleanButton extends SettingButton {
    private final Setting<Boolean> setting;

    public BooleanButton(Module module, Setting setting, int X, int Y, int W, int H) {
        super(module, X, Y, W, H);
        this.setting = setting;
    }

    @Override
    public void render(int mX, int mY) {
        drawButton();

        MatrixStack stack = new MatrixStack();
        stack.scale(Colors.s, Colors.s, Colors.s);

        if (setting.get()) {
            drawButton(mX, mY);
            Font.get.renderText(stack, setting.getName(), (float) (getX() + 6),
                    (float) (getY() + 4), -1);
        } else {
            if (isHover(getX(), getY(), getW(), getH() - 1, mX, mY)) {
                BedTrap.getClickGui().drawFlat(getX() + 3, getY() + 1, getX() + getW() - 3, getY() + getH(),
                        new Color(20, 20, 20, 255).brighter().getRGB(), new Color(20, 20, 20, 255).brighter().getRGB());
            } else {
                BedTrap.getClickGui().drawGradient(getX() + 3, getY() + 1, getX() + getW() - 3, getY() + getH(),
                        new Color(20, 20, 20, 255).getRGB(), new Color(20, 20, 20, 255).getRGB());
            }

            Font.get.renderText(stack, setting.getName(), (float) (getX() + 6),
                    (float) (getY() + 4), new Color(155, 155, 155, 255).getRGB());
        }
    }

    @Override
    public void mouseDown(int mX, int mY, int mB) {
        if (isHover(getX(), getY(), getW(), getH() - 1, mX, mY) && (mB == 0 || mB == 1)) {
            mc.player.playSound(SoundEvents.UI_BUTTON_CLICK, 1f, 1f);
            setting.setValue(!setting.get());
        }
    }
}
