package bedtrap.development.events.event;

import bedtrap.development.events.Cancelled;

public class SetPlayerSprintEvent extends Cancelled {
    private boolean sprinting;

    public SetPlayerSprintEvent(boolean sprinting) {
        this.sprinting = sprinting;
    }

    public boolean isSprinting() {
        return sprinting;
    }

    public SetPlayerSprintEvent setSprinting(boolean sprinting) {
        this.sprinting = sprinting;
        return this;
    }
}