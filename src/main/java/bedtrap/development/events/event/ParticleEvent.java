package bedtrap.development.events.event;

import bedtrap.development.events.Cancelled;
import net.minecraft.particle.ParticleEffect;

public class ParticleEvent extends Cancelled {
    public ParticleEffect particle;

    public ParticleEvent(ParticleEffect particle) {
        this.setCancelled(false);
        this.particle = particle;
    }
}
