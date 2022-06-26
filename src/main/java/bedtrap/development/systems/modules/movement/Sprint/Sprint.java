package bedtrap.development.systems.modules.movement.Sprint;

import bedtrap.development.events.event.PlayerMoveEvent;
import bedtrap.development.events.event.SetPlayerSprintEvent;
import bedtrap.development.systems.modules.Module;
import bedtrap.development.ic.Setting;
import com.google.common.eventbus.Subscribe;

import java.util.Arrays;

@Module.Info(name = "Sprint", category = Module.Category.Movement)
public class Sprint extends Module {
    Setting<String> mode = register("Mode", Arrays.asList("Normal", "Rage"), "Normal");

    @Subscribe
    public void onMove(PlayerMoveEvent event) {
        setDisplayInfo(mode.get());
        if (mc.player.input.movementForward + mc.player.input.movementSideways != 0)
            mc.player.setSprinting(true);
    }

    @Subscribe
    public void onShit(SetPlayerSprintEvent event) {
        if (mode.get("Rage") && !mc.player.isSubmergedInWater() && !mc.player.isInLava() && !mc.player.input.jumping)
            event.setSprinting(false).setCancelled(true);
    }
}
