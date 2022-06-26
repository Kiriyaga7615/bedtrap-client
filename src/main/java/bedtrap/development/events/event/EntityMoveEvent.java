package bedtrap.development.events.event;

import bedtrap.development.events.Cancelled;

public class EntityMoveEvent extends Cancelled {
    public double motionX, motionY, motionZ;

    public EntityMoveEvent(final double motionX, final double motionY, final double motionZ) {
        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;
    }
}
