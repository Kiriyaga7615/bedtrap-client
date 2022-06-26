package bedtrap.development.events.event;

import bedtrap.development.events.Cancelled;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;

public class PlayerMoveEvent extends Cancelled {
    private static final PlayerMoveEvent INSTANCE = new PlayerMoveEvent();

    public MovementType type;
    public Vec3d movement;

    public static PlayerMoveEvent get(MovementType type, Vec3d movement) {
        INSTANCE.type = type;
        INSTANCE.movement = movement;
        return INSTANCE;
    }
}