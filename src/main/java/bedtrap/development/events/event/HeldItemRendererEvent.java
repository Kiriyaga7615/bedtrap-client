package bedtrap.development.events.event;

import bedtrap.development.events.Cancelled;
import net.minecraft.client.util.math.MatrixStack;

public class HeldItemRendererEvent extends Cancelled {
    public static class Hand extends HeldItemRendererEvent {
        public net.minecraft.util.Hand hand;
        public MatrixStack matrix;

        public Hand(net.minecraft.util.Hand hand, MatrixStack matrices) {
            this.hand = hand;
            this.matrix = matrices;

        }
    }

    public static class Update extends HeldItemRendererEvent  {

    }
}
