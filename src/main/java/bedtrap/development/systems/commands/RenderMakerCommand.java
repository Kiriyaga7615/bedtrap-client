package bedtrap.development.systems.commands;

import bedtrap.development.ic.Command;
import bedtrap.development.ic.Configs;
import bedtrap.development.systems.modules.other.RenderMaker.RenderMaker;
import bedtrap.development.systems.utils.game.ChatUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class RenderMakerCommand extends Command {
    public RenderMakerCommand() {
        super(new String[]{"rendermaker", "rm"});
    }

    @Override
    public void onCommand(String name, String[] args) {
        BufferedWriter bw;
        try {
            bw = new BufferedWriter(new FileWriter(Configs.mainFolder.getAbsolutePath() + "/RenderMaker.txt"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (args[0].equalsIgnoreCase("get")) {
            write(bw, "    public void render(Render3DEvent event, BlockPos blockPos) {");

            for (RenderMaker.Storage storage : RenderMaker.instance.storages) {
                double sX = round(storage.sX - storage.start.getX());
                double sY = round(storage.sY - storage.start.getY());
                double sZ = round(storage.sZ - storage.start.getZ());

                double eX = round(storage.eX - storage.end.getX());
                double eY = round(storage.eY - storage.end.getY());
                double eZ = round(storage.eZ - storage.end.getZ());

                write(bw, "        renderLine(event, new Vec3d(blockPos.getX() + " + sX + ", blockPos.getY() + " + sY + ", blockPos.getZ() + " + sZ + "), new Vec3d(blockPos.getX() + " + eX + ", blockPos.getY() + " + eY + ", blockPos.getZ() + " + eZ + "));");
            }

            write(bw, "    }");
            write(bw, """
                                        
                        private void renderLine(Render3DEvent event, Vec3d start, Vec3d end) {
                            start = Renderer3D.INSTANCE.getRenderPosition(start);
                            end = Renderer3D.INSTANCE.getRenderPosition(end);
                                        
                            Renderer3D.INSTANCE.drawLine(event.getMatrixStack(), (float) start.x, (float) start.y, (float) start.z, (float) end.x, (float) end.y, (float) end.z, Color.WHITE.getRGB());
                        }
                    """);

            try {
                bw.close();
                ChatUtils.info("RenderMaker", "Saving...");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            ChatUtils.info("FakePlayer", "Invalid input! Usage: " + getPrefix() + " get.");
        }
    }

    private double round(double num) {
        return ((double) Math.round(num * 100) / 100);
    }

    private void write(BufferedWriter bw, String text) {
        try {
            bw.write(text + "\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
