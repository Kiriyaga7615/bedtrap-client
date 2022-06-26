package bedtrap.development.gui.button;

import bedtrap.development.BedTrap;
import bedtrap.development.ic.Setting;
import bedtrap.development.gui.Component;
import bedtrap.development.gui.button.settings.*;
import bedtrap.development.systems.modules.Module;
import bedtrap.development.systems.modules.other.Colors.Colors;
import bedtrap.development.systems.modules.other.Font.Font;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.util.ArrayList;

public class ModuleButton implements Component {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private final Module module;
    private final ArrayList<SettingButton> buttons = new ArrayList<>();
    private final int W;
    private final int H;
    private int X;
    private int Y;
    private boolean open;
    private int showingModuleCount;

    public ModuleButton(Module module, int x, int y, int w, int h) {
        this.module = module;
        X = x;
        Y = y;
        W = w;
        H = h;

        int n = 0;
        for (Setting setting : BedTrap.getSettingManager().getSettingsForMod(module)) {

            SettingButton settingButton = null;

            switch (setting.getType()) {
                case Boolean -> settingButton = new BooleanButton(module, setting, X, Y + H + n, W, H);
                case Integer -> settingButton = new IntegerButton.Slider(module, setting, X, Y + H + n, W, H);
                case Double -> settingButton = new DoubleButton.Slider(module, setting, X, Y + H + n, W, H);
                case Mode -> settingButton = new ModeButton(module, setting, X, Y + H + n, W, H);
            }

            buttons.add(settingButton);
            n += H;

        }

        buttons.add(new DrawnButton(module, X, Y + H + n, W, H));
        buttons.add(new BindButton(module, X, Y + H + n, W, H));
    }

    @Override
    public void render(int mX, int mY) {
        Color darkOutlineColor = new Color(20, 20, 20, 255);

        MatrixStack stack = new MatrixStack();
        stack.scale(Colors.s, Colors.s, Colors.s);

        DrawableHelper.fill(stack, X, Y, X + 2, Y + H + 2, darkOutlineColor.getRGB());
        DrawableHelper.fill(stack, X, Y, X + W, Y + 1, darkOutlineColor.getRGB());
        DrawableHelper.fill(stack, X + W, Y, X + W - 2, Y + H + 2, darkOutlineColor.getRGB());
        DrawableHelper.fill(stack, X, Y + H, X + W, Y + H + 1, darkOutlineColor.getRGB());

        if (module.isActive()) {
            if (isHover(X, Y, W, H - 1, mX, mY)) {
                BedTrap.getClickGui().drawFlat(X + 2, Y + 1, X + W - 2, Y + H,
                        new Color(Colors.get.r.get().intValue(), Colors.get.g.get().intValue(),
                                Colors.get.b.get().intValue(), 255).darker().getRGB(),
                        new Color(Colors.get.r.get().intValue(), Colors.get.g.get().intValue(),
                                Colors.get.b.get().intValue(), 255).darker().getRGB());
            } else {
                BedTrap.getClickGui().drawFlat(X + 2, Y + 1, X + W - 2, Y + H,
                        new Color(Colors.get.r.get().intValue(), Colors.get.g.get().intValue(),
                                Colors.get.b.get().intValue(), 255).getRGB(),
                        new Color(Colors.get.r.get().intValue(), Colors.get.g.get().intValue(),
                                Colors.get.b.get().intValue(), 255).getRGB());
            }
            Font.get.renderText(stack, module.getName(), (float) (X + 5), (float) (Y + 4), -1);
        } else {
            if (isHover(X, Y, W, H - 1, mX, mY)) {
                BedTrap.getClickGui().drawGradient(X + 2, Y + 1, X + W - 2, Y + H, new Color(30, 30, 30, 190).brighter().getRGB(),
                        new Color(25, 25, 25, 190).brighter().getRGB());
            } else {
                BedTrap.getClickGui().drawGradient(X + 2, Y + 1, X + W - 2, Y + H, new Color(30, 30, 30, 190).getRGB(),
                        new Color(25, 25, 25, 190).getRGB());
            }

            Font.get.renderText(stack, module.getName(), (float) (X + 5), (float) (Y + 4),
                    new Color(155, 155, 155, 255).getRGB());
        }

        MatrixStack stacks = new MatrixStack();
        stacks.scale(Colors.s, Colors.s, Colors.s);
        Identifier gear = new Identifier("picture", "gear.png");
        RenderSystem.setShaderTexture(0, gear);
        DrawableHelper.drawTexture(stacks, X + W - 13, Y + 3, 0, 0, 10, 10, 10, 10);
    }

    @Override
    public void mouseDown(int mX, int mY, int mB) {
        if (isHover(X, Y, W, H - 1, mX, mY)) {
            if (mB == 0) {
                mc.player.playSound(SoundEvents.UI_BUTTON_CLICK, 1f, 1f);
                if (!module.getName().equals("Colors")) module.toggle();
            } else if (mB == 1) {
                processRightClick();
            }
        }

        if (open) {
            for (SettingButton settingButton : buttons) {
                settingButton.mouseDown(mX, mY, mB);
            }
        }
    }

    @Override
    public void mouseUp(int mX, int mY) {
        for (SettingButton settingButton : buttons) {
            settingButton.mouseUp(mX, mY);
        }
    }

    @Override
    public void keyPress(int key) {
        for (SettingButton settingButton : buttons) {
            settingButton.keyPress(key);
        }
    }

    @Override
    public void close() {
        for (SettingButton button : buttons) {
            button.close();
        }
    }

    private boolean isHover(int X, int Y, int W, int H, int mX, int mY) {
        return mX >= X * Colors.s && mX <= (X + W) * Colors.s && mY >= Y * Colors.s && mY <= (Y + H) * Colors.s;
    }

    public void setX(int x) {
        X = x;
    }

    public void setY(int y) {
        Y = y;
    }

    public boolean isOpen() {
        return open;
    }

    public Module getModule() {
        return module;
    }

    public ArrayList<SettingButton> getButtons() {
        return buttons;
    }

    public int getShowingModuleCount() {
        return showingModuleCount;
    }

    public void processRightClick() {
        mc.player.playSound(SoundEvents.UI_BUTTON_CLICK, 1f, 1f);
        if (!open) {
            showingModuleCount = buttons.size();
            open = true;
        } else {
            showingModuleCount = 0;
            open = false;
        }
    }
}
