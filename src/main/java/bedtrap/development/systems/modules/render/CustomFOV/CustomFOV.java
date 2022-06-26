package bedtrap.development.systems.modules.render.CustomFOV;

import bedtrap.development.events.event.TickEvent;
import bedtrap.development.ic.Setting;
import bedtrap.development.systems.modules.Module;
import com.google.common.eventbus.Subscribe;

@Module.Info(name = "CustomFov", category = Module.Category.Render)
public class CustomFOV extends Module {
    public Setting<Double> fov = register("Fov", 125, 100, 200, 0);

    @Subscribe
    public void onTick(TickEvent.Post event) {
        setDisplayInfo(fov.get()+"");
        mc.options.fov = fov.get();
    }
}
