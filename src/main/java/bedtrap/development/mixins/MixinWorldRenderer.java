package bedtrap.development.mixins;

import bedtrap.development.BedTrap;
import bedtrap.development.events.event.Render3DEvent;
import bedtrap.development.systems.modules.render.Chams.Chams;
import bedtrap.development.systems.modules.render.NoWeather.NoWeather;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(WorldRenderer.class)
public abstract class MixinWorldRenderer {
    @Inject(method = "renderWeather", at = @At("HEAD"), cancellable = true)
    private void onRenderWeather(LightmapTextureManager manager, float f, double d, double e, double g, CallbackInfo info) {
        if (NoWeather.get.isActive()) info.cancel();
    }
}