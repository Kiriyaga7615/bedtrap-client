package bedtrap.development.systems.modules.combat.PistonCrystal;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.List;

import static bedtrap.development.systems.modules.combat.PistonCrystal.PCUtils.closestVec3d;
import static bedtrap.development.systems.modules.combat.PistonCrystal.PCUtils.distanceTo;

public class Triplet {
    public List<BlockPos> blockPos;
    public Direction direction;
    public boolean extended;

    public Triplet(List<BlockPos> blockPos, Direction direction) {
        this.blockPos = blockPos;
        this.direction = direction;
        this.extended = false;
    }

    public double distance() {
        return distanceTo(closestVec3d(blockPos.get(2)));
    }
}