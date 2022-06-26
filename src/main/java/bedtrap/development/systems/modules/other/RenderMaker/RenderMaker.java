package bedtrap.development.systems.modules.other.RenderMaker;

import bedtrap.development.events.event.Render3DEvent;
import bedtrap.development.events.event.TickEvent;
import bedtrap.development.ic.util.Renderer3D;
import bedtrap.development.systems.modules.Module;
import bedtrap.development.systems.modules.other.Rotations.Rotations;
import com.google.common.eventbus.Subscribe;
import net.minecraft.item.SwordItem;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Module.Info(name = "RenderMaker", category = Module.Category.Other)
public class RenderMaker extends Module {
    public List<Storage> storages = new ArrayList<>();
    private Action action;

    private Storage currentStorage;

    @Override
    public void onActivate() {
        storages.clear();
        action = Action.Start;

        currentStorage = new Storage(4, 2, 0, -4, -2, -0, null, null);
    }

    @Subscribe
    public void onTick(TickEvent.Post event) {
        if (!(mc.player.getMainHandStack().getItem() instanceof SwordItem)) return;
        if (mc.crosshairTarget == null || !(mc.crosshairTarget instanceof BlockHitResult result)) return;

        if (mc.options.sprintKey.isPressed()) {
            Vec3d closest = null;
            double distance = 420;

            for (Storage storage : storages) {
                Vec3d start = new Vec3d(storage.sX, storage.sY, storage.sZ);
                Vec3d end = new Vec3d(storage.eX, storage.eY, storage.eZ);
                Vec3d current;

                double tempDistance = result.getPos().distanceTo(start);
                current = start;
                if (tempDistance > result.getPos().distanceTo(end)) {
                    tempDistance = result.getPos().distanceTo(end);
                    current = end;
                }

                if (tempDistance < distance) {
                    distance = tempDistance;
                    closest = current;
                }
            }

            if (closest == null) return;
            Rotations.instance.clientRotate(Rotations.instance.getYaw(closest), Rotations.instance.getPitch(closest));
        }

        switch (action) {
            case Start -> {
                if (mc.options.attackKey.isPressed()) {
                    currentStorage.sX = result.getPos().x;
                    currentStorage.sY = result.getPos().y;
                    currentStorage.sZ = result.getPos().z;
                    currentStorage.start = result.getBlockPos();

                    action = Action.End;
                }
            }
            case End -> {
                if (mc.options.useKey.isPressed()) {
                    currentStorage.eX = result.getPos().x;
                    currentStorage.eY = result.getPos().y;
                    currentStorage.eZ = result.getPos().z;
                    currentStorage.end = result.getBlockPos();

                    action = Action.Add;
                }
            }
            case Add -> {
                storages.add(currentStorage);
                currentStorage = new Storage(4, 2, 0, -4, -2, -0, null, null);

                action = Action.Start;
            }
        }
    }

    @Subscribe
    public void onRender(Render3DEvent event) {
        if (currentStorage.sX != 4 && currentStorage.sY != 2 && currentStorage.sZ != 0) {
            if (mc.crosshairTarget == null || !(mc.crosshairTarget instanceof BlockHitResult result)) return;

            renderTracer(event, new Vec3d(currentStorage.sX, currentStorage.sY, currentStorage.sZ), result.getPos());
        }

        if (storages.isEmpty()) return;
        storages.forEach(storage -> renderTracer(event,
                new Vec3d(storage.sX, storage.sY, storage.sZ),
                new Vec3d(storage.eX, storage.eY, storage.eZ)
        ));
    }

    private void renderTracer(Render3DEvent event, Vec3d start, Vec3d end) {
        start = Renderer3D.getRenderPosition(start);
        end = Renderer3D.getRenderPosition(end);

        Renderer3D.drawLine(event.getMatrixStack(), (float) start.x, (float) start.y, (float) start.z, (float) end.x, (float) end.y, (float) end.z, Color.WHITE.getRGB());
    }

    public enum Action {
        Start, End, Add
    }

    public static RenderMaker instance;

    public RenderMaker() {
        instance = this;
    }

    public static class Storage {
        public double sX, sY, sZ, eX, eY, eZ;
        public BlockPos start, end;

        public Storage(double sX, double sY, double sZ, double eX, double eY, double eZ, BlockPos start, BlockPos end) {
            this.sX = sX;
            this.sY = sY;
            this.sZ = sZ;
            this.eX = eX;
            this.eY = eY;
            this.eZ = eZ;
            this.start = start;
            this.end = end;
        }
    }
}
