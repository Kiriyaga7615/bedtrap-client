package bedtrap.development.systems.modules.miscellaneous.BindClick;

import bedtrap.development.events.event.TickEvent;
import bedtrap.development.ic.Setting;
import bedtrap.development.systems.modules.Module;
import com.google.common.eventbus.Subscribe;
import bedtrap.development.systems.utils.advanced.FindItemResult;
import bedtrap.development.systems.utils.advanced.InvUtils;

import net.minecraft.item.Items;
import net.minecraft.util.Hand;


import java.util.List;

@Module.Info(name = "BindClick", category = Module.Category.Miscellaneous)
public class BindClick extends Module {
    public Setting<String> mode = register("Mode", List.of("Pearl", "Rocket"), "Pearl");

    private FindItemResult item;

    @Subscribe
    private void onTick(TickEvent.Pre event) {
        switch (mode.get()) {
            case "Pearl" -> item = InvUtils.findInHotbar(Items.ENDER_PEARL);
            case "Rocket" -> item = InvUtils.findInHotbar(Items.FIREWORK_ROCKET);
        }

        if (item.found()) {
            InvUtils.swap(item.slot(), true);
            mc.interactionManager.interactItem(mc.player, mc.world, Hand.MAIN_HAND);
            InvUtils.swapBack();
        }

        toggle();
    }
}
