package bedtrap.development.systems.commands;

import bedtrap.development.ic.Command;
import bedtrap.development.systems.modules.other.Waypoints.Waypoints;
import bedtrap.development.systems.utils.game.ChatUtils;
import bedtrap.development.systems.utils.other.Colors;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

public class WaypointCommand extends Command {
    public static int waypointId = -1;

    public WaypointCommand() {
        super(new String[]{"w", "waypoint"});
    }

    @Override
    public void onCommand(String name, String[] args) {
        if (args[0].equalsIgnoreCase("colors")) {
            ChatUtils.info("Waypoints", "red, aqua, blue, darkblue, darkgreen, darkaqua, darkred, darkpurple, gold, gray, black, green, white, lightpurple, yellow.");
            return;
        }

        if (args.length < 2 && (args[0].equalsIgnoreCase("rem") || args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("del") || args[0].equalsIgnoreCase("delete"))) {
            ChatUtils.info("Waypoints", "Invalid input, Usage: \".w " + args[0].toLowerCase() + " id\"");
            return;
        }

        if (args.length < 6 && args[0].equalsIgnoreCase("add")) {
            ChatUtils.info("Waypoints", "Invalid input, Usage: \".w add 0 0 0 name color\"");
            return;
        }

        if (args[0].equalsIgnoreCase("add")) {
            int x, y, z;

            try {
                x = Integer.parseInt(args[1]);
                y = Integer.parseInt(args[2]);
                z = Integer.parseInt(args[3]);
            } catch (NumberFormatException e) {
                ChatUtils.info("Waypoints", "Wrong BlockPos format, Usage: \".w add 0 0 0 name color\"");
                return;
            }

            BlockPos waypointPos = new BlockPos(x, y, z);
            String waypointName = args[4];
            Formatting waypointColor = Colors.getFormatting(args[5]);

            if (waypointColor == null) {
                ChatUtils.info("Waypoints", "Wrong Formatting, Usage: \".w add 0 0 0 name color\"");
                return;
            }

            waypointId += 1;
            Waypoints.get.waypoints.add(new Waypoints.Waypoint(waypointPos, waypointName, waypointColor, waypointId));
            ChatUtils.info("Waypoints", "Successfully added " + waypointColor + waypointName + Formatting.GRAY + ".");
        } else if (args[0].equalsIgnoreCase("rem") || args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("del") || args[0].equalsIgnoreCase("delete")) {
            int id;

            try {
                id = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                ChatUtils.info("Waypoints", "Wrong id.");
                return;
            }

            if (!Waypoints.get.matchesId(id)) {
                ChatUtils.info("Waypoints", "Wrong id.");
                return;
            }

            Waypoints.Waypoint waypoint = Waypoints.get.getWaypointById(id);

            String waypointName = waypoint.getName();
            Formatting waypointColor = waypoint.getColor();

            waypointId--;
            Waypoints.get.waypoints.remove(Waypoints.get.getWaypointById(id));
            ChatUtils.info("Waypoints", "Successfully removed " + waypointColor + waypointName + ".");

        } else ChatUtils.info("Waypoints", "Please, type \".w add 0 0 0 name color\"");
    }
}