package bedtrap.development.systems.modules.miscellaneous.AntiPiston;

import bedtrap.development.events.event.BlockUpdateEvent;
import bedtrap.development.events.event.TickEvent;
import bedtrap.development.ic.Setting;
import bedtrap.development.systems.modules.Module;
import bedtrap.development.systems.modules.combat.PistonCrystal.PistonCrystal;
import bedtrap.development.systems.utils.advanced.FindItemResult;
import bedtrap.development.systems.utils.advanced.InvUtils;
import com.google.common.eventbus.Subscribe;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

@Module.Info(name = "AntiPiston", category = Module.Category.Miscellaneous)
public class AntiPiston extends Module {
    public Setting<Boolean> push = register("Push", false);

    private BlockPos blockPos;

    @Override
    public void onActivate() {
        blockPos = null;
    }

    @Subscribe
    public void onBlock(BlockUpdateEvent event) {
        if (!(event.newState.getBlock() instanceof PistonBlock)) return;
        if (PistonCrystal.instance.shouldReturn(event.pos)) return;
        Box piston = new Box(event.pos);

        for (Box box : getBoxes()) {
            if (!piston.intersects(box)) continue;

            blockPos = event.pos;
            doBreak();
            break;
        }
    }

    @Subscribe
    public void onTick(TickEvent.Post event) {
        if (blockPos == null) return;
        if (!mc.world.isAir(blockPos)) return;

        InvUtils.syncSlots();
        blockPos = null;
    }

    private void doBreak() {
        if (blockPos == null) return;
        BlockState blockState = mc.world.getBlockState(blockPos);

        mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, blockPos, Direction.DOWN));
        mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, blockPos, Direction.DOWN));
        InvUtils.swap(InvUtils.findFastestTool(blockState));
        mc.getNetworkHandler().sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
        InvUtils.swapBack();
    }

    private List<Box> getBoxes() {
        List<Box> boxes = new ArrayList<>();

        for (int height = 1; height <= 2; height++) {
            BlockPos blockPos = mc.player.getBlockPos().up(height);

            for (Direction direction : Direction.values()) {
                if (direction == Direction.UP || direction == Direction.DOWN) continue;
                BlockPos crystalPos = blockPos.offset(direction);

                boolean canPlace = mc.world.isAir(crystalPos) &&
                        (mc.world.getBlockState(crystalPos.down()).isOf(Blocks.OBSIDIAN) || mc.world.getBlockState(crystalPos.down()).isOf(Blocks.BEDROCK));

                if (!canPlace) continue;
                Vec3d vec3d = new Vec3d(crystalPos.getX(), crystalPos.getY(), crystalPos.getZ());
                Box endCrystal = new net.minecraft.util.math.Box(vec3d.x - 0.5, vec3d.y, vec3d.z - 0.5, vec3d.x + 1.5, vec3d.y + 2, vec3d.z + 1.5);

                boxes.add(endCrystal);
            }
        }

        return boxes;
    }
}
