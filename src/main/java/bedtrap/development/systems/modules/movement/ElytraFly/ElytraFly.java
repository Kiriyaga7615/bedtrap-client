package bedtrap.development.systems.modules.movement.ElytraFly;

import bedtrap.development.events.event.TickEvent;
import bedtrap.development.ic.Setting;
import bedtrap.development.systems.modules.Module;
import com.google.common.eventbus.Subscribe;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdatePlayerAbilitiesC2SPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.List;

@Module.Info(name = "ElytraFly", category = Module.Category.Movement)
public class ElytraFly extends Module {

    public static ElytraFly get;

    public Setting<String> mode = register("Mode", List.of("Boost", "Negrium"), "Boost");
    public Setting<Boolean> autoFly = register("AutoFly", false);
    public Setting<Boolean> takeOff = register("TakeOff", true);
    public Setting<Double> speed = register("Speed", 0.2D, 0.1, 4, 2);
    public Setting<Boolean> flatFly = register("FlayFly", true);
    public Setting<Boolean> upEly = register("upLift", true);

    public ElytraFly() {
        get = this;
    }

    @Subscribe
    public void onTick(TickEvent.Post event) {
        // Module info
        setDisplayInfo(String.valueOf(mc.player.getYaw()));

        if (!mc.player.isFallFlying()) {
            if (takeOff.get() && !mc.player.isOnGround() && mc.options.jumpKey.isPressed()) {
                info("Take Off triggered.");
                mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
            }
            return;
        }

        if (autoFly.get() && mc.player.isFallFlying()) {
            mc.options.forwardKey.setPressed(true);
        }

        if (mode.get("Boost")) {
            if (mc.player.getAbilities().flying) mc.player.getAbilities().flying = false;

            float yaw = (float) Math.toRadians(mc.player.getYaw());
            if (mc.options.forwardKey.isPressed()) {
                mc.player.addVelocity(-MathHelper.sin(yaw) * speed.get() / 10, 0, MathHelper.cos(yaw) * speed.get() / 10);
            }
        } else {
            Vec3d vec = new Vec3d(0, 0, speed.get())
                    .rotateX(flatFly.get() ? 0.02f : -(float) Math.toRadians(mc.player.getPitch()))
                    .rotateY(-(float) Math.toRadians(mc.player.getYaw()));

            if (mc.player.isFallFlying()) {
                mc.getNetworkHandler().sendPacket(new UpdatePlayerAbilitiesC2SPacket(new PlayerAbilities()));
                if (upEly.get() && mc.player.getPitch() < 0.f)
                    return;

                if (mc.options.backKey.isPressed()) vec = vec.multiply(-1);
                else if (mc.options.leftKey.isPressed()) vec = vec.rotateY((float) Math.toRadians(90));
                else if (mc.options.rightKey.isPressed()) vec = vec.rotateY(-(float) Math.toRadians(90));
                else if (mc.options.jumpKey.isPressed())
                    vec = new Vec3d(0, speed.get(), 0);
                else if (mc.options.sneakKey.isPressed())
                    vec = new Vec3d(0, -speed.get(), 0);
                else if (!mc.options.forwardKey.isPressed()) vec = Vec3d.ZERO;
                mc.player.setVelocity(vec);
            }
        }
    }
}