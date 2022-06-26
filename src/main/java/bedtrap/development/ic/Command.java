package bedtrap.development.ic;

import bedtrap.development.mixins.IChatHud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;


public class Command {

    public static MinecraftClient mc;
    private static String prefix = ".";

    static {
        mc = MinecraftClient.getInstance();
    }

    String[] name;

    public Command(String[] command) {
        this.name = command;
    }

    public static void sendClientMessage(String string) {
        ((IChatHud) mc.inGameHud.getChatHud()).add(new LiteralText("" + Formatting.RED + "[BedTrap] " + string), 86741);
    }

    public static String getPrefix() {
        return prefix;
    }

    public static void setPrefix(String prefix) {
        Command.prefix = prefix;
    }

	public String[] getName() {
        return name;
    }

    public void onCommand(String command, String[] args) {
    }
}
