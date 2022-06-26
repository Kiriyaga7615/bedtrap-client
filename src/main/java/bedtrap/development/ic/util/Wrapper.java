package bedtrap.development.ic.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;


public interface Wrapper {

    MinecraftClient mc = MinecraftClient.getInstance();

    TextRenderer textRender = mc.textRenderer;

    Gson gson = new GsonBuilder().setPrettyPrinting().create();

}
