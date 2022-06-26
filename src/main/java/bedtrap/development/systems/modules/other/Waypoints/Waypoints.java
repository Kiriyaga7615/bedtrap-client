package bedtrap.development.systems.modules.other.Waypoints;

import bedtrap.development.events.event.Render3DEvent;
import bedtrap.development.ic.Setting;
import bedtrap.development.ic.util.Renderer3D;
import bedtrap.development.systems.modules.Module;
import bedtrap.development.systems.utils.other.Colors;
import com.google.common.eventbus.Subscribe;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;

import static bedtrap.development.systems.modules.other.Waypoints.WUtils.distanceTo;

@Module.Info(name = "Waypoints", category = Module.Category.Other, drawn = false)
public class Waypoints extends Module {
    private final Setting<Boolean> distance = register("Distance", true);
    private final Setting<Boolean> background = register("Background", false);
    private final Setting<Double> scale = register("Scale", 0, -5, 5, 2);

    public final ArrayList<Waypoint> waypoints = new ArrayList<>();

    @Override
    public void onActivate() {
        if (!waypoints.isEmpty()) return;

        info(name, "Waypoints is empty.");
        info(name, "Please, type \".w add 0 0 0 name color\"");
    }

    @Subscribe
    public void onRender(Render3DEvent event) {
        if (waypoints.isEmpty()) return;

        waypoints.forEach(waypoint -> {
            Vec3d rPos = new Vec3d(waypoint.blockPos.getX() + 0.5, waypoint.blockPos.getY() + 0.5, waypoint.blockPos.getZ() + 0.5);
            double size = Math.max(2 * (distanceTo(waypoint.blockPos) / 20), 1);

            Renderer3D.drawText(new LiteralText("").setStyle(Style.EMPTY.withFormatting(waypoint.color)).append(waypoint.name), rPos.x, rPos.y, rPos.z, size * scale.get(), background.get());
            if (distance.get())
                Renderer3D.drawText(new LiteralText(String.valueOf(((double) Math.round(distanceTo(waypoint.blockPos) * 100) / 100))), rPos.x, (rPos.y - 0.20) - (size / 2), rPos.z, size * scale.get(), background.get());
        });
    }

    public Waypoint getWaypointById(int id) {
        for (Waypoint waypoint : waypoints) {
            if (waypoint.id == id) return waypoint;
        }

        return new Waypoint(new BlockPos(0, 0, 0), "null", Formatting.WHITE, -1);
    }

    public boolean matchesId(int id) {
        if (waypoints.isEmpty()) return false;

        return getWaypointById(id).id != -1;
    }

    public static Waypoints get;

    public Waypoints() {
        get = this;
    }

    public static class Waypoint {
        private BlockPos blockPos;
        private String name;
        private Formatting color;
        private int id;

        public Waypoint(BlockPos blockPos, String name, Formatting color, int id) {
            this.blockPos = blockPos;
            this.name = name;
            this.color = color;
            this.id = id;
        }

        public String getName() {
            return name + Formatting.GRAY;
        }

        public Formatting getColor() {
            return color;
        }

        public String writeId() {
            return String.valueOf(id);
        }

        public String writeData() {
            return blockPos.getX() + " " + blockPos.getY() + " " + blockPos.getZ() + ":" + name + ":" + Colors.getString(color) + ":" + id;
        }
    }
}
