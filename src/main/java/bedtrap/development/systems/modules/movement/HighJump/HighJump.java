package bedtrap.development.systems.modules.movement.HighJump;

import bedtrap.development.events.event.JumpVelocityMultiplierEvent;
import bedtrap.development.events.event.TickEvent;
import bedtrap.development.ic.Setting;
import bedtrap.development.systems.modules.Module;
import com.google.common.eventbus.Subscribe;

@Module.Info(name = "HighJump", category = Module.Category.Movement)
public class HighJump extends Module {
    public Setting<Double> factor = register("Factor", 1F, 0.1F, 3F, 1);
    public Setting<Boolean> fastJump = register("FastJump", true);

    @Subscribe
    private void onJumpVelocityMultiplier(JumpVelocityMultiplierEvent event) {
        event.multiplier *= factor.get();
    }

    @Subscribe
    private void onTick(TickEvent.Post event) {
        mc.player.upwardSpeed = 1;
    }
}
