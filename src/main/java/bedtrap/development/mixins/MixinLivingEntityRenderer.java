package bedtrap.development.mixins;

import bedtrap.development.systems.modules.other.Rotations.Rotations;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import static bedtrap.development.ic.util.Wrapper.mc;

@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> {
    @Shadow
    @Nullable
    protected abstract RenderLayer getRenderLayer(T entity, boolean showBody, boolean translucent, boolean showOutline);

    //3rd Person Rotation

    @ModifyVariable(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", ordinal = 2, at = @At(value = "STORE", ordinal = 0))
    public float changeYaw(float oldValue, LivingEntity entity) {
        if (Rotations.instance.thirdPerson.get() && entity.equals(mc.player) && Rotations.instance.rotationTimer < 10) return Rotations.instance.serverYaw;
        return oldValue;
    }

    @ModifyVariable(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", ordinal = 3, at = @At(value = "STORE", ordinal = 0))
    public float changeHeadYaw(float oldValue, LivingEntity entity) {
        if (Rotations.instance.thirdPerson.get() && entity.equals(mc.player) && Rotations.instance.rotationTimer < 10) return Rotations.instance.serverYaw;
        return oldValue;
    }

    @ModifyVariable(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", ordinal = 5, at = @At(value = "STORE", ordinal = 3))
    public float changePitch(float oldValue, LivingEntity entity) {
        if (Rotations.instance.thirdPerson.get() && entity.equals(mc.player) && Rotations.instance.rotationTimer < 10) return Rotations.instance.serverPitch;
        return oldValue;
    }
}
