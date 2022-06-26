package bedtrap.development.systems.commands;

import bedtrap.development.ic.Command;
import bedtrap.development.ic.Configs;
import bedtrap.development.systems.utils.game.ChatUtils;
import net.minecraft.util.Formatting;

public class LoadCommand extends Command {

    public LoadCommand() {
        super(new String[]{"load"});
    }

    @Override
    public void onCommand(String name, String[] args) {
        try{
            Configs.get.load();
            ChatUtils.info("Config","Successfully loaded.");
        } catch (Exception e){
            ChatUtils.info("Config","Exception while loading.");
        }
    }
}
