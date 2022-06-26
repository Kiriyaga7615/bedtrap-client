package bedtrap.development.mixins;

import bedtrap.development.systems.modules.Modules;
import bedtrap.development.BedTrap;
import bedtrap.development.events.event.KeyEvent;
import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class MixinKeyboard {
    @Inject(method = "onKey", at = @At(value = "INVOKE", target = "net/minecraft/client/util/InputUtil.isKeyPressed(JI)Z", ordinal = 5), cancellable = true)
    private void onKeyEvent(long windowPointer, int key, int scanCode, int action, int modifiers, CallbackInfo callbackInfo) {
        KeyEvent event = new KeyEvent(key);
        BedTrap.EventBus.post(event);
        Modules.getModules().forEach(module -> {
            if (!module.isBounded()) return;
            if (module.getBind() == key) module.toggle();
        });
    }
}
