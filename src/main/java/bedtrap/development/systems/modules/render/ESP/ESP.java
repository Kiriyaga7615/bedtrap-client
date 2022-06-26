package bedtrap.development.systems.modules.render.ESP;

import bedtrap.development.events.event.Render3DEvent;
import bedtrap.development.events.event.TickEvent;
import bedtrap.development.ic.Friends;
import bedtrap.development.ic.Setting;
import bedtrap.development.ic.util.Renderer3D;
import bedtrap.development.systems.modules.Module;
import bedtrap.development.systems.modules.other.Colors.Colors;
import bedtrap.development.systems.modules.other.HUD.HUD;
import com.google.common.eventbus.Subscribe;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Module.Info(name = "ESP", category = Module.Category.Render)
public class ESP extends Module {

    public static ESP get;
    public Setting<String> mode = register("Mode", List.of("Box", "FancyBox"), "FancyBox");
    public Setting<Boolean> players = register("Players", true);
    public Setting<Boolean> animals = register("Animals", false);
    public Setting<Boolean> monsters = register("Monsters", true);
    public Setting<Boolean> items = register("Items", true);
    public Setting<Boolean> others = register("Others", false);
    public Setting<Double> renderHeight = register("RenderHeight", 1D, 0.1D, 1.5D, 1);
    public Setting<String> renderMode = register("RendererMode", List.of("Normal", "Fancy"), "Fancy");
    public Setting<Boolean> holes = register("Holes", false);
    public Setting<Boolean> ignoreOwn = register("IgnoreOwn", true);

    private final List<Hole> holeList = new ArrayList<>();

    public ESP() {
        get = this;
    }

    @Subscribe
    public void onTick(TickEvent.Post event) {
        if (!holes.get()) return;
        BlockPos centerPos = mc.player.getBlockPos();

        holeList.clear();

        for (int i = centerPos.getX() - 7; i < centerPos.getX() + 7; i++) {
            for (int j = centerPos.getY() - 4; j < centerPos.getY() + 4; j++) {
                for (int k = centerPos.getZ() - 7; k < centerPos.getZ() + 7; k++) {
                    BlockPos pos = new BlockPos(i, j, k);

                    if (!allowed(pos, ignoreOwn.get())) continue;

                    int safe = 0;
                    int unsafe = 0;

                    for (Direction direction : Direction.values()) {
                        if (direction == Direction.UP || direction == Direction.DOWN) continue;

                        BlockState state = mc.world.getBlockState(pos.offset(direction));

                        if (state.getBlock() == Blocks.BEDROCK) safe++;
                        else if (state.getBlock() == Blocks.OBSIDIAN) unsafe++;
                    }
                    if (safe + unsafe != 4) continue;

                    if (safe == 4) holeList.add(new Hole(pos, Hole.HoleType.Safe)); else
                        holeList.add(new Hole(pos, Hole.HoleType.Unsafe));
                }
            }
        }

    }

