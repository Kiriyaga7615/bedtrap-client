package bedtrap.development.mixins;

import bedtrap.development.BedTrap;
import bedtrap.development.events.event.EntityMoveEvent;
import bedtrap.development.events.event.SendMovementPacketsEvent;
import bedtrap.development.events.event.SetPlayerSprintEvent;
import bedtrap.development.systems.modules.movement.NoSlow.NoSlow;
import bedtrap.development.systems.modules.movement.Velocity.Velocity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

import static bedtrap.development.ic.util.Wrapper.mc;

@Mixin(ClientPlayerEntity.class)
public abstract class MixinClientPlayerEntity {

    @Inject(method = "move", at = @At("HEAD"), cancellable = true)
    public void move(MovementType holeType, Vec3d movement, CallbackInfo ci) {
        if (holeType == MovementType.PLAYER && holeType == MovementType.SELF) {
            EntityMoveEvent event = new EntityMoveEvent(movement.x, movement.y, movement.z);
            BedTrap.EventBus.post(event);
        }
    }

    @Inject(method = "pushOutOfBlocks", at = @At("HEAD"), cancellable = true)
    private void onPushOutOfBlocks(double x, double d, CallbackInfo info) {
        if (Velocity.get.isActive() && Velocity.get.push.get()) {
            info.cancel();
        }
    }

    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"))
    private boolean redirectUsingItem(ClientPlayerEntity player) {
        if (NoSlow.get.isActive()) return false;
        return player.isUsingItem();
    }

/*    // TODO: 18.05.2022 дать пизды тому кто это сделал
    @Redirect(method = "sendMovementPackets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isSneaking()Z"))
    private boolean isSneaking(ClientPlayerEntity clientPlayerEntity) {
        return NoSlow.get.airStrict.get() || clientPlayerEntity.isSneaking();
    }*/

    @Inject(method = "setSprinting", at = @At("HEAD"), cancellable = true)
    public void onSetSprint(boolean sprinting, CallbackInfo ci) {
        SetPlayerSprintEvent event = new SetPlayerSprintEvent(sprinting);
        BedTrap.EventBus.post(event);

        if(event.isCancelled()) {
            event.setSprinting(event.isSprinting());
            ci.cancel();
        }
    }

    @Inject(method = "sendMovementPackets", at = @At("HEAD"))
    private void onSendMovementPacketsHead(CallbackInfo info) {
        SendMovementPacketsEvent.Pre event = new SendMovementPacketsEvent.Pre();
        BedTrap.EventBus.post(event);
    }

    @Inject(method = "sendMovementPackets", at = @At("TAIL"))
    private void onSendMovementPacketsTail(CallbackInfo info) {
        SendMovementPacketsEvent.Post event = new SendMovementPacketsEvent.Post();
        BedTrap.EventBus.post(event);    }
}
