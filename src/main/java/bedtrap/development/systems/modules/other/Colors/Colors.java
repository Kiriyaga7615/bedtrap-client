package bedtrap.development.systems.modules.other.Colors;

import bedtrap.development.BedTrap;
import bedtrap.development.ic.Setting;
import bedtrap.development.systems.modules.Module;

@Module.Info(name = "Colors", category = Module.Category.Other, bind = 344, drawn = false)
public class Colors extends Module {

    public static Colors get;
    public Setting<Double> scale = register("Scale", 1, 0.5, 1.5, 2);
    public Setting<Double> r = register("Red", 215, 0, 255, 0);
    public Setting<Double> g = register("Green", 0, 0, 255, 0);
    public Setting<Double> b = register("Blue", 0, 0, 255, 0);

    public Colors() {
        get = this;
    }

    public static float s;

    @Override
    public void onActivate() {
        s = scale.get().floatValue();
        if (cantUpdate()) return;
        mc.setScreen(BedTrap.getClickGui());
    }

    @Override
    public void onDeactivate() {
        if (cantUpdate()) return;
        mc.setScreen(null);
    }
}
