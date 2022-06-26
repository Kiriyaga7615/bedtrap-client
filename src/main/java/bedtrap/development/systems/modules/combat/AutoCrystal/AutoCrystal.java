package bedtrap.development.systems.modules.combat.AutoCrystal;

import bedtrap.development.events.event.*;
import bedtrap.development.ic.Friends;
import bedtrap.development.ic.Setting;
import bedtrap.development.ic.util.Renderer3D;
import bedtrap.development.ic.util.RotationUtil;
import bedtrap.development.mixins.ClientPlayerInteractionManagerAccessor;
import bedtrap.development.systems.modules.Module;
import bedtrap.development.systems.modules.combat.PistonCrystal.PistonCrystal;
import bedtrap.development.systems.utils.advanced.DamageUtils;
import bedtrap.development.systems.utils.advanced.FindItemResult;
import bedtrap.development.systems.utils.advanced.InvUtils;
import bedtrap.development.systems.utils.other.TimerUtils;
import com.google.common.eventbus.Subscribe;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.item.PickaxeItem;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundIdS2CPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static bedtrap.development.systems.modules.combat.AutoCrystal.ACUtils.*;

/**
 * @author Eureka
 */

@Module.Info(name = "AutoCrystal", category = Module.Category.Combat)
public class AutoCrystal extends Module {

    // General
    public Setting<String> swap = register("Swap", List.of("Silent", "Normal", "OFF"), "Normal");
    public Setting<Integer> swapDelay = register("SwapDelay", 0, 0, 20);
    public Setting<Boolean> syncSlot = register("SyncSlot", true);

    // Place and Break
    public Setting<Integer> placeDelay = register("PlaceDelay", 0, 0, 10);
    public Setting<Double> placeRange = register("PlaceRange", 4.7, 0, 7, 1);
    public Setting<Integer> breakDelay = register("BreakDelay", 0, 0, 10);
    public Setting<Double> breakRange = register("BreakRange", 4.7, 0, 7, 1);
    public Setting<Boolean> smartRange = register("SmartRange", true);
    public Setting<String> fastBreak = register("FastBreak", List.of("Kill", "Instant", "Sequential", "All", "OFF"), "OFF");
    public Setting<String> freqMode = register("FreqMode", List.of("EachTick", "Divide", "OFF"), "Divide");
    public Setting<Integer> frequency = register("Frequency", 20, 0, 20);
    public Setting<Integer> ticksExisted = register("TicksExisted", 1, 0, 5);
    public Setting<Boolean> multiPlace = register("MultiPlace", false);
    public Setting<Boolean> oneTwelve = register("1.12", false);
    public Setting<Boolean> rotate = register("Rotate", false);
    public Setting<Boolean> rayTrace = register("RayTrace", false);
    public Setting<Boolean> predictID = register("PredictID", false);
    public Setting<Integer> delayID = register("DelayID", 0, 0, 5);
    public Setting<Boolean> ignoreTerrain = register("IgnoreTerrain", true);
    public Setting<Integer> blockUpdate = register("BlockUpdate", 200, 0, 500);
    public Setting<Boolean> limit = register("Limit", true);
    public Setting<Integer> limitAttacks = register("LimitAttacks", 5, 1, 10);
    public Setting<Integer> passedTicks = register("PassedTicks", 10, 0, 20);
    public Setting<String> priority = register("Prio", List.of("Place", "Break", "Smart"), "Smart");

    // Damage
    public Setting<String> doPlace = register("Place", List.of("Best DMG", "Most DMG"), "Best DMG");
    public Setting<String> doBreak = register("Break", List.of("All", "PlacePos", "Best DMG"), "Best DMG");
    public Setting<Double> minDmg = register("MinDamage", 7.5, 0, 36, 1);
    public Setting<Double> safety = register("Safety", 25, 0, 100, 1);
    public Setting<Boolean> antiSelfPop = register("AntiSelfPop", true);
    public Setting<Boolean> antiFriendDamage = register("AntiFriendDamage", false);
    public Setting<Double> friendMaxDmg = register("MaxDamage", 8, 0, 36, 1);

