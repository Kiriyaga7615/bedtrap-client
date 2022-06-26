package bedtrap.development.systems.modules.combat.SelfWeb;

import bedtrap.development.events.event.Render3DEvent;
import bedtrap.development.events.event.TickEvent;
import bedtrap.development.ic.Friends;
import bedtrap.development.ic.Setting;
import bedtrap.development.ic.util.Renderer3D;
import bedtrap.development.ic.util.RotationUtil;
import bedtrap.development.systems.modules.Module;
import bedtrap.development.systems.utils.advanced.FindItemResult;
import bedtrap.development.systems.utils.advanced.InvUtils;
import bedtrap.development.systems.utils.other.TimerUtils;
import com.google.common.eventbus.Subscribe;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.List;

import static bedtrap.development.systems.modules.combat.SelfWeb.SWUtils.*;
import static bedtrap.development.systems.utils.advanced.InvUtils.findInHotbar;

@Module.Info(name = "SelfWeb", category = Module.Category.Combat)
public class SelfWeb extends Module {
    public Setting<String> mode = register("Mode", List.of("Smart", "Simple"), "Smart");
    public Setting<Double> triggerRange = register("TriggerRange", 3, 1, 6, 1);
    public Setting<String> doPlace = register("Place", List.of("Packet", "Client"), "Packet");
    public Setting<Boolean> holeOnly = register("HoleOnly", true);
    public Setting<Boolean> onGround = register("OnGround", true);
    public Setting<Boolean> rotate = register("Rotate", false);
    public Setting<Boolean> friends = register("Friends", true);
    public Setting<Double> placeRange = register("PlaceRange", 4.5, 1, 7, 1);
    public Setting<Boolean> swapBack = register("SwapBack", true);
    public Setting<Boolean> toggleOnJump = register("ToggleOnJump", true);
    public Setting<Boolean> render = register("Render", true);

    private final List<PlayerEntity> targets = new ArrayList<>();

    private FindItemResult cobweb;
    private BlockPos cobwebPos, renderPos;

    private final TimerUtils timer = new TimerUtils();

    @Override
    public void onActivate() {
        timer.reset();
    }

    @Subscribe
    public void onTick(TickEvent.Post event) {
        targets.addAll(getTargets());

        cobwebPos = mc.player.getBlockPos();
        cobweb = findInHotbar(Items.COBWEB);
        if (!cobweb.found()) {
            info(name, "Can't find cobweb, toggling...");
            toggle();
            return;
        }

        if (toggleOnJump.get() && ((mc.options.jumpKey.isPressed() || mc.player.input.jumping) || mc.player.prevY < mc.player.getPos().getY())) {
            toggle();
            return;
        }

        if (onGround.get() && !mc.player.isOnGround()) return;
        if (!isSurrounded(mc.player) && holeOnly.get()) return;
        switch (mode.get()) {
            case "Smart" -> targets.forEach(target -> {
                if (friends.get()) {
                    for (PlayerEntity friend : mc.world.getPlayers()) {
                        if (!Friends.isFriend(friend)) continue;
                        BlockPos cobwebPos = friend.getBlockPos();

                        if (distanceTo(closestVec3d(cobwebPos)) > placeRange.get()) continue;
                        if (target.distanceTo(friend) > triggerRange.get()) continue;

                        if (onGround.get() && !friend.isOnGround()) continue;
                        if (!isSurrounded(friend)) continue;

                        doPlace(cobweb, cobwebPos);
                    }
                }

                if (mc.player.distanceTo(target) > triggerRange.get()) return;

                doPlace(cobweb, cobwebPos);
            });
            case "Simple" -> doPlace(cobweb, cobwebPos);
        }
    }

    private void doPlace(FindItemResult itemResult, BlockPos blockPos) {
        if (!itemResult.found() || blockPos == null || !mc.world.isAir(blockPos)) return;
        Hand hand = itemResult.isOffhand() ? Hand.OFF_HAND : Hand.MAIN_HAND;
        BlockHitResult hitResult = new BlockHitResult(closestVec3d(blockPos), Direction.DOWN, blockPos, false);
        int prevSlot = InvUtils.inventory.selectedSlot;

        if (rotate.get()) RotationUtil.rotate(blockPos);

        InvUtils.swap(itemResult);
        if (doPlace.get("Packet")) mc.getNetworkHandler().sendPacket(new PlayerInteractBlockC2SPacket(hand, hitResult));
        else mc.interactionManager.interactBlock(mc.player, mc.world, hand, hitResult);
        if (swapBack.get()) InvUtils.swap(prevSlot);
    }

    private List<PlayerEntity> getTargets() {
        targets.clear();

        for (PlayerEntity player : mc.world.getPlayers()) {
            if (mc.player.distanceTo(player) > 15) continue;
            if (Friends.isFriend(player)) continue;
            if (mc.player == player) continue;
            if (player.isDead()) continue;

            targets.add(player);
        }

        return targets;
    }

    private void setRenderPos(BlockPos blockPos) {
        if (!mc.world.isAir(blockPos)) return;

        renderPos = blockPos;
    }

    @Subscribe
    public void onRender(Render3DEvent event) {
        if (!render.get()) return;

        Renderer3D.get.drawBoxWithOutline(event.getMatrixStack(), renderPos);
    }
}
