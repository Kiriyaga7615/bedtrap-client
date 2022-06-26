package bedtrap.development.systems.modules.render.Chams;

import bedtrap.development.ic.Setting;
import bedtrap.development.systems.modules.Module;
import net.minecraft.client.render.Frustum;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.entity.player.PlayerEntity;

@Module.Info(name = "Chams", category = Module.Category.Render)
public class Chams extends Module {
    public Setting<Boolean> players = register("Players", true);
    public Setting<Boolean> crystals = register("Crystals", false);

    public boolean shouldRender(Entity entity) {
        if (!isActive()) return false;
        if (entity == null) return false;
        if (entity instanceof StriderEntity) return false;
        if (entity instanceof EndCrystalEntity) return crystals.get();
        if (entity instanceof PlayerEntity && entity != mc.player) return players.get();

        return false;
    }

    public static Chams get;

    public Chams() {
        get = this;
    }
}
