package bedtrap.development.gui.button.settings;

import bedtrap.development.BedTrap;
import bedtrap.development.gui.button.SettingButton;
import bedtrap.development.systems.modules.Module;
import bedtrap.development.systems.modules.other.Colors.Colors;
import bedtrap.development.systems.modules.other.Font.Font;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;

import java.awt.*;

public class DrawnButton extends SettingButton {
    public DrawnButton(Module module, int x, int y, int w, int h) {
        super(module, x, y, w, h);
    }

    @Override
    public void render(int mX, int mY) {
        drawButton();
        MatrixStack stack = new MatrixStack();
        stack.scale(Colors.s, Colors.s, Colors.s);
        if (getModule().drawn) {
            drawButton(mX, mY);
            Font.get.renderText(stack, "Drawn", (float) (getX() + 6), (float) (getY() + 4), -1);
        } else {
            if (isHover(getX(), getY(), getW(), getH() - 1, mX, mY)) {
                BedTrap.getClickGui().drawFlat(getX() + 3, getY() + 1, getX() + getW() - 3, getY() + getH(),
                        new Color(20, 20, 20, 255).brighter().getRGB(), new Color(20, 20, 20, 255).brighter().getRGB());
            } else {
                BedTrap.getClickGui().drawFlat(getX() + 3, getY() + 1, getX() + getW() - 3, getY() + getH(),
                        new Color(20, 20, 20, 255).getRGB(), new Color(20, 20, 20, 255).getRGB());
            }
            Font.get.renderText(stack, "Drawn", (float) (getX() + 6), (float) (getY() + 4),
                    new Color(155, 155, 155, 255).getRGB());
        }
    }

    @Override
    public void mouseDown(int mX, int mY, int mB) {
        if (isHover(getX(), getY(), getW(), getH() - 1, mX, mY) && (mB == 0 || mB == 1)) {
            mc.player.playSound(SoundEvents.UI_BUTTON_CLICK, 1f, 1f);
            this.getModule().drawn = !this.getModule().drawn;
        }
    }
}