    @Subscribe
    public void onRender(Render3DEvent event) {
        if (holes.get()) for (Hole hole : holeList) hole.render(event,renderHeight.get());

        if (mode.get("Box")) {
            int i = 0;

            for (Entity entity : mc.world.getEntities()) {
                if (players.get() && entity instanceof PlayerEntity player && player != mc.player) {
                    Renderer3D.get.drawEntityBox(event.getMatrixStack(), player, event.getPartialTicks(), Friends.isFriend((PlayerEntity) entity) ? Color.CYAN.getRGB() : new Color(220, 0, 0, 255).getRGB());
                    i++;
                }
                if (animals.get() && entity instanceof AnimalEntity animal) {
                    Renderer3D.get.drawEntityBox(event.getMatrixStack(), animal, event.getPartialTicks(), new Color(0, 220, 0, 255).getRGB());
                    i++;
                }
                if (monsters.get() && entity instanceof Monster monster) {
                    Renderer3D.get.drawEntityBox(event.getMatrixStack(), (Entity) monster, event.getPartialTicks(), new Color(220, 0, 0, 255).getRGB());
                    i++;
                }
                if (items.get() && entity instanceof ItemEntity item) {
                    int a = entity.age * 10;
                    if (a > 254) a = 255;
                    Renderer3D.get.drawEntityBox(event.getMatrixStack(), item, event.getPartialTicks(), new Color(Colors.get.r.get().intValue(), Colors.get.g.get().intValue(), Colors.get.b.get().intValue(), a).getRGB());
                    i++;
                }
                if (others.get() && !(entity instanceof PlayerEntity) && !(entity instanceof ItemEntity) && !(entity instanceof Monster) && !(entity instanceof AnimalEntity)) {
                    Renderer3D.get.drawEntityBox(event.getMatrixStack(), entity, event.getPartialTicks(), new Color(Colors.get.r.get().intValue(), Colors.get.g.get().intValue(), Colors.get.b.get().intValue(), 255).getRGB());
                    i++;
                }
            }
            setDisplayInfo(String.valueOf(i));
        }

        if (mode.get("FancyBox")) {
            int i = 0;

            for (Entity entity : mc.world.getEntities()) {
                if (players.get() && entity instanceof PlayerEntity player && player != mc.player) {
                    Renderer3D.get.fancyBox(event.getMatrixStack(), player, event.getPartialTicks(), Friends.isFriend(player.getEntityName()) ? Color.CYAN.getRGB() : new Color(220, 0, 0, 255).getRGB());
                    i++;
                }
                if (animals.get() && entity instanceof AnimalEntity animal) {
                    Renderer3D.get.fancyBox(event.getMatrixStack(), animal, event.getPartialTicks(), new Color(0, 220, 0, 255).getRGB());
                    i++;
                }
                if (monsters.get() && entity instanceof Monster monster) {
                    Renderer3D.get.fancyBox(event.getMatrixStack(), (Entity) monster, event.getPartialTicks(), new Color(220, 0, 0, 255).getRGB());
                    i++;
                }
                if (items.get() && entity instanceof ItemEntity item) {
                    int a = entity.age * 10;
                    if (a > 254) a = 255;
                    Renderer3D.get.fancyBox(event.getMatrixStack(), item, event.getPartialTicks(), new Color(HUD.get.colorType().getRed(), HUD.get.colorType().getGreen(), HUD.get.colorType().getBlue(), a).getRGB());
                    i++;
                }
                if (others.get() && !(entity instanceof PlayerEntity) && !(entity instanceof ItemEntity) && !(entity instanceof Monster) && !(entity instanceof AnimalEntity)) {
                    Renderer3D.get.fancyBox(event.getMatrixStack(), entity, event.getPartialTicks(), new Color(Colors.get.r.get().intValue(), Colors.get.g.get().intValue(), Colors.get.b.get().intValue(), 255).getRGB());
                    i++;
                }
            }
            setDisplayInfo(String.valueOf(i));
        }
    }

    private boolean allowed(BlockPos pos, boolean ignoreOwn) {
        if (!mc.world.isAir(pos)) return false;
        if ((ignoreOwn && (mc.player.getBlockPos().equals(pos)))) return false;
        for (int h = 0; h < 4; h++) {
            if (!mc.world.getBlockState(pos.up(h)).isAir()) return false;
        }
        return !mc.world.isAir(pos.down());
    }

    private static class Hole {
        public BlockPos blockPos;
        public Hole.HoleType type;

        public Hole(BlockPos blockPos, Hole.HoleType holeType) {
            this.blockPos = blockPos;
            this.type = holeType;
        }

        public int safeColor(){
            if (Colors.get.g.get() + Colors.get.b.get() <= 99)
                return new Color(0,120,0,120).getRGB();
            return HUD.get.color();
        }
        public int getColor() {
            return switch (this.type) {
                case Unsafe -> new Color(120,0,0,120).getRGB();
                default -> safeColor();
            };
        }

        public void render(Render3DEvent event, double renderHeight) {
            Vec3d vec3d = Renderer3D.get.getRenderPosition(blockPos.getX(), blockPos.getY(), blockPos.getZ());
            Box box = new Box(vec3d.getX(), vec3d.getY(), vec3d.getZ(), vec3d.getX()+1, vec3d.getY()+renderHeight, vec3d.getZ()+1);

            if (ESP.get.renderMode.get("Normal")) {
                Renderer3D.get.drawBox(event.getMatrixStack(), box, getColor());
                Renderer3D.get.drawOutlineBox(event.getMatrixStack(), box, getColor());
            } else {
                Renderer3D.get.fancyUrn(event.getMatrixStack(), box, getColor());
            }
        }

        public enum HoleType {
            Safe,
            Unsafe,
        }
    }
}
