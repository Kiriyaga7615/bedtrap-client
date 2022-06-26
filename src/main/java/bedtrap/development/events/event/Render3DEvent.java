package bedtrap.development.events.event;

import bedtrap.development.events.Cancelled;
import net.minecraft.client.util.math.MatrixStack;


public class Render3DEvent extends Cancelled {

    private final float partialTicks;
    private final MatrixStack matrixStack;

    public Render3DEvent(MatrixStack matrixStack, float partialTicks2) {
        this.partialTicks = partialTicks2;
        this.matrixStack = matrixStack;
    }

    public float getPartialTicks() {
        return partialTicks;
    }

    public MatrixStack getMatrixStack() {
        return matrixStack;
    }

    public static class EventRender3DNoBob extends Cancelled {

        private final float partialTicks;
        private final MatrixStack matrixStack;

        public EventRender3DNoBob(MatrixStack matrixStack, float partialTicks2) {
            this.partialTicks = partialTicks2;
            this.matrixStack = matrixStack;
        }

        public float getPartialTicks() {
            return partialTicks;
        }

        public MatrixStack getMatrixStack() {
            return matrixStack;
        }

    }
}
