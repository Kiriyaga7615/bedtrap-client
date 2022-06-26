package bedtrap.development.systems.modules.movement.LongJump;

import bedtrap.development.events.event.TickEvent;
import bedtrap.development.ic.Setting;
import bedtrap.development.systems.modules.Module;
import com.google.common.eventbus.Subscribe;
import net.minecraft.util.math.MathHelper;


@Module.Info(name = "LongJump", category = Module.Category.Movement)
public class LongJump extends Module {
    public Setting<Double> speed = register("Factor", 1F, 0.1F, 3F, 1);

    @Subscribe
    public void onTick(TickEvent.Post event) {
        if (mc.player == null) return;
        //преобразование градусов в радианы методом toRadians,тк yaw  в градусах
        float yaw = (float) Math.toRadians(mc.player.getYaw());
        //коэффицент плавности который я методом тыка подобрал, и решил для красоты дабл у сделать отдельным
        double vSpeed = speed.get() / 5;
        //cам буст во время прыжка
        if (!mc.player.isOnGround()) {
            //ну думаю тут понятно как и что, синус от радиан яу помноженный на плавный коээфицент скорости (х), тоже самое и с z, только косинус
            mc.player.addVelocity(-MathHelper.sin(yaw) * vSpeed, 0.0F, MathHelper.cos(yaw) * vSpeed);
        } else if (mc.player.isOnGround()) {
            mc.player.setVelocity(0, 0, 0);
        }
        toggle();
    }
}
// ажаж или еврей не трогайте код если не кринж, я сам тут всё сделаю и наведу порядок, на мпвп робит фактор от 1.5 до 2.5 норм