package bedtrap.development.systems.modules.miscellaneous.AutoRespawn;

import bedtrap.development.events.event.TickEvent;
import bedtrap.development.systems.modules.Module;
import com.google.common.eventbus.Subscribe;

@Module.Info(name = "AutoRespawn", category = Module.Category.Miscellaneous)
public class AutoRespawn extends Module {

    @Subscribe
    private void onTick(TickEvent.Post event) {
        if (mc.player.isDead()) {
            mc.player.requestRespawn();
            mc.setScreen(null);
        }
    }
}

