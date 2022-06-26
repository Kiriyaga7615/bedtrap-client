package bedtrap.development.gui.button.settings;

import bedtrap.development.ic.Setting;
import bedtrap.development.gui.button.SettingButton;
import bedtrap.development.systems.modules.Module;
import bedtrap.development.systems.modules.other.Colors.Colors;
import bedtrap.development.systems.modules.other.Font.Font;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;

import java.awt.*;

public class ModeButton extends SettingButton {
    private final Setting<String> setting;

    public ModeButton(Module module, Setting setting, int X, int Y, int W, int H) {
        super(module, X, Y, W, H);
        this.setting = setting;
    }

    @Override
    public void render(int mX, int mY) {
        drawButton();
        MatrixStack stack = new MatrixStack();
        stack.scale(Colors.s, Colors.s, Colors.s);
        drawButton(mX, mY);

        Font.get.renderText(stack, setting.getName(),
                (float) (getX() + 6),
                (float) (getY() + 4),
                new Color(255, 255, 255, 255).getRGB());

        Font.get.renderText(stack, setting.get(),
                (float) ((getX() + 6) + Font.get.getWidth(" " + setting.getName())),
                (float) (getY() + 4), new Color(155, 155, 155, 255).getRGB());

    }

    @Override
    public void mouseDown(int mX, int mY, int mB) {
        if (isHover(getX(), getY(), getW(), getH() - 1, mX, mY)) {
            mc.player.playSound(SoundEvents.UI_BUTTON_CLICK, 1f, 1f);
            if (mB == 0) {
                int i = 0;
                int enumIndex = 0;
                for (String enumName : setting.getModes()) {
                    if (enumName.equals(setting.get()))
                        enumIndex = i;
                    i++;
                }
                if (enumIndex == setting.getModes().size() - 1) {
                    setting.setValue(setting.getModes().get(0));
                } else {
                    enumIndex++;
                    i = 0;
                    for (String enumName : setting.getModes()) {
                        if (i == enumIndex)
                            setting.setValue(enumName);
                        i++;
                    }
                }
            } else if (mB == 1) {
                int i = 0;
                int enumIndex = 0;
                for (String enumName : setting.getModes()) {
                    if (enumName.equals(setting.get()))
                        enumIndex = i;
                    i++;
                }
                if (enumIndex == 0) {
                    setting.setValue(setting.getModes().get(setting.getModes().size() - 1));
                } else {
                    enumIndex--;
                    i = 0;
                    for (String enumName : setting.getModes()) {
                        if (i == enumIndex)
                            setting.setValue(enumName);
                        i++;
                    }
                }
            }
        }
    }
}