    // FacePlace
    public Setting<Double> faceBreaker = register("FaceBreaker", 11, 0, 36, 1);
    public Setting<Double> armorBreaker = register("ArmorBreaker", 25, 0, 100, 1);

    // Support
    public Setting<Boolean> support = register("Support", false);
    public Setting<Integer> supportDelay = register("SupportDelay", 5, 0, 10);

    // Surround
    public Setting<Boolean> crystalOnBreak = register("CrystalOnBreak", false);
    public Setting<String> surroundBreak = register("SurroundBreak", List.of("Always", "OnMine", "OFF"), "OnMine");

    // Pause
    public Setting<Boolean> eatPause = register("EatPause", true);
    public Setting<Boolean> minePause = register("MinePause", false);

    // Render
    public Setting<String> render = register("Render", List.of("Box", "Smooth", "None"), "Smooth");
    public Setting<Integer> smoothFactor = register("SmoothFactor", 10, 5, 20);
    public Setting<Integer> renderTime = register("RenderTime", 10, 0, 15);
    public Setting<Boolean> damage = register("Damage", false);
    public Setting<Boolean> tracer = register("Tracer", true);

    private final ExecutorService thread = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * (1 + 13 / 3));

    private BlockPos bestPos = new BlockPos(4, 2, 0);
    private double bestDamage = 0;

    public int placeTimer, breakTimer, swapTimer, idTimer;
    public int attacks, ticksPassed;
    public boolean firstTime;

    private boolean shouldSupport;

    public BlockPos renderPos;
    public int renderTimer;

    private int lastEntityId, last;
    private Hand hand;

    private BlockPos updatedBlock;

    public IntSet brokenCrystals = new IntOpenHashSet();
    public final int[] second = new int[20];
    public static int cps;
    public int tick, i, lastSpawned = 20;

    private final List<CrystalMap> crystalMap = new ArrayList<>();
    private final TimerUtils blockTimer = new TimerUtils();
    private final TimerUtils supportTimer = new TimerUtils();

    @Override
    public void onActivate() {
        placeTimer = 0;
        swapTimer = 0;
        idTimer = 0;
        supportTimer.reset();

        bestPos = null;
        updatedBlock = null;
        renderPos = null;

        shouldSupport = false;
        renderTimer = renderTime.get();
        brokenCrystals.clear();
        firstTime = true;

        tick = 0;
        Arrays.fill(second, 0);
        i = 0;

        crystalMap.clear();
        blockTimer.reset();
        setDisplayInfo("0.0, 0");
    }

    @Subscribe
    public void onTick(TickEvent.Post event) {
        getCPS();
        updateBlock();

        if (renderTimer > 0 && renderPos != null) renderTimer--;
        if (placeTimer > 0) placeTimer--;
        if (breakTimer > 0) breakTimer--;
        if (swapTimer > 0) swapTimer--;
        if (idTimer > 0) idTimer--;

        if (ticksPassed > 0) ticksPassed--;
        else {
            ticksPassed = 20;
            attacks = 0;
        }

        if (eatPause.get() && mc.player.isUsingItem() && (mc.player.getMainHandStack().isFood() || mc.player.getOffHandStack().isFood()))
            return;
        if (minePause.get() && mc.interactionManager.isBreakingBlock()) return;
        if (PistonCrystal.instance.shouldPause()) return;

        thread.execute(this::doCrystalOnBreak);
        thread.execute(this::doSurroundBreak);
        thread.execute(this::doSupport);
        thread.execute(this::doCalculate);

        if (firstTime && priority.get("Smart")) {
            doPlace();
            doBreak();
            doPlace();
            firstTime = !firstTime;
        } else if (priority.get("Place")) {
            doPlace();
            doBreak();
        } else {
            doBreak();
            doPlace();
        }

        crystalMap.forEach(CrystalMap::tick);
    }

    @Subscribe
    public void onAdded(EntityAddedEvent event) {
        if (!(event.entity instanceof EndCrystalEntity)) return;

        if ((fastBreak.get("Instant") || fastBreak.get("All"))) {
            if (bestPos == null) return;

            if (bestPos.equals(event.entity.getBlockPos().down())) {
                doBreak(event.entity, false);
            }
        }

        last = event.entity.getId() - lastEntityId;
        lastEntityId = event.entity.getId();
    }

    @Subscribe
    public void onRemove(EntityRemovedEvent event) {
        if (!(event.entity instanceof EndCrystalEntity)) return;

        if (brokenCrystals.contains(event.entity.getId())) {
            lastSpawned = 20;
            tick++;

            removeId(event.entity);
        }
    }

    @Subscribe
    private void onSend(PacketEvent.Send event) {
        if (event.packet instanceof UpdateSelectedSlotC2SPacket) {
            swapTimer = swapDelay.get();
        }
    }

    @Subscribe
    private void onReceive(PacketEvent.Receive event) {
        if (!(event.packet instanceof PlaySoundIdS2CPacket packet)) return;

        if (fastBreak.get("Sequential") || fastBreak.get("All")) {
            if (packet.getCategory().getName().equals(SoundCategory.BLOCKS.getName()) && packet.getSoundId().getPath().equals("entity.generic.explode")) {
                brokenCrystals.forEach(crystalMap -> mc.world.removeEntity(crystalMap, Entity.RemovalReason.KILLED));
            }
        }
    }

    @Subscribe
    public void onBlockUpdate(BlockUpdateEvent event) {
        if (event.newState.isAir()) {
            updatedBlock = event.pos;
            blockTimer.reset();
        }
    }

    private void doPlace() {
        doPlace(bestPos);
        setDisplayInfo(getBestDamage() + ", " + AutoCrystal.cps);
    }

    private void doPlace(BlockPos blockPos) {
        if (blockPos == null) bestDamage = 0;
        if (blockPos == null || placeTimer > 0) return;

        FindItemResult crystal = InvUtils.findInHotbar(Items.END_CRYSTAL);
        if (!crystal.found()) return;

        hand = mc.player.getOffHandStack().getItem() == Items.END_CRYSTAL ? Hand.OFF_HAND : Hand.MAIN_HAND;
        if (distanceTo(closestVec3d(blockPos)) > placeRange.get()) return;
        BlockHitResult hitResult = getResult(blockPos);

        if (!swap.get("OFF") && !crystal.isOffhand() && !crystal.isMainHand()) InvUtils.swap(crystal.slot());

        if (crystal.isOffhand() || crystal.isMainHand()) {
            if (rotate.get()) RotationUtil.rotate(blockPos);

            if (!hasEntity(new Box(blockPos.up()))) mc.player.swingHand(hand);
            else mc.player.networkHandler.sendPacket(new HandSwingC2SPacket(hand));

            mc.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(hand, hitResult));
        }

        if (predictID.get() && idTimer <= 0) {
            EndCrystalEntity endCrystal = new EndCrystalEntity(mc.world, blockPos.getX() + 0.5, blockPos.getY() + 1.0, blockPos.getZ() + 0.5);
            endCrystal.setShowBottom(false);
            endCrystal.setId(lastEntityId + last);

            doBreak(endCrystal, false);
            endCrystal.kill();

            idTimer = delayID.get();
        }

        if (swap.get("Silent")) {
            InvUtils.swapBack();
            if (syncSlot.get()) InvUtils.syncSlots();
        }

        placeTimer = placeDelay.get();
        setRender(blockPos);
    }

    private void doBreak() {
        doBreak(getCrystal(), true);
    }

    private void doBreak(Entity entity, boolean checkAge) {
        if (entity == null || breakTimer > 0 || swapTimer > 0 || !frequency() || (checkAge && entity.age < ticksExisted.get()))
            return;

        if (limit.get()) {
            if (!getCrystal(entity).canHit() && getCrystal(entity).attacks > limitAttacks.get()) {
                getCrystal(entity).shouldWait = true;
            }
        }

        if (distanceTo(closestVec3d(entity)) > breakRange.get()) return;
        processAttack(entity);

        if (!matchesCrystal(entity)) crystalMap.add(new CrystalMap(entity.getId(), 1));
        else getCrystal(entity).attacks++;

        if (fastBreak.get("Kill")) {
            entity.kill();

            lastSpawned = 20;
            tick++;
        }

        addBroken(entity);
        attacks++;
        breakTimer = breakDelay.get();
    }

    private BlockHitResult getResult(BlockPos blockPos) {
        Vec3d eyesPos = new Vec3d(mc.player.getX(), mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()), mc.player.getZ());

        if (!rayTrace.get()) return new BlockHitResult(closestVec3d(blockPos), Direction.UP, blockPos, false);
        for (Direction direction : Direction.values()) {
            RaycastContext raycastContext = new RaycastContext(eyesPos, new Vec3d(blockPos.getX() + 0.5 + direction.getVector().getX() * 0.5,
                    blockPos.getY() + 0.5 + direction.getVector().getY() * 0.5,
                    blockPos.getZ() + 0.5 + direction.getVector().getZ() * 0.5), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, mc.player);
            BlockHitResult result = mc.world.raycast(raycastContext);
            if (result != null && result.getType() == HitResult.Type.BLOCK && result.getBlockPos().equals(blockPos)) {
                return result;
            }
        }

        return new BlockHitResult(closestVec3d(blockPos), blockPos.getY() == 255 ? Direction.DOWN : Direction.UP, blockPos, false);
    }

    private boolean matchesCrystal(Entity entity) {
        return getCrystal(entity).attacks != 0;
    }

    private CrystalMap getCrystal(Entity entity) {
        for (CrystalMap crystal : crystalMap) {
            if (crystal.getId() == entity.getId()) return crystal;
        }

        return new CrystalMap(-9999, 0);
    }

    private void doCrystalOnBreak() {
        if (!crystalOnBreak.get()) return;

        try {
            float progress = ((ClientPlayerInteractionManagerAccessor) mc.interactionManager).getBreakingProgress();
            BlockPos breakingPos = ((ClientPlayerInteractionManagerAccessor) mc.interactionManager).getCurrentBreakingBlockPos();
            if (!partOfSurround(breakingPos)) return;

            if (progress > 0.96F) doPlace(breakingPos);
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    private void doSurroundBreak() {
        if (surroundBreak.get("OFF")) return;
        if (isFacePlacing()) return;

        if (surroundBreak.get("OnMine") && !mc.interactionManager.isBreakingBlock()) return;
        List<BlockPos> vulnerablePos = new ArrayList<>();

        try {
            for (PlayerEntity player : mc.world.getPlayers()) {
                if (player == mc.player) continue;
                if (Friends.isFriend(player)) continue;
                if (!isSurrounded(player)) continue;

                for (BlockPos bp : getSphere(player, 5)) {
                    if (hasEntity(new Box(bp), entity -> entity == mc.player || entity == player || entity instanceof ItemEntity))
                        continue;

                    boolean canPlace = mc.world.isAir(bp.up()) &&
                            (mc.world.getBlockState(bp).isOf(Blocks.OBSIDIAN) || mc.world.getBlockState(bp).isOf(Blocks.BEDROCK));

                    if (!canPlace) continue;
                    Vec3d vec3d = new Vec3d(bp.getX(), bp.getY() + 1, bp.getZ());
                    Box endCrystal = new Box(vec3d.x - 0.5, vec3d.y, vec3d.z - 0.5, vec3d.x + 1.5, vec3d.y + 2, vec3d.z + 1.5);

                    for (BlockPos surround : getSurroundBlocks(player, true)) {
                        if (mc.world.getBlockState(surround).getHardness(mc.world, surround) <= 0) return;

                        if (surroundBreak.get("OnMine") && mc.player.getMainHandStack().getItem() instanceof PickaxeItem) {
                            BlockPos breakingPos = ((ClientPlayerInteractionManagerAccessor) mc.interactionManager).getCurrentBreakingBlockPos();
                            if (breakingPos == null) return;

                            if (!surround.equals(breakingPos)) continue;
                        }
                        Box box = new Box(surround);

                        if (endCrystal.intersects(box)) vulnerablePos.add(bp);
                    }
                }
            }
        } catch (Exception e) {
            e.fillInStackTrace();
        }


        if (vulnerablePos.isEmpty()) return;
        vulnerablePos.sort(Comparator.comparingDouble(ACUtils::distanceTo));
        BlockPos blockPos = vulnerablePos.get(0);

        if (hasEntity(new Box(blockPos.up())) || distanceTo(closestVec3d(blockPos)) > placeRange.get()) return;
        doPlace(blockPos);
    }

    private void doCalculate() {
        FindItemResult crystal = InvUtils.findInHotbar(Items.END_CRYSTAL);
        if (!crystal.found()) return;

        List<BlockPos> sphere = getSphere(mc.player, Math.ceil(placeRange.get()));
        BlockPos bestPos = null;
        double bestDamage = 0.0;
        double safety = 0.0;

        try {
            for (BlockPos bp : sphere) {
                if (distanceTo(closestVec3d(bp)) > placeRange.get()) continue;

                boolean canPlace = mc.world.isAir(bp.up()) &&
                        (mc.world.getBlockState(bp).isOf(Blocks.OBSIDIAN) || mc.world.getBlockState(bp).isOf(Blocks.BEDROCK));

                if (!canPlace) continue;
                if (updatedBlock != null && updatedBlock.equals(bp.up())) continue;

                EndCrystalEntity fakeCrystal = new EndCrystalEntity(mc.world, bp.getX() + 0.5, bp.getY() + 1.0, bp.getZ() + 0.5);

                if (smartRange.get() && distanceTo(closestVec3d(fakeCrystal)) > breakRange.get()) continue;
                if (oneTwelve.get() && !mc.world.isAir(bp.up(2))) continue;

                double targetDamage = getHighestDamage(roundVec(bp), null);
                double selfDamage = DamageUtils.crystalDamage(mc.player, roundVec(bp));
                safety = (targetDamage / 36 - selfDamage / 36) * 100;

                if (safety < this.safety.get()
                        || antiSelfPop.get() && selfDamage > mc.player.getHealth() + mc.player.getAbsorptionAmount())
                    continue;

                boolean validPos = true;
                if (antiFriendDamage.get()) {
                    for (PlayerEntity friend : mc.world.getPlayers()) {
                        if (!Friends.isFriend(friend)) continue;

                        double friendDamage = DamageUtils.crystalDamage(friend, roundVec(bp));
                        if (friendDamage > friendMaxDmg.get()) {
                            validPos = false;
                            break;
                        }
                    }
                }
                if (!validPos) continue;
                if (intersectsWithEntity(bp, multiPlace.get(), fakeCrystal)) continue;


                if (targetDamage > bestDamage) {
                    bestDamage = targetDamage;
                    bestPos = bp;
                }
            }
        } catch (Exception e) {
            e.fillInStackTrace();
        }

        this.bestPos = bestPos;
        this.bestDamage = bestDamage;
    }

    private void doSupport() {
        if (!support.get() || !supportTimer.passedTicks(supportDelay.get())) return;
        List<BlockPos> support = getSphere(mc.player, Math.ceil(placeRange.get()));
        List<BlockPos> placePositions = support.stream().filter(this::canPlace).toList();
        if (placePositions.isEmpty()) shouldSupport = true;

        FindItemResult obsidian = InvUtils.findInHotbar(Items.OBSIDIAN);
        if (!shouldSupport || bestPos != null || !obsidian.found()) return;
        BlockPos bestPos = null;

        double bestDamage = 0.0;
        double safety = 0.0;

        try {
            for (BlockPos bp : support) {
                if (distanceTo(closestVec3d(bp)) > placeRange.get()) continue;

                boolean canPlace = mc.world.getBlockState(bp).isAir() && mc.world.getBlockState(bp.up()).isAir();
                if (!canPlace) continue;
                if (hasEntity(new Box(bp.up()))) continue;

                double targetDamage = getHighestDamage(roundVec(bp), bp);
                double selfDamage = DamageUtils.crystalDamage(mc.player, roundVec(bp));

                safety = (targetDamage / 36 - selfDamage / 36) * 100;

                if (safety < this.safety.get()
                        || antiSelfPop.get() && selfDamage > mc.player.getHealth() + mc.player.getAbsorptionAmount())
                    continue;

                if (targetDamage > bestDamage) {
                    bestDamage = targetDamage;
                    bestPos = bp;
                }
            }

            if (bestPos != null) {
                Hand hand = obsidian.isOffhand() ? Hand.OFF_HAND : Hand.MAIN_HAND;
                BlockHitResult hitResult = new BlockHitResult(closestVec3d(bestPos), Direction.DOWN, bestPos, false);

                InvUtils.swap(obsidian);
                mc.getNetworkHandler().sendPacket(new PlayerInteractBlockC2SPacket(hand, hitResult));
                mc.getNetworkHandler().sendPacket(new HandSwingC2SPacket(hand));
                InvUtils.swapBack();
            }
        } catch (Exception e) {
            e.fillInStackTrace();
        }

        shouldSupport = false;
        supportTimer.reset();
    }

    private boolean canPlace(BlockPos blockPos) {
        if (!mc.world.getBlockState(blockPos.up()).getFluidState().isEmpty()) return false;
        if (!(mc.world.isAir(blockPos.up()) && (mc.world.getBlockState(blockPos).isOf(Blocks.OBSIDIAN) || mc.world.getBlockState(blockPos).isOf(Blocks.BEDROCK))))
            return false;

        return !hasEntity(new Box(blockPos.up()), entity -> !(entity instanceof EndCrystalEntity));
    }

    private boolean intersectsWithEntity(BlockPos blockPos, boolean multiPlace, EndCrystalEntity fakeCrystal) {
        if (multiPlace) {
            return hasEntity(new Box(blockPos).stretch(0, 2, 0));
        } else {
            return hasEntity(new Box(blockPos).stretch(0, 2, 0), entity -> !(entity instanceof EndCrystalEntity && entity.getPos().getX() == fakeCrystal.getPos().getX() && entity.getPos().getY() == fakeCrystal.getPos().getY() && entity.getPos().getZ() == fakeCrystal.getPos().getZ()));
        }
    }

    private void updateBlock() {
        if (updatedBlock != null && blockTimer.passedMillis(blockUpdate.get())) {
            updatedBlock = null;
        }
    }

    private double getHighestDamage(Vec3d vec3d, @Nullable BlockPos supportPos) {
        if (mc.world == null || mc.player == null) return 0;
        if (mc.world.getPlayers().isEmpty()) return 0;

        double highestDamage = 0;

        for (PlayerEntity target : mc.world.getPlayers()) {
            if (Friends.isFriend(target)) continue;
            if (target == mc.player) continue;
            if (target.isDead() || target.getHealth() == 0) continue;

            double targetDamage = DamageUtils.crystalDamage(target, vec3d, supportPos, ignoreTerrain.get());

            if (targetDamage < minDmg.get() && !shouldFacePlace(target, targetDamage)) continue;

            if (doPlace.get("Best DMG")) {
                if (targetDamage > highestDamage) {
                    highestDamage = targetDamage;
                }
            } else highestDamage += targetDamage;
        }

        return highestDamage;
    }

    private EndCrystalEntity getCrystal() {
        for (Entity entity : mc.world.getEntities()) {
            if (!(entity instanceof EndCrystalEntity)) continue;
            if (mc.player.distanceTo(entity) > breakRange.get()) continue;

            if (getCrystal(entity).shouldWait) continue;
            if (doBreak.get("All")) return (EndCrystalEntity) entity;

            double tempDamage = getHighestDamage(roundVec(entity), null);
            if (tempDamage > minDmg.get()) return (EndCrystalEntity) entity;
        }

        if (bestPos == null) return null;
        return getEntity(bestPos.up());
    }

    private double getBestDamage() {
        return ((double) Math.round(bestDamage * 100) / 100);
    }

    private boolean frequency() {
        switch (freqMode.get()) {
            case "EachTick" -> {
                if (attacks > frequency.get()) return false;
            }
            case "Divide" -> {
                if (!divide(frequency.get()).contains(ticksPassed)) return false;
            }
            case "OFF" -> {
                return true;
            }
        }

        return true;
    }

    public void getCPS() {
        i++;
        if (i >= second.length) i = 0;

        second[i] = tick;
        tick = 0;

        cps = 0;
        for (int i : second) cps += i;

        lastSpawned--;
        if (lastSpawned >= 0 && cps > 0) cps--;
        if (cps == 0) bestDamage = 0.0;
    }

    public ArrayList<Integer> divide(int frequency) {
        ArrayList<Integer> freqAttacks = new ArrayList<>();
        int size = 0;

        if (20 < frequency) return freqAttacks;
        else if (20 % frequency == 0) {
            for (int i = 0; i < frequency; i++) {
                size += 20 / frequency;
                freqAttacks.add(size);
            }
        } else {
            int zp = frequency - (20 % frequency);
            int pp = 20 / frequency;

            for (int i = 0; i < frequency; i++) {
                if (i >= zp) {
                    size += pp + 1;
                    freqAttacks.add(size);
                } else {
                    size += pp;
                    freqAttacks.add(size);
                }
            }
        }

        return freqAttacks;
    }

    private void addBroken(Entity entity) {
        if (!brokenCrystals.contains(entity.getId())) brokenCrystals.add(entity.getId());
    }

    private void removeId(Entity entity) {
        if (brokenCrystals.contains(entity.getId())) brokenCrystals.remove(entity.getId());
    }

    private boolean shouldFacePlace(PlayerEntity player, double damage) {
        if (faceBreaker.get() == 0 || damage < 1.5) return false;
        if (!isSurrounded(player) || isFaceTrapped()) return false;
        return (player.getHealth() + player.getAbsorptionAmount()) <= faceBreaker.get() || getWorstArmor(player) <= armorBreaker.get();
    }

    private boolean isFacePlacing() {
        return bestPos != null && partOfSurround(bestPos);
    }

    private boolean partOfSurround(BlockPos blockPos) {
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player) continue;

            if (getSurroundBlocks(player, true).contains(blockPos)) return true;
        }

        return false;
    }

    public void setRender(BlockPos blockPos) {
        renderPos = blockPos;
        renderTimer = renderTime.get();
    }

    Box renderBox = null;

    @Subscribe
    public void onRender(Render3DEvent event) {
        // fixing AutoMine funny crash
        if (bestPos == null) return;
        if (render.get("None")) return;
        if (renderTimer == 0) renderPos = null;
        if (renderPos == null) return;

        Box post = new Box(renderPos);
        if (renderBox == null) renderBox = post;

        double x = (post.minX - renderBox.minX) / smoothFactor.get();
        double y = (post.minY - renderBox.minY) / smoothFactor.get();
        double z = (post.minZ - renderBox.minZ) / smoothFactor.get();

        renderBox = new Box(renderBox.minX + x, renderBox.minY + y, renderBox.minZ + z, renderBox.maxX + x, renderBox.maxY + y, renderBox.maxZ + z);

        Vec3d realVec = render.get("Box") ? new Vec3d(renderPos.getX() + 0.5, renderPos.getY() + 0.5, renderPos.getZ() + 0.5) : renderBox.getCenter(); // Для рендера дамага
        Vec3d fixedVec = Renderer3D.getRenderPosition(render.get("Box") ? realVec : renderBox.getCenter()); // Для рендера блока и трейсера

        Box box = new Box(fixedVec.x - 0.5, fixedVec.y - 0.5, fixedVec.z - 0.5, fixedVec.x + 0.5, fixedVec.y + 0.5, fixedVec.z + 0.5);

        if (render.get("Box")) box = Box.from(Renderer3D.getRenderPosition(renderPos));
        Renderer3D.get.drawBoxWithOutline(event.getMatrixStack(), box);

        if (damage.get()) {
            if (renderPos == null) return;

            Vec3d rPos = new Vec3d(realVec.getX(), realVec.getY(), realVec.getZ());
            Renderer3D.drawText(Text.of(String.valueOf(getBestDamage())), rPos.x, rPos.y, rPos.z, 0.76, false);
        }

        if (tracer.get()) {
            if (renderPos == null) return;

            renderTracer(event, fixedVec);
        }
    }

    private void renderTracer(Render3DEvent event, Vec3d vec3d) {
        Vec3d player = Renderer3D.getEntityRenderPosition(mc.player, event.getPartialTicks());

        Renderer3D.drawLine(event.getMatrixStack(), (float) vec3d.x, (float) vec3d.y, (float) vec3d.z, (float) player.x, (float) player.y, (float) player.z, Color.WHITE.getRGB());
    }

    public static AutoCrystal instance;

    public AutoCrystal() {
        instance = this;
    }
}