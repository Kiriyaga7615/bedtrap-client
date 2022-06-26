package bedtrap.development.systems.modules.miscellaneous.TPSSync;

import bedtrap.development.events.event.TickEvent;
import bedtrap.development.systems.modules.Module;
import bedtrap.development.systems.modules.movement.Timer.Timer;
import bedtrap.development.systems.modules.other.HUD.HUD;
import com.google.common.eventbus.Subscribe;

@Module.Info(name = "TPSSync", category = Module.Category.Miscellaneous)
public class TPSSync extends Module {

    @Override
    public void onDeactivate() {
        Timer.get.timerOverride(1.0F);
    }

    @Subscribe
    public void onTick(TickEvent.Post event) {
        float tps = HUD.get.getTickRate();

        Timer.get.timerOverride((tps >= 1 ? tps : 1) / 20);
    }
}
