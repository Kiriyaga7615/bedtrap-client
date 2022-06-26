package bedtrap.development.events.event;

import bedtrap.development.events.Cancelled;
import net.minecraft.client.util.math.MatrixStack;


public class RenderEvent extends Cancelled {

    private final MatrixStack stack;

    public RenderEvent(MatrixStack stack) {
        this.stack = stack;
    }

    public MatrixStack getMatrixStack() {
        return stack;
    }

}
