package bedtrap.development.mixins;

import bedtrap.development.BedTrap;
import bedtrap.development.events.event.StartBreakingBlockEvent;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static bedtrap.development.ic.util.Wrapper.mc;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class MixinClientPlayerInteractionManager {
    @Inject(method = "attackBlock", at = @At("HEAD"), cancellable = true)
    private void onAttackBlock(BlockPos blockPos, Direction direction, CallbackInfoReturnable<Boolean> info) {
        if (mc.world == null && mc.player == null) return;

        BedTrap.EventBus.post(new StartBreakingBlockEvent(blockPos, direction));
    }
}
