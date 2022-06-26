package bedtrap.development.systems.modules.combat.HoleFill;

import bedtrap.development.events.event.TickEvent;
import bedtrap.development.ic.Setting;
import bedtrap.development.systems.modules.Module;
import bedtrap.development.systems.utils.advanced.FindItemResult;
import bedtrap.development.systems.utils.advanced.InvUtils;
import com.google.common.eventbus.Subscribe;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.List;

import static bedtrap.development.ic.Friends.isFriend;
import static bedtrap.development.systems.modules.combat.HoleFill.HFUtils.*;


@Module.Info(name = "HoleFill", category = Module.Category.Combat)
public class HoleFill extends Module {

    public Setting<Integer> blockPerInterval = register("BlocksPerInterval", 2, 1, 5);
    public Setting<Integer> intervalDelay = register("IntervalDelay", 1, 0, 3);
    public Setting<Boolean> rotate = register("Rotate", false);
    public Setting<Double> placeRange = register("PlaceRange", 4.5, 2, 7, 1);
    public Setting<Double> targetRange = register("TargetRange", 7, 5, 12, 1);
    public Setting<Integer> radius = register("Radius", 3, 1, 6); // vertical = 6
    public Setting<Boolean> self = register("Self", true);

    private ArrayList<BlockPos> blocks = new ArrayList<>();
    private ArrayList<PlayerEntity> targets = new ArrayList<>();
    private int interval;

    @Override
    public void onActivate() {
        blocks.clear();
        targets.clear();
        interval = 0;
    }

    @Subscribe
    public void onTick(TickEvent.Pre event) {
        findTargets();
        if (targets.isEmpty()) return;
        blocks.clear();

        for (PlayerEntity target : targets) {
            BlockPos centerPos = target.getBlockPos();

            for (int i = centerPos.getX() - radius.get(); i < centerPos.getX() + radius.get(); i++) {
                for (int j = centerPos.getY() - 6; j < centerPos.getY(); j++) {
                    for (int k = centerPos.getZ() - radius.get(); k < centerPos.getZ() + radius.get(); k++) {
                        BlockPos pos = new BlockPos(i, j, k);

                        if (!allowed(pos, target, self.get())) continue;

                        int count = 0;

                        for (Direction direction : Direction.values()) {
                            if (direction == Direction.UP || direction == Direction.DOWN) continue;

                            BlockState state = mc.world.getBlockState(pos.offset(direction));

                            if (state.getBlock() == Blocks.BEDROCK || state.getBlock() == Blocks.OBSIDIAN) count++;
                        }
                        if (count != 4) continue;

                        if (self.get() && mc.player.getBlockPos().equals(pos))
                            blocks.add(pos.up(2));
                        else
                            blocks.add(pos);
                    }
                }
            }
        }
        setDisplayInfo(String.valueOf(blocks.size()));
    }

    @Subscribe
    private void onPostTick(TickEvent.Post event) {
        if (targets.isEmpty() || blocks.isEmpty()) return;
        if (interval > 0) interval--;
        if (interval > 0) return;

        FindItemResult block = InvUtils.findInHotbar(Items.OBSIDIAN);

        for (int i = 0; i <= blockPerInterval.get(); i++) {
            if (blocks.size() > i) {
                place(blocks.get(i), rotate.get(), block.slot());
            }
        }
        interval = intervalDelay.get();
    }

    private boolean allowed(BlockPos pos, PlayerEntity target, boolean self) {
        if (!mc.world.isAir(pos)) return false;
        if (distanceTo(mc.player.getBlockPos(), pos) > placeRange.get()) return false;
        if (target.getBlockPos().equals(pos)) return false;
        if (!self && mc.player.getBlockPos().equals(pos)) return false;
        for (int h = 0; h < 4; h++) {
            if (!mc.world.getBlockState(pos.up(h)).isAir()) return false;
        }
        return !mc.world.isAir(pos.down());
    }

    private void findTargets() {
        targets.clear();
        for (PlayerEntity e : mc.world.getPlayers()) {
            if (e.isCreative() || e == mc.player) continue;

            if (!e.isDead() && e.isAlive() && !isFriend(e) && e.distanceTo(mc.player) <= targetRange.get()) {
                targets.add(e);
            }
        }
    }
}