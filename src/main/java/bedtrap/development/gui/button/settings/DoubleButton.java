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

public class DoubleButton extends SettingButton {
    private final Setting<Double> setting;
    protected boolean dragging;
    protected double sliderWidth;

    DoubleButton(Module module, Setting setting, int X, int Y, int W, int H) {
        super(module, X, Y, W, H);
        this.dragging = false;
        this.sliderWidth = 0;
        this.setting = setting;
    }

    protected void updateSlider(int mouseX) {
    }

    @Override
    public void render(int mX, int mY) {
        updateSlider(mX);

        drawButton();

        MatrixStack stack = new MatrixStack();
        stack.scale(Colors.s, Colors.s, Colors.s);

        if (isHover(getX(), getY(), getW(), getH() - 1, mX, mY)) {
            BedTrap.getClickGui().drawFlat((int) (getX() + 3 + (sliderWidth)), getY() + 1, getX() + getW() - 3,
                    getY() + getH(), new Color(20, 20, 20, 255).getRGB(), new Color(20, 20, 20, 255).getRGB());
            BedTrap.getClickGui().drawFlat(getX() + 3, getY() + 1, (int) (getX() - 2 + (sliderWidth) + 5),
                    getY() + getH(),
                    new Color(Colors.get.r.get().intValue(), Colors.get.g.get().intValue(),
                            Colors.get.b.get().intValue(), 255).darker().getRGB(),
                    new Color(Colors.get.r.get().intValue(), Colors.get.g.get().intValue(),
                            Colors.get.b.get().intValue(), 255).darker().getRGB());
        } else {
            BedTrap.getClickGui().drawFlat((int) (getX() + 3 + (sliderWidth)), getY() + 1, getX() + getW() - 3,
                    getY() + getH(), new Color(20, 20, 20, 255).getRGB(), new Color(20, 20, 20, 255).getRGB());
            BedTrap.getClickGui().drawFlat(getX() + 3, getY() + 1, (int) (getX() - 2 + (sliderWidth) + 5),
                    getY() + getH(),
                    new Color(Colors.get.r.get().intValue(), Colors.get.g.get().intValue(),
                            Colors.get.b.get().intValue(), 255).getRGB(),
                    new Color(Colors.get.r.get().intValue(), Colors.get.g.get().intValue(),
                            Colors.get.b.get().intValue(), 255).getRGB());
        }

        Font.get.renderText(stack, setting.getName(), (float) (getX() + 6), (float) (getY() + 4),
                new Color(255, 255, 255, 255).getRGB());

        Font.get.renderText(stack,
                String.format("%." + setting.getInc() + "f", setting.get()).replace(",", "."),
                (float) ((getX() + 6) + Font.get.getWidth(" " + setting.getName())),
                (float) (getY() + 4), new Color(155, 155, 155, 255).getRGB());

    }

    @Override
    public void mouseDown(int mX, int mY, int mB) {
        if (isHover(getX(), getY(), getW(), getH() - 1, mX, mY)) {
            if (!dragging) mc.player.playSound(SoundEvents.UI_BUTTON_CLICK, 1f, 1f);
            dragging = true;
        }
    }

    @Override
    public void mouseUp(int mouseX, int mouseY) {
        dragging = false;
    }

    @Override
    public void close() {
        dragging = false;
    }

    public static class Slider extends DoubleButton {
        private final Setting<Double> intSetting;

        public Slider(Module module, Setting setting, int X, int Y, int W, int H) {
            super(module, setting, X, Y, W, H);
            intSetting = setting;
        }

        @Override
        protected void updateSlider(final int mouseX) {
            final double diff = Math.min(getW(), Math.max(0, mouseX - getX()));
            final double min = intSetting.getMin();
            final double max = intSetting.getMax();
            sliderWidth = (getW() - 6) * (intSetting.get() - min) / (max - min);
            if (dragging) {
                if (diff == 0.0) {
                    intSetting.setValue(intSetting.getMin());
                } else {
                    intSetting.setValue(Double.parseDouble(String.format("%." + intSetting.getInc() + "f", diff / getW() * (max - min) + min).replace(",", ".")));
                }
            }
        }
    }
}
