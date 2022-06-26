package bedtrap.development.systems.commands;

import bedtrap.development.ic.Command;
import bedtrap.development.systems.utils.game.ChatUtils;
import net.minecraft.util.Formatting;



public class PrefixCommand extends Command {

    public PrefixCommand() {
        super(new String[]{"prefix", "p"});
    }

    @Override
    public void onCommand(String name, String[] args) {
        if (args.length == 0) {
            ChatUtils.info("Prefix","Invalid input! Usage: " + getPrefix() + "p <value>");
            return;
        }

        Command.setPrefix(String.valueOf(args[0]));
        ChatUtils.info("Prefix","Prefix was updated.");
    }
}
