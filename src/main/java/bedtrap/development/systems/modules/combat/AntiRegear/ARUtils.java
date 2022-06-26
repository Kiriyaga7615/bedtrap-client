package bedtrap.development.systems.modules.combat.AntiRegear;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

public class ARUtils extends AntiRegear{
    public static List<BlockPos> getSphere(BlockPos centerPos, double radius, double height) {
        ArrayList<BlockPos> blocks = new ArrayList<>();

        for (int i = centerPos.getX() - (int) radius; i < centerPos.getX() + radius; i++) {
            for (int j = centerPos.getY() - (int) height; j < centerPos.getY() + height; j++) {
                for (int k = centerPos.getZ() - (int) radius; k < centerPos.getZ() + radius; k++) {
                    BlockPos pos = new BlockPos(i, j, k);
                    if (distanceBetween(centerPos, pos) <= radius && !blocks.contains(pos)) blocks.add(pos);
                }
            }
        }

        return blocks;
    }

    public static double distanceBetween(BlockPos blockPos1, BlockPos blockPos2) {
        double d = blockPos1.getX() - blockPos2.getX();
        double e = blockPos1.getY() - blockPos2.getY();
        double f = blockPos1.getZ() - blockPos2.getZ();
        return MathHelper.sqrt((float) (d * d + e * e + f * f));
    }
}
