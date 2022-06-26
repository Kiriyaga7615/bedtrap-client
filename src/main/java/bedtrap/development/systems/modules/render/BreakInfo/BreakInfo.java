package bedtrap.development.systems.modules.render.BreakInfo;

import bedtrap.development.events.event.Render3DEvent;
import bedtrap.development.ic.Setting;
import bedtrap.development.ic.util.Renderer3D;
import bedtrap.development.mixins.WorldRendererAccessor;
import bedtrap.development.systems.modules.Module;
import bedtrap.development.systems.modules.combat.AutoCrystal.AutoCrystal;
import bedtrap.development.systems.modules.other.HUD.HUD;
import com.google.common.eventbus.Subscribe;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.BlockBreakingInfo;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;

import java.util.Map;

@Module.Info(name = "BreakInfo", category = Module.Category.Render)
public class BreakInfo extends Module {
    public Setting<Boolean> render = register("Render", false);

    @Subscribe
    public void onRender(Render3DEvent event) {
        Map<Integer, BlockBreakingInfo> blocks = ((WorldRendererAccessor) mc.worldRenderer).getBlockBreakingInfos();

        blocks.values().forEach(info -> {
            BlockPos pos = info.getPos();
            int stage = info.getStage();
            info.getActorId();

            BlockState state = mc.world.getBlockState(pos);
            VoxelShape shape = state.getOutlineShape(mc.world, pos);
            if (shape.isEmpty()) return;

            double shrinkFactor = (9 - (stage + 1)) / 9d;
            double progress = 1d - shrinkFactor;

            boolean compatibility = AutoCrystal.instance.isActive() && AutoCrystal.instance.renderPos != null && AutoCrystal.instance.renderPos.equals(pos);

            Vec3d rPos = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            Renderer3D.drawText(Text.of(((double) Math.round(progress * 100) / 100) + "%"), rPos.x, compatibility ? rPos.y - 0.15 : rPos.y + 0.15, rPos.z, 1, false);
            Renderer3D.drawText(Text.of(getName (info.getActorId())), rPos.x, compatibility ? rPos.y - 0.30 : rPos.y - 0.15, rPos.z, 1, false);

            if (!render.get()) return;

            double max = ((double) Math.round(progress * 100) / 100);
            double min = 1 - max;

            Vec3d vec3d = Renderer3D.get.getRenderPosition(pos.getX(), pos.getY(), pos.getZ());
            Box box = new Box(vec3d.x + min, vec3d.y + min, vec3d.z + min, vec3d.x + max, vec3d.y + max, vec3d.z + max);
            Renderer3D.get.drawOutlineBox(event.getMatrixStack(), box, HUD.get.color());
        });
    }

    private String getName(int id) {
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player.getId() == id) return player.getGameProfile().getName();
        }

        return "Something went wrong...";
    }
}
