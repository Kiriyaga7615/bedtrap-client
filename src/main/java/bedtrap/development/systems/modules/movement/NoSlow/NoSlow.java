package bedtrap.development.systems.modules.movement.NoSlow;

import bedtrap.development.ic.Setting;
import bedtrap.development.systems.modules.Module;

@Module.Info(name = "NoSlow", category = Module.Category.Movement)
public class NoSlow extends Module {

    public static NoSlow get;
    public Setting<Boolean> airStrict = register("AirStrict", true);

    public NoSlow() {
        get = this;
    }

}
