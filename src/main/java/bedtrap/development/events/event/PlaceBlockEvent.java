package bedtrap.development.events.event;

import bedtrap.development.events.Cancelled;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

public class PlaceBlockEvent extends Cancelled {
    public BlockPos blockPos;
    public Block block;

    public PlaceBlockEvent(BlockPos blockPos, Block block) {
        this.blockPos = blockPos;
        this.block = block;
    }
}
