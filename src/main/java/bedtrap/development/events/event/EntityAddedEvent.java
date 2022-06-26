package bedtrap.development.events.event;

import bedtrap.development.events.Cancelled;
import net.minecraft.entity.Entity;

public class EntityAddedEvent extends Cancelled {

    public Entity entity;

    public EntityAddedEvent(Entity entity) {
        this.entity = entity;
    }

}
