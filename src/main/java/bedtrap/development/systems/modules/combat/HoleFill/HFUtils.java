package bedtrap.development.systems.modules.combat.HoleFill;

import bedtrap.development.ic.util.RotationUtil;
import bedtrap.development.systems.utils.advanced.InvUtils;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;

import static bedtrap.development.ic.util.Wrapper.mc;

public class HFUtils {
    static double distanceTo(BlockPos blockPos1, BlockPos blockPos2) {
        double d = blockPos1.getX() - blockPos2.getX();
        double e = blockPos1.getY() - blockPos2.getY();
        double f = blockPos1.getZ() - blockPos2.getZ();
        return MathHelper.sqrt((float) (d * d + e * e + f * f));
    }

    static void place(BlockPos pos, boolean rotate, int slot) {
        if (pos != null) {
            int prevSlot = mc.player.getInventory().selectedSlot;
            InvUtils.swap(slot);
            if (rotate) RotationUtil.rotate(pos);
            BlockHitResult result = new BlockHitResult(new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5), Direction.DOWN, pos, true);
            mc.getNetworkHandler().sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, result));
            mc.player.swingHand(Hand.MAIN_HAND);
            InvUtils.swap(prevSlot);
        }
    }
}
