package bedtrap.development.systems.commands;

import bedtrap.development.BedTrap;
import bedtrap.development.ic.Command;
import bedtrap.development.systems.utils.game.ChatUtils;
import net.minecraft.util.Formatting;

public class FriendCommand extends Command {

    public FriendCommand() {
        super(new String[]{"f", "friend"});
    }

    @Override
    public void onCommand(String name, String[] args) {
        if (args.length < 2) {
            ChatUtils.info("Friends","Invalid input. Usage: " + getPrefix() + "f <add/del> <name>");
            return;
        }

        if (args[0].equalsIgnoreCase("add")) {
            if (BedTrap.getFriendManager().isFriend(args[1])) {
                ChatUtils.info("Friends",args[1] + " is already in your friend list.");
            } else {
                BedTrap.getFriendManager().getFriends().add(args[1]);
                ChatUtils.info("Friends",args[1] + " is added to the friend list.");
            }
        } else if (args[0].equalsIgnoreCase("del") || args[0].equalsIgnoreCase("remove")) {
            if (BedTrap.getFriendManager().getFriends().isEmpty()) {
                ChatUtils.info("Friends","The target player isn't found in friend list.");

            } else if (!BedTrap.getFriendManager().isFriend(args[1])) {
                ChatUtils.info("Friends",args[1] + " The target player isn't found in friend list.");
            } else {
                ChatUtils.info("Friends",args[1] + " is removed from friend list.");
                BedTrap.getFriendManager().getFriends().remove(args[1]);
            }
        } else {
            sendClientMessage("Invalid input! Usage: " + getPrefix() + "f <add/del> <name>");
        }

    }

}
