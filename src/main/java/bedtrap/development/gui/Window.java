package bedtrap.development.gui;

import bedtrap.development.BedTrap;
import bedtrap.development.gui.button.SettingButton;
import bedtrap.development.systems.modules.Modules;
import bedtrap.development.systems.modules.Module;
import bedtrap.development.gui.button.ModuleButton;
import bedtrap.development.systems.modules.other.Colors.Colors;
import bedtrap.development.systems.modules.other.Font.Font;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;

import java.awt.*;
import java.util.ArrayList;

import static bedtrap.development.systems.modules.Module.mc;

public class Window implements Component {
    private final ArrayList<ModuleButton> buttons = new ArrayList<>();
    private final Module.Category category;
    private final int W;
    private final int H;
    private final ArrayList<ModuleButton> buttonsBeforeClosing = new ArrayList<>();
    private int X;
    private int Y;
    private int dragX;
    private int dragY;
    private boolean open = true;
    private boolean dragging;
    private int totalHeightForBars;
    private int showingButtonCount;

    public Window(Module.Category category, int x, int y, int w, int h) {
        this.category = category;

        X = x;
        Y = y;


        W = w;
        H = h;

        int yOffset = Y + H;

        for (Module module : Modules.getModules(category)) {
            ModuleButton button = new ModuleButton(module, X, yOffset, W, H);
            buttons.add(button);
            yOffset += H;
            totalHeightForBars++;
        }
        showingButtonCount = buttons.size();
    }

    @Override
    public void render(int mX, int mY) {

        if (dragging) {
            X = dragX + mX;
            Y = dragY + mY;
        }

        BedTrap.getClickGui().drawFlat(X, Y, X + W, Y + H,
                new Color(Colors.get.r.get().intValue(), Colors.get.g.get().intValue(),
                        Colors.get.b.get().intValue(), 255).getRGB(),
                new Color(Colors.get.r.get().intValue(), Colors.get.g.get().intValue(),
                        Colors.get.b.get().intValue(), 255).getRGB());
        MatrixStack stack = new MatrixStack();
        stack.scale(Colors.s, Colors.s, Colors.s);
        Font.get.renderText(stack, category.name(), X + 4, Y + 4, -1);

        if (open) {
            DrawableHelper.fill(stack, X, Y + H, X + W, Y + H + 1, new Color(20, 20, 20, 255).getRGB());

            int modY = Y + H + 1;
            totalHeightForBars = 0;

            int moduleRenderCount = 0;
            for (ModuleButton moduleButton : buttons) {
                moduleRenderCount++;

                if (moduleRenderCount < showingButtonCount + 1) {
                    moduleButton.setX(X);
                    moduleButton.setY(modY);

                    moduleButton.render(mX, mY);

                    modY += H;
                    totalHeightForBars++;

                    if (moduleButton.isOpen()) {

                        int settingRenderCount = 0;
                        for (SettingButton settingButton : moduleButton.getButtons()) {
                            settingRenderCount++;

                            if (settingRenderCount < moduleButton.getShowingModuleCount() + 1) {
                                settingButton.setX(X);
                                settingButton.setY(modY);

                                settingButton.render(mX, mY);

                                modY += H;
                                totalHeightForBars++;
                            }
                        }
                    }
                }
            }
            DrawableHelper.fill(stack, X, Y + H + ((totalHeightForBars) * H) + 2, X + W,
                    Y + H + ((totalHeightForBars) * H) + 3, new Color(20, 20, 20, 255).getRGB());
        }
    }

    @Override
    public void mouseDown(int mX, int mY, int mB) {
        if (isHover(X, Y, W, H, mX, mY)) {
            if (mB == 0) {
                dragging = true;
                dragX = X - mX;
                dragY = Y - mY;
            } else if (mB == 1) {
                if (open) {
                    showingButtonCount = buttons.size();
                    mc.player.playSound(SoundEvents.UI_BUTTON_CLICK, 1f, 1f);
                    open = false;
                    for (ModuleButton button : buttons) {
                        if (button.isOpen()) {
                            button.processRightClick();
                            buttonsBeforeClosing.add(button);
                        }
                    }
                } else if (!open) {
                    showingButtonCount = buttons.size();
                    mc.player.playSound(SoundEvents.UI_BUTTON_CLICK, 1f, 1f);
                    open = true;
                    buttonsBeforeClosing.clear();                }
            }
        }

        if (open) {
            for (ModuleButton button : buttons) {
                button.mouseDown(mX, mY, mB);
            }
        }
    }

    @Override
    public void mouseUp(int mX, int mY) {
        dragging = false;

        if (open) {
            for (ModuleButton button : buttons) {
                button.mouseUp(mX, mY);
            }
        }
    }

    @Override
    public void keyPress(int key) {
        if (open) {
            for (ModuleButton button : buttons) {
                button.keyPress(key);
            }
        }
    }

    @Override
    public void close() {
        for (ModuleButton button : buttons) {
            button.close();
        }
    }

    private boolean isHover(int X, int Y, int W, int H, int mX, int mY) {
        return mX >= X * Colors.s && mX <= (X + W) * Colors.s && mY >= Y * Colors.s && mY <= (Y + H) * Colors.s;
    }

    public Module.Category getCategory() {
        return category;
    }

    public int getX() {
        return X;
    }

    public int getY() {
        return Y;
    }

    public void setY(int y) {
        Y = y;
    }

    public int getH() {
        return H;
    }

}
