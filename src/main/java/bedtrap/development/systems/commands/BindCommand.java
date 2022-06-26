package bedtrap.development.systems.commands;

import bedtrap.development.ic.Command;
import bedtrap.development.systems.modules.Module;
import bedtrap.development.systems.modules.Modules;
import bedtrap.development.systems.utils.game.ChatUtils;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Formatting;


public class BindCommand extends Command {

    public BindCommand() {
        super(new String[]{"b", "bind"});
    }

    @Override
    public void onCommand(String name, String[] args) {

        boolean found = false;

        if (args.length == 0) {
            ChatUtils.info("Bind","Invalid input.");
            return;
        }

        for (Module m : Modules.getModules()) {
            if (m.getName().equalsIgnoreCase(args[0])) {
                found = true;
                try {
                    m.setBind(InputUtil.fromTranslationKey("key.keyboard." + args[1].toLowerCase()).getCode());
                    ChatUtils.info("Bind", m.getName() + " was bound to " + args[1].toUpperCase()+".");
                } catch (Exception e) {
                    e.printStackTrace();
                    ChatUtils.info("Bind","Invalid input. Usage: " + getPrefix() + "bind <Module> <Key>");
                }
                break;
            }
        }

        if (!found)
            ChatUtils.info("Bind","Module isn't existing.");

    }

}
