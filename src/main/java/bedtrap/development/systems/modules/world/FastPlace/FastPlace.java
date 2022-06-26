package bedtrap.development.systems.modules.world.FastPlace;

import bedtrap.development.events.event.TickEvent;
import bedtrap.development.ic.Setting;
import bedtrap.development.mixins.MinecraftClientAccessor;
import bedtrap.development.systems.modules.Module;
import bedtrap.development.systems.utils.advanced.FindItemResult;
import bedtrap.development.systems.utils.advanced.InvUtils;
import bedtrap.development.systems.utils.other.TimerUtils;
import com.google.common.eventbus.Subscribe;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.shape.VoxelShape;

@Module.Info(name = "FastPlace", category = Module.Category.World)
public class FastPlace extends Module {

    public Setting<Integer> delay = register("delay", 0, 0, 4);
    public Setting<Integer> startDelay = register("StartDelay", 110, 15, 500);
    public Setting<Boolean> autoPlace = register("AutoPlace", false);
    public Setting<Boolean> autoSwitch = register("AutoSwitch", false);
    public Setting<Boolean> onlyXP = register("OnlyXP", false);

    public final TimerUtils timer = new TimerUtils();

    @Override
    public void onDeactivate() {
        if (autoPlace.get()) mc.options.useKey.setPressed(false);
    }

    @Subscribe
    public void onTick(TickEvent.Post event) {
        FindItemResult blocks = InvUtils.findInHotbar(itemStack -> itemStack.getItem() instanceof BlockItem);
        FindItemResult exp = InvUtils.findInHotbar(Items.EXPERIENCE_BOTTLE);

        if (!exp.found() && (!(mc.crosshairTarget instanceof BlockHitResult))) return;
        if (exp.isMainHand() || (!onlyXP.get() && (mc.player.getMainHandStack().getItem() instanceof BlockItem || blocks.found() && autoSwitch.get()))) {
            if (autoSwitch.get() && blocks.found()) InvUtils.swap(blocks.slot(), false);
            if (autoPlace.get()) mc.options.useKey.setPressed(true);

            int i = ((MinecraftClientAccessor) mc).getItemUseCooldown();

            if (!timer.passedMillis(startDelay.get())) return;
            if (i == 0) timer.reset();
            if (!timer.passedMillis(startDelay.get())) return;

            if (i > delay.get()) i = delay.get();
            ((MinecraftClientAccessor) mc).setItemUseCooldown(i);
        }
    }
}
