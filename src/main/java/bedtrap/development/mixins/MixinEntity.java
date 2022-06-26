package bedtrap.development.mixins;

import bedtrap.development.BedTrap;
import bedtrap.development.events.event.JumpVelocityMultiplierEvent;
import bedtrap.development.events.event.PlayerMoveEvent;
import bedtrap.development.systems.modules.movement.Velocity.Velocity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import static bedtrap.development.ic.util.Wrapper.mc;

@Mixin(Entity.class)
public abstract class MixinEntity{

    @Shadow
    public World world;

    @Shadow
    public abstract BlockPos getBlockPos();

    @Shadow
    protected abstract BlockPos getVelocityAffectingPos();

    @Inject(method = "move", at = @At("HEAD"))
    private void onMove(MovementType type, Vec3d movement, CallbackInfo info) {
        if ((Object) this != MinecraftClient.getInstance().player) return;

        PlayerMoveEvent event = new PlayerMoveEvent();
        event.type = type;
        event.movement = movement;
        BedTrap.EventBus.post(event);
    }


    @ModifyArgs(method = "pushAwayFrom(Lnet/minecraft/entity/Entity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;addVelocity(DDD)V"))
    private void onPushAwayFrom(Args args, Entity entity) {

        // Velocity
        if ((Object) this == mc.player && Velocity.get.isActive() && Velocity.get.push.get()) {
            args.set(0, (double) args.get(0) * 0);
            args.set(2, (double) args.get(2) * 0);
        }
    }

    @Inject(method = "getJumpVelocityMultiplier", at = @At("HEAD"), cancellable = true)
    private void onGetJumpVelocityMultiplier(CallbackInfoReturnable<Float> info) {
        if ((Object) this == MinecraftClient.getInstance().player) {
            float f = world.getBlockState(getBlockPos()).getBlock().getJumpVelocityMultiplier();
            float g = world.getBlockState(getVelocityAffectingPos()).getBlock().getJumpVelocityMultiplier();
            float a = f == 1.0D ? g : f;

            JumpVelocityMultiplierEvent event = new JumpVelocityMultiplierEvent();
            BedTrap.EventBus.post(event);

            info.setReturnValue(a * event.multiplier);
        }
    }
}
