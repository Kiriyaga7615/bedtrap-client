package bedtrap.development.systems.modules.combat.FeetTrap;

import bedtrap.development.events.event.Render3DEvent;
import bedtrap.development.events.event.TickEvent;
import bedtrap.development.ic.Setting;
import bedtrap.development.ic.util.Renderer3D;
import bedtrap.development.mixins.ClientPlayerInteractionManagerAccessor;
import bedtrap.development.mixins.WorldRendererAccessor;
import bedtrap.development.systems.modules.Module;
import bedtrap.development.systems.utils.advanced.FindItemResult;
import bedtrap.development.systems.utils.advanced.InvUtils;
import bedtrap.development.systems.utils.other.TimerUtils;
import com.google.common.eventbus.Subscribe;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.render.BlockBreakingInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static bedtrap.development.systems.modules.combat.FeetTrap.FTUtils.*;

@Module.Info(name = "FeetTrap", category = Module.Category.Combat)
public class FeetTrap extends Module {

    public static FeetTrap get;
    public Setting<Integer> blockPerInterval = register("BlocksPerInterval", 3, 1, 5);
    public Setting<Integer> intervalDelay = register("IntervalDelay", 1, 0, 3);
    public Setting<Boolean> antiBreak = register("AntiBreak", false);
    public Setting<Boolean> rotate = register("Rotate", false);
    public Setting<Double> collisionPassed = register("CollisionPassed", 1500, 1250, 5000, 0);
    public Setting<Boolean> clientRemove = register("ClientRemove(test)", false);
    public Setting<Boolean> autoDisable = register("AutoDisable", true);

    public FeetTrap() {
        get = this;
    }

    private List<BlockPos> poses = new ArrayList<>();
    private final List<BlockPos> queue = new ArrayList<>();

    private final TimerUtils unsurroundedTimer = new TimerUtils();
    private final TimerUtils oneTickTimer = new TimerUtils();

    private int interval;

    @Override
    public void onActivate() {
        queue.clear();

        interval = 0;
        unsurroundedTimer.reset();
        oneTickTimer.reset();
    }

    public ArrayList<BlockPos> getPositions(PlayerEntity player) {
        ArrayList<BlockPos> positions = new ArrayList<>();
        List<Entity> getEntityBoxes;

        for (BlockPos blockPos : getSphere(player.getBlockPos(), 3, 1)) {
            if (!mc.world.getBlockState(blockPos).getMaterial().isReplaceable()) continue;
            getEntityBoxes = mc.world.getOtherEntities(null, new Box(blockPos), entity -> entity == player);
            if (!getEntityBoxes.isEmpty()) continue;
            if (unsurroundedTimer.passedMillis(collisionPassed.get().longValue()) && !mc.world.canPlace(Blocks.OBSIDIAN.getDefaultState(), blockPos, ShapeContext.absent()))
                continue;


            for (Direction direction : Direction.values()) {
                if (direction == Direction.UP || direction == Direction.DOWN) continue;

                getEntityBoxes = mc.world.getOtherEntities(null, new Box(blockPos.offset(direction)), entity -> entity == player);
                if (!getEntityBoxes.isEmpty()) positions.add(blockPos);
            }
        }
        return positions;
    }

    @Subscribe
    public void onTick(TickEvent.Post event) {
        if (autoDisable.get() && ((mc.options.jumpKey.isPressed() || mc.player.input.jumping) || mc.player.prevY < mc.player.getPos().getY())) {
            toggle();
            return;
        }

        if (interval > 0) interval--;
        if (interval > 0) return;

        Map<Integer, BlockBreakingInfo> blocks = ((WorldRendererAccessor) mc.worldRenderer).getBlockBreakingInfos();
        BlockPos ownBreakingPos = ((ClientPlayerInteractionManagerAccessor) mc.interactionManager).getCurrentBreakingBlockPos();
        ArrayList<BlockPos> boobies = getSurroundBlocks(mc.player);

        blocks.values().forEach(info -> {
            BlockPos pos = info.getPos();
            if (antiBreak.get() && !pos.equals(ownBreakingPos) && info.getStage() >= 0) {
                if (boobies.contains(pos)) queue.addAll(getBlocksAround(pos));
            }
            if (clientRemove.get() && !pos.equals(ownBreakingPos) && info.getStage() >= 8) {
                if (boobies.contains(pos)) mc.world.setBlockState(pos,Blocks.AIR.getDefaultState());
            }
        });


        poses = getPositions(mc.player);
        poses.addAll(queue);

        setDisplayInfo(String.valueOf(poses.size()));
        FindItemResult block = InvUtils.findInHotbar(Items.OBSIDIAN);
        if (poses.isEmpty() || !block.found()) {
            if (isSurrounded(mc.player)) unsurroundedTimer.reset();
            return;
        }

        for (int i = 0; i <= blockPerInterval.get(); i++) {
            if (poses.size() > i) {
                place(poses.get(i), rotate.get(),block.slot(), true);
                queue.remove(poses.get(i));
            }
        }
        interval = intervalDelay.get();
    }

    private List<BlockPos> getBlocksAround(BlockPos blockPos) {
        List<BlockPos> blocks = new ArrayList<>();

        for (Direction direction : Direction.values()) {
            if (direction == Direction.UP || direction == Direction.DOWN) continue;
            if (hasEntity(new Box(blockPos.offset(direction)))) continue;
            if (!mc.world.isAir(blockPos.offset(direction))) continue;
            if (queue.contains(blockPos.offset(direction))) continue;

            blocks.add(blockPos.offset(direction));
        }

        return blocks;
    }

    @Subscribe
    public void onRender(Render3DEvent event) {
        if (poses.isEmpty()) return;

        poses.forEach(blockPos -> {
            Renderer3D.get.drawBoxWithOutline(event.getMatrixStack(), blockPos);
        });
    }
}
