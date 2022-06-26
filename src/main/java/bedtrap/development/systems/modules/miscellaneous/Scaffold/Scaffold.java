package bedtrap.development.systems.modules.miscellaneous.Scaffold;

import bedtrap.development.events.event.TickEvent;
import bedtrap.development.ic.Setting;
import bedtrap.development.systems.modules.Module;
import bedtrap.development.systems.utils.advanced.FindItemResult;
import bedtrap.development.systems.utils.advanced.InvUtils;
import com.google.common.eventbus.Subscribe;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;

import static bedtrap.development.systems.modules.miscellaneous.Scaffold.SFutils.onStand;
import static bedtrap.development.systems.modules.miscellaneous.Scaffold.SFutils.place;

@Module.Info(name = "Scaffold", category = Module.Category.Miscellaneous)
public class Scaffold extends Module {
    public Setting<Integer> delay = register("PlaceDelay", 5, 0, 7);
    public Setting<Boolean> predict = register("PredictMotion", false);
    private BlockPos pos;
    private FindItemResult item;

    @Subscribe
    public void onTick(TickEvent.Post event) {
        pos = mc.player.getBlockPos();
        if (mc.player.getBlockPos() == null) return;
        item = InvUtils.findInHotbar(Items.OBSIDIAN);
        if (!item.found()) {
            info("Cant found in invetory current Item.Disabling...");
            toggle();
            return;
        }
        for (int p = 0; p <= delay.get(); p++) {
            place(pos.down(1), item);
            if (predict.get() && !onStand()) place(pos.down(1).offset(mc.player.getMovementDirection()), item);
        }
    }
}