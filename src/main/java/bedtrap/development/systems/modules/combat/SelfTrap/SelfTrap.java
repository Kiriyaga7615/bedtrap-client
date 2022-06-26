package bedtrap.development.systems.modules.combat.SelfTrap;

import bedtrap.development.events.event.TickEvent;
import bedtrap.development.ic.Setting;
import bedtrap.development.systems.modules.Module;
import bedtrap.development.systems.modules.combat.FeetTrap.FeetTrap;
import bedtrap.development.systems.utils.advanced.FindItemResult;
import bedtrap.development.systems.utils.advanced.InvUtils;
import com.google.common.eventbus.Subscribe;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

import static bedtrap.development.systems.modules.combat.SelfTrap.STUtils.*;

@Module.Info(name = "SelfTrap", category = Module.Category.Combat)
public class SelfTrap extends Module {

    public Setting<String> mode = register("Mode", List.of("Full", "Head", "Top"), "Top");
    public Setting<Integer> blockPerInterval = register("BlocksPerInterval", 2, 1, 5);
    public Setting<Integer> intervalDelay = register("IntervalDelay", 1, 0, 3);
    public Setting<Boolean> rotate = register("Rotate", false);
    public Setting<Boolean> autoDisable = register("AutoDisable", true);

    private int interval;

    @Override
    public void onActivate() {
        interval = 0;
    }

    @Subscribe
    public void onTick(TickEvent.Post event) {
        if (autoDisable.get() && ((mc.options.jumpKey.isPressed() || mc.player.input.jumping) || mc.player.prevY < mc.player.getPos().getY())) {
            toggle();
            return;
        }

        //if (!FeetTrap.get.getDisplayInfo().equals("0")) return;

        if (interval > 0) interval--;
        if (interval > 0) return;

        ArrayList<BlockPos> poses = getTrapBlocks(mode);
        setDisplayInfo(String.valueOf(poses.size()));
        FindItemResult block = InvUtils.findInHotbar(Items.OBSIDIAN);
        if (poses.isEmpty() || !block.found() || !isSurrounded()) return;



        for (int i = 0; i <= blockPerInterval.get(); i++) {
            if (poses.size() > i) {
                place(poses.get(i), rotate.get(),block.slot(), true);
            }
        }
        interval = intervalDelay.get();
    }
}
