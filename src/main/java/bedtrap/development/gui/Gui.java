package bedtrap.development.gui;

import bedtrap.development.ic.util.Wrapper;
import bedtrap.development.systems.modules.Module;
import bedtrap.development.systems.modules.other.Colors.Colors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

import java.awt.*;
import java.util.ArrayList;

public class Gui extends Screen {
    public static ArrayList<bedtrap.development.gui.Window> windows = new ArrayList<>();

    public Gui() {
        super(new LiteralText("Colors"));
        windows = new ArrayList<>();
        int xOffset = 3;
        for (Module.Category category : Module.Category.values()) {
            windows.add(new bedtrap.development.gui.Window(category, xOffset, 3, 95, 15));
            xOffset += 100;
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
        matrices.scale(Colors.s, Colors.s, Colors.s);
        DrawableHelper.fill(matrices, 0, 0, MinecraftClient.getInstance().getWindow().getScaledWidth(), MinecraftClient.getInstance().getWindow().getScaledHeight(), new Color(0, 0, 0, 150).getRGB());
        for (bedtrap.development.gui.Window window : windows) {
            window.render(mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        windows.forEach(window -> window.mouseDown((int) mouseX, (int) mouseY, mouseButton));
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int state) {
        windows.forEach(window -> window.mouseUp((int) mouseX, (int) mouseY));
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (bedtrap.development.gui.Window window : windows) {
            window.keyPress(keyCode);
        }

        if (keyCode == InputUtil.fromTranslationKey("key.keyboard.escape").getCode()) {
            Wrapper.mc.setScreen(null);
            Colors.get.toggle();
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }


    public void drawGradient(int left, int top, int right, int bottom, int startColor, int endColor) {
        MatrixStack st = new MatrixStack();
        st.scale(Colors.s, Colors.s, Colors.s);
        this.fillGradient(st, left, top, right, bottom, startColor, endColor);
    }

    public void drawFlat(int left, int top, int right, int bottom, int startColor, int endColor) {
        MatrixStack st = new MatrixStack();
        st.scale(Colors.s, Colors.s, Colors.s);
        this.fillGradient(st, left, top, right, bottom, startColor, startColor);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (amount < 0) {
            for (bedtrap.development.gui.Window window : windows) {
                window.setY(window.getY() - 5);
            }
        } else if (amount > 0) {
            for (Window window : windows) {
                window.setY(window.getY() + 5);
            }
        }
        return super.mouseScrolled(mouseX, mouseY, amount);
    }
}
