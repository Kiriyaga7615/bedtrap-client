package bedtrap.development.systems.modules.movement.AutoWalk;

import bedtrap.development.events.event.TickEvent;
import bedtrap.development.ic.Setting;
import bedtrap.development.systems.modules.Module;
import com.google.common.eventbus.Subscribe;
import net.minecraft.client.option.KeyBinding;

import java.util.List;

@Module.Info(name = "AutoWalk", category = Module.Category.Movement)
public class AutoWalk extends Module {
    public Setting<String> direction = register("Direction", List.of("Forward", "Back", "Right", "Left"), "Forward");
    public Setting<Boolean> airStop = register("StopOnAir", true);
    public Setting<Boolean> shiftStop = register("StopOnShift", true);

    private KeyBinding key;

    @Override
    public void onDeactivate() {
        key.setPressed(false);
    }

    @Subscribe
    public void onTick(TickEvent.Post event) {
        key = switch (direction.get()) {
            case "Forward" -> mc.options.forwardKey;
            case "Back" -> mc.options.backKey;
            case "Right" -> mc.options.rightKey;
            case "Left" -> mc.options.leftKey;
            default -> throw new IllegalStateException("Unexpected value: " + direction.get());
        };

        key.setPressed(!onAir() && !onShift());
    }

    private boolean onAir() {
        return airStop.get() && mc.player.fallDistance > 2;
    }

    private boolean onShift() {
        return shiftStop.get() && mc.player.isSneaking();
    }
}