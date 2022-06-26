package bedtrap.development.systems.modules.movement.Step;

import bedtrap.development.events.event.TickEvent;
import bedtrap.development.ic.Setting;
import bedtrap.development.systems.modules.Module;
import com.google.common.eventbus.Subscribe;

import java.util.List;

@Module.Info(name = "Step", category = Module.Category.Movement)
public class Step extends Module {
    public Setting<String> mode = register("Mode", List.of("Vanilla", "Smart"), "Vanilla");
    public Setting<Integer> stepHeight = register("StepHeight", 2, 1, 4);
    public Setting<Double> speed = register("Factor", 0.1, 0.1, 2, 1);


    @Subscribe
    public void onTick(TickEvent.Post event) {
        switch (mode.get()) {
            case "Vanilla" -> mc.player.stepHeight = stepHeight.get();
            case "Smart" -> {
                //todo make in night
            }
        }
    }

    @Override
    public void onDeactivate() {
        mc.player.stepHeight = 0.6F;
    }
}