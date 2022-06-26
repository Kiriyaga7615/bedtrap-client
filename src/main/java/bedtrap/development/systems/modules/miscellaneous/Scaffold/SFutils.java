package bedtrap.development.systems.modules.miscellaneous.Scaffold;

import bedtrap.development.systems.utils.advanced.FindItemResult;
import bedtrap.development.systems.utils.advanced.InvUtils;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import static bedtrap.development.ic.util.Wrapper.mc;

public class SFutils {
    public static void place(BlockPos blockPos, FindItemResult findItemResult) {
        if (canPlace(blockPos)) {
            int prevSlot = mc.player.getInventory().selectedSlot;
            InvUtils.swap(findItemResult.slot());
            mc.interactionManager.interactBlock(mc.player, mc.world, findItemResult.getHand(), new BlockHitResult(new Vec3d(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5), Direction.UP, blockPos, false));
            mc.getNetworkHandler().sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
            InvUtils.swap(prevSlot);
        }
    }

    public static boolean canPlace(BlockPos blockPos) {
        return mc.world.getBlockState(blockPos).isAir();
    }

    public static boolean onStand() {
        return  mc.player.sidewaysSpeed == 0 && mc.player.forwardSpeed == 0;
    }

    public static boolean onDescent() {
        return (mc.player.input.sneaking && mc.options.sneakKey.isPressed());
    }
}
