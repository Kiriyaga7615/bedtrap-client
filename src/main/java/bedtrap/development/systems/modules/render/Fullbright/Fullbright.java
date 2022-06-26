package bedtrap.development.systems.modules.render.Fullbright;

import bedtrap.development.events.event.TickEvent;
import bedtrap.development.systems.modules.Module;
import com.google.common.eventbus.Subscribe;

@Module.Info(name = "Fullbright", category = Module.Category.Render)
public class Fullbright extends Module {

    @Subscribe
    public void onTick(TickEvent.Post event) {
        mc.options.gamma = 420;
    }
}
