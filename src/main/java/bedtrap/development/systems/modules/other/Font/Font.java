package bedtrap.development.systems.modules.other.Font;

import bedtrap.development.ic.Setting;
import bedtrap.development.ic.util.text.IFont;
import bedtrap.development.systems.modules.Module;
import net.minecraft.client.util.math.MatrixStack;

@Module.Info(name = "Font", category = Module.Category.Other, drawn = false)
public class Font extends Module {
    private Setting<Boolean> hud = register("Hud", false);
    public void renderText(MatrixStack matrices, String text, float x, float y, int color) {
        if (isActive()) IFont.legacy18.drawStringWithShadow(matrices, text, x - 1, y - 2, color);
        else mc.textRenderer.drawWithShadow(matrices, text, x, y, color);
    }

    public void renderHudText(MatrixStack matrices, String text, float x, float y, int color) {
        if (hud()) IFont.legacy18.drawStringWithShadow(matrices, text, x - 1, y - 2, color);
        else mc.textRenderer.drawWithShadow(matrices, text, x, y, color);
    }

    public double getWidth(String text) {
        return isActive() ? IFont.legacy18.getWidthIgnoreChar(text) : mc.textRenderer.getWidth(text);
    }

    public boolean hud() {
        return isActive() && hud.get();
    }

    public static Font get;

    public Font() {
        get = this;
    }
}
