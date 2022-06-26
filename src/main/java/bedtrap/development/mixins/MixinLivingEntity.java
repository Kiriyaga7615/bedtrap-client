package bedtrap.development.mixins;

import bedtrap.development.systems.modules.render.ViewModel.ViewModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import static bedtrap.development.ic.util.Wrapper.mc;

@Mixin(LivingEntity.class)
public class MixinLivingEntity {

    private final LivingEntity entity = ((LivingEntity) (Object) this);

    @ModifyArg(method = "swingHand(Lnet/minecraft/util/Hand;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;swingHand(Lnet/minecraft/util/Hand;Z)V"))
    private Hand setHand(Hand hand) {
        if ((Object) this == mc.player && ViewModel.get.isActive()) {
            if (ViewModel.get.swingMode.get("None")) return hand;
            return ViewModel.get.swingMode.get("OffHand") ? Hand.OFF_HAND : Hand.MAIN_HAND;
        }

        return hand;
    }
}
