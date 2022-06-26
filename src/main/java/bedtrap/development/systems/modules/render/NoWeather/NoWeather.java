package bedtrap.development.systems.modules.render.NoWeather;

import bedtrap.development.events.event.ParticleEvent;
import bedtrap.development.events.event.TickEvent;
import bedtrap.development.ic.Setting;
import bedtrap.development.systems.modules.Module;
import com.google.common.eventbus.Subscribe;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;


@Module.Info(name = "NoWeather", category = Module.Category.Render)
public class NoWeather extends Module {

    public Setting<Boolean> noSounds = register("NoSounds", true);
    public static NoWeather get;

    public NoWeather() {
        get = this;
    }

    @Subscribe
    public void onTick(TickEvent.Pre event) {
        if (mc.world.isThundering()) setDisplayInfo("Thunder");
        else if (mc.world.isRaining()) setDisplayInfo("Rain");
        else setDisplayInfo("Clear");

        if (noSounds.get()) mc.options.setSoundVolume(SoundCategory.WEATHER, 0);
    }

    @Subscribe
    private void onAddParticle(ParticleEvent event) {
        if (event.particle.getType() == ParticleTypes.RAIN) event.cancel();
    }


}