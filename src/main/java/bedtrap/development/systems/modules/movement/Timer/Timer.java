package bedtrap.development.systems.modules.movement.Timer;

import bedtrap.development.events.event.TickEvent;
import bedtrap.development.ic.Setting;
import bedtrap.development.systems.modules.Module;
import bedtrap.development.systems.modules.other.HUD.HUD;
import com.google.common.eventbus.Subscribe;

@Module.Info(name = "Timer", category = Module.Category.Movement)
public class Timer extends Module {

    public static Timer get;
    public Setting<Double> factor = register("Factor", 1, 0.1, 5, 1);

    public Timer() {
        get = this;
    }

    public static float override = 1;

    @Subscribe
    public void onTick(TickEvent.Post event){
        get = this;
    }

    public float getMultiplier() {
        if (override != 1) return override;
        if (isActive()) return factor.get().floatValue();
        return 1;
    }

    public void timerOverride(float override) {
        this.override = override;
    }

}
