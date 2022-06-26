package bedtrap.development.gui.button;

import bedtrap.development.BedTrap;
import bedtrap.development.systems.modules.Module;
import bedtrap.development.gui.Component;
import bedtrap.development.systems.modules.other.Colors.Colors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;

public class SettingButton implements Component {
    public final MinecraftClient mc = MinecraftClient.getInstance();
    private final int H;
    private Module module;
    private int X;
    private int Y;
    private int W;

    public SettingButton(Module module, int x, int y, int w, int h) {
        this.module = module;
        X = x;
        Y = y;
        W = w;
        H = h;
    }

    @Override
    public void render(int mX, int mY) {
    }

    @Override
    public void mouseDown(int mX, int mY, int mB) {
    }

    @Override
    public void mouseUp(int mX, int mY) {
    }

    @Override
    public void keyPress(int key) {
    }

    @Override
    public void close() {
    }

    public void drawButton() {
        Color darkOutlineColor = new Color(20, 20, 20, 255);

        MatrixStack stack = new MatrixStack();
        stack.scale(Colors.s, Colors.s, Colors.s);

        DrawableHelper.fill(stack, getX(), getY(), getX() + 3, getY() + getH() + 1,
                darkOutlineColor.getRGB());
        DrawableHelper.fill(stack, getX(), getY(), getX() + getW(), getY() + 1,
                darkOutlineColor.getRGB());
        DrawableHelper.fill(stack, getX() + getW(), getY(), getX() + getW() - 3, getY() + getH() + 1,
                darkOutlineColor.getRGB());
        DrawableHelper.fill(stack, getX(), getY() + getH(), getX() + getW(), getY() + getH() + 1,
                darkOutlineColor.getRGB());
    }

    public void drawButton(int mX, int mY) {
        if (isHover(getX(), getY(), getW(), getH() - 1, mX, mY)) {
            BedTrap.getClickGui().drawGradient(getX() + 3, getY() + 1, getX() + getW() - 3, getY() + getH(),
                    new Color(Colors.get.r.get().intValue(), Colors.get.g.get().intValue(),
                            Colors.get.b.get().intValue(), 255).darker().getRGB(),
                    new Color(Colors.get.r.get().intValue(), Colors.get.g.get().intValue(),
                            Colors.get.b.get().intValue(), 255).darker().getRGB());
        } else {
            BedTrap.getClickGui().drawGradient(getX() + 3, getY() + 1, getX() + getW() - 3, getY() + getH(),
                    new Color(Colors.get.r.get().intValue(), Colors.get.g.get().intValue(),
                            Colors.get.b.get().intValue(), 255).getRGB(),
                    new Color(Colors.get.r.get().intValue(), Colors.get.g.get().intValue(),
                            Colors.get.b.get().intValue(), 255).getRGB());
        }
    }

    public int getHeight() {
        return H;
    }

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public int getX() {return X;}
    public void setX(int x) {X = x;}
    public int getY() {return Y;}
    public void setY(int y) {
        Y = y;
    }
    public int getW() {return W;}
    public void setW(int w) {
        W = w;
    }
    public int getH() {return H;}

    public boolean isHover(int X, int Y, int W, int H, int mX, int mY) {
        return mX >= X * Colors.s && mX <= (X + W) * Colors.s && mY >= Y * Colors.s && mY <= (Y + H) * Colors.s;
    }
}
