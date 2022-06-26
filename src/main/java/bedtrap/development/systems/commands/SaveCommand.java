package bedtrap.development.systems.commands;

import bedtrap.development.ic.Command;
import bedtrap.development.ic.Configs;
import bedtrap.development.systems.utils.game.ChatUtils;
import net.minecraft.util.Formatting;

public class SaveCommand extends Command {

    public SaveCommand() {
        super(new String[]{"save"});
    }

    @Override
    public void onCommand(String name, String[] args) {
        try{
            Configs.get.start();
            ChatUtils.info("Config","Successfully saved.");
        } catch (Exception e){
            ChatUtils.info("Config","Exception while saving.");
        }
    }
}