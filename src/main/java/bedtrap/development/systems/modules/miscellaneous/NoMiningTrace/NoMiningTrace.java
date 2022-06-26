package bedtrap.development.systems.modules.miscellaneous.NoMiningTrace;

import bedtrap.development.ic.Setting;
import bedtrap.development.systems.modules.Module;
import net.minecraft.item.PickaxeItem;

@Module.Info(name = "NoMiningTrace", category = Module.Category.Miscellaneous)
public class NoMiningTrace extends Module {
    public Setting<Boolean> onlyPickaxe = register("OnlyPickaxe", true);

    public boolean canWork() {
        if (!isActive()) return false;

        if (onlyPickaxe.get()) return mc.player.getMainHandStack().getItem() instanceof PickaxeItem;
        return true;
    }

    public static NoMiningTrace instance;

    public NoMiningTrace() {
        instance = this;
    }
}
