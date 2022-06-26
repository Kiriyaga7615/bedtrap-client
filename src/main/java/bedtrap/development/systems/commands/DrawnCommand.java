package bedtrap.development.systems.commands;

import bedtrap.development.BedTrap;
import bedtrap.development.ic.Command;
import bedtrap.development.systems.modules.Module;
import bedtrap.development.systems.utils.game.ChatUtils;
import net.minecraft.util.Formatting;



public class DrawnCommand extends Command {

    boolean found = false;

    public DrawnCommand() {
        super(new String[]{"d", "drawn"});
    }

    @Override
    public void onCommand(String name, String[] args) {
        if (args.length == 0) {
            ChatUtils.info("Drawn","Invalid input. Usage: " + getPrefix() + "drawn <Module>");
            return;
        }

        Module module = BedTrap.getModuleManager().getModule(args[0]);
        if (module != null) {
            module.setDrawn(!module.drawn);
            if (module.isDrawn())
                ChatUtils.info("Drawn",module.getName() + " now drawn.");
            else
                ChatUtils.info("Drawn",module.getName() + " is no more drawn.");
        } else
            ChatUtils.info("Drawn","Module isn't existing.");
    }

}
