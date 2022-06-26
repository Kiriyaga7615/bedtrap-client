package bedtrap.development.mixins;

import bedtrap.development.BedTrap;
import bedtrap.development.events.event.EntityAddedEvent;
import bedtrap.development.events.event.EntityRemovedEvent;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

import static bedtrap.development.ic.util.Wrapper.mc;

@Mixin(ClientWorld.class)
public abstract class MixinClientWorld {
    @Shadow
    @Nullable
    public abstract Entity getEntityById(int id);

    @Inject(method = "addEntityPrivate", at = @At("TAIL"))
    private void onSpawnEntityPrivate(int id, Entity entity, CallbackInfo info) {
        if (entity != null) BedTrap.EventBus.post(new EntityAddedEvent(entity));
    }

    @Inject(method = "removeEntity", at = @At("HEAD"))
    private void onRemoveEntity(int entityId, Entity.RemovalReason removalReason, CallbackInfo info) {
        if (mc.world != null && mc.player != null && getEntityById(entityId) != null) BedTrap.EventBus.post(new EntityRemovedEvent(getEntityById(entityId)));
    }
}
