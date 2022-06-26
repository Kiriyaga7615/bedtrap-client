package bedtrap.development.systems.utils.game;

import bedtrap.development.mixins.IChatHud;
import bedtrap.development.systems.modules.other.Colors.Colors;
import bedtrap.development.systems.modules.other.HUD.HUD;
import net.minecraft.text.BaseText;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static bedtrap.development.ic.util.Wrapper.mc;

public class ChatUtils {
    public static Text PREFIX;
    public static void info(String text) {
        if (mc.world == null) return;

        PREFIX = new LiteralText("[BedTrap] ")
                .setStyle(Style.EMPTY.withColor(HUD.get.color()));

        BaseText message = new LiteralText("");
        message.append(PREFIX);
        message.setStyle(message.getStyle().withFormatting(Formatting.GRAY));
        message.append(text);

        ((IChatHud) mc.inGameHud.getChatHud()).add(message, 0);
    }

    public static void info(String module, String text) {
        if (mc.world == null) return;
        Text MODULE;

        PREFIX = new LiteralText("[BedTrap] ")
                .setStyle(Style.EMPTY.withColor(HUD.get.color()));
        MODULE = new LiteralText("[" + module + "] ")
                .setStyle(Style.EMPTY.withColor(HUD.get.revertColor()));

        BaseText message = new LiteralText("");
        message.append(PREFIX);
        message.append(MODULE);
        message.setStyle(message.getStyle().withFormatting(Formatting.GRAY));
        message.append(text);

        ((IChatHud) mc.inGameHud.getChatHud()).add(message, 0);
    }

    public void infoModule(String name, boolean isActive) {
        if (mc.world == null) return;
        PREFIX = new LiteralText("[BedTrap] ")
                .setStyle(Style.EMPTY.withColor(HUD.get.color()));

        if (name.equals(Colors.get.name)) return;

        BaseText message = new LiteralText("");
        message.append(PREFIX);
        message.append(Formatting.GRAY + name + " toggled ");
        message.append(isActive ? Formatting.GREEN + "on" + Formatting.GRAY + "." : Formatting.RED + "off" + Formatting.GRAY + ".");

        ((IChatHud) mc.inGameHud.getChatHud()).add(message, 86741);
    }
}
