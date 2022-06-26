package bedtrap.development.ic;

import bedtrap.development.BedTrap;
import bedtrap.development.ic.util.Wrapper;
import bedtrap.development.systems.commands.WaypointCommand;
import bedtrap.development.systems.modules.Module;
import bedtrap.development.systems.modules.Modules;
import bedtrap.development.systems.modules.other.Waypoints.Waypoints;
import bedtrap.development.systems.utils.other.Colors;
import com.google.gson.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class Configs extends Thread implements Wrapper {
    public static Configs get;
    public static final File mainFolder = new File("BedTrap");
    private static final String modulesFolder = mainFolder.getAbsolutePath() + "/modules";
    private static final String waypointsFolder = mainFolder.getAbsolutePath() + "/waypoints";
    private static final String prefix = "Prefix.json";
    private static final String friends = "Friends.json";

    public Configs() {
        get = this;
    }

    public void load() {
        try {
            loadPrefix();
            loadModules();
            loadWaypoints();
            loadFriends();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadModules() throws IOException {
        for (Module m : Modules.getModules()) {
            loadModule(m);
        }
    }

    private void loadModule(Module m) throws IOException {
        Path path = Path.of(modulesFolder, m.getName() + ".json");
        if (!path.toFile().exists()) return;
        String rawJson = loadFile(path.toFile());
        JsonObject jsonObject = new JsonParser().parse(rawJson).getAsJsonObject();
        if (jsonObject.get("Enabled") != null && jsonObject.get("Drawn") != null && jsonObject.get("Bind") != null) {
            if (jsonObject.get("Enabled").getAsBoolean()) m.toggle();
            m.setDrawn(jsonObject.get("Drawn").getAsBoolean());
            m.setBind(jsonObject.get("Bind").getAsInt());
        }
        BedTrap.getSettingManager().getSettingsForMod(m).forEach(s -> {
            JsonElement settingObject = jsonObject.get(s.getName());
            if (settingObject != null) {
                switch (s.getType()) {
                    case Boolean -> s.setValue(settingObject.getAsBoolean());
                    case Double -> s.setValue(settingObject.getAsDouble());
                    case Mode -> s.setValue(settingObject.getAsString());
                    case Integer -> s.setValue(settingObject.getAsInt());
                }
            }
        });
    }

    private void loadWaypoints() throws IOException {
        Path path = Path.of(waypointsFolder, "Waypoints.json");
        if (!path.toFile().exists()) return;

        String rawJson = loadFile(path.toFile());
        JsonObject jsonObject = new JsonParser().parse(rawJson).getAsJsonObject();

        int size = jsonObject.size();
        for (int i = 0; i < size; i++) {
            if (jsonObject.get(String.valueOf(i)) == null) continue;
            String[] waypoint = jsonObject.get(String.valueOf(i)).getAsString().split(":");
            String[] blockPos = waypoint[0].split(" ");

            int x = Integer.parseInt(blockPos[0]);
            int y = Integer.parseInt(blockPos[1]);
            int z = Integer.parseInt(blockPos[2]);

            String name = waypoint[1];
            Formatting color = Colors.getFormatting(waypoint[2]);
            int id = Integer.parseInt(waypoint[3]);

            Waypoints.get.waypoints.add(new Waypoints.Waypoint(new BlockPos(x, y, z), name, color, id));
        }

        if (size != 0) WaypointCommand.waypointId = size - 1;
    }

    private void loadFriends() throws IOException {
        Path path = Path.of(mainFolder.getAbsolutePath(), friends);
        if (!path.toFile().exists()) return;
        String rawJson = loadFile(path.toFile());
        JsonObject jsonObject = new JsonParser().parse(rawJson).getAsJsonObject();
        if (jsonObject.get("Friends") != null) {
            JsonArray friendObject = jsonObject.get("Friends").getAsJsonArray();
            friendObject.forEach(object -> BedTrap.getFriendManager().getFriends().add(object.getAsString()));
        }
    }

    private void loadPrefix() throws IOException {
        Path path = Path.of(mainFolder.getAbsolutePath(), prefix);
        if (!path.toFile().exists()) return;
        String rawJson = loadFile(path.toFile());
        JsonObject jsonObject = new JsonParser().parse(rawJson).getAsJsonObject();
        if (jsonObject.get("Prefix") != null) {
            Command.setPrefix(jsonObject.get("Prefix").getAsString());
        }
    }

    public String loadFile(File file) throws IOException {
        FileInputStream stream = new FileInputStream(file.getAbsolutePath());
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(stream))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        }
        return resultStringBuilder.toString();
    }

    @Override
    public void run() {
        if (!mainFolder.exists() && !mainFolder.mkdirs()) System.out.println("Failed to create config folder");
        if (!new File(modulesFolder).exists() && !new File(modulesFolder).mkdirs())
            System.out.println("Failed to create modules folder");
        if (!new File(waypointsFolder).exists() && !new File(waypointsFolder).mkdirs())
            System.out.println("Failed to create waypoint folder");
        try {
            saveModules();
            saveWaypoints();
            saveFriends();
            savePrefix();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveModules() throws IOException {
        for (Module m : Modules.getModules()) {
            saveModule(m);
        }
    }

    private void saveWaypoints() throws IOException {
        Path path = Path.of(waypointsFolder, "Waypoints.json");
        createFile(path);

        JsonObject jsonObject = new JsonObject();
        for (Waypoints.Waypoint waypoint : Waypoints.get.waypoints) {
            jsonObject.add(waypoint.writeId(), new JsonPrimitive(waypoint.writeData()));
        }
        Files.write(path, gson.toJson(new JsonParser().parse(jsonObject.toString())).getBytes());
    }

    private void saveModule(Module m) throws IOException {
        Path path = Path.of(modulesFolder, m.getName() + ".json");
        createFile(path);
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("Enabled", new JsonPrimitive(m.isActive()));
        jsonObject.add("Drawn", new JsonPrimitive(m.isDrawn()));
        jsonObject.add("Bind", new JsonPrimitive(m.getBind()));
        BedTrap.getSettingManager().getSettingsForMod(m).forEach(s -> {
            switch (s.getType()) {
                case Mode -> jsonObject.add(s.getName(), new JsonPrimitive((String) s.get()));
                case Boolean -> jsonObject.add(s.getName(), new JsonPrimitive((Boolean) s.get()));
                case Double -> jsonObject.add(s.getName(), new JsonPrimitive((Double) s.get()));
                case Integer -> jsonObject.add(s.getName(), new JsonPrimitive((Integer) s.get()));
            }
        });
        Files.write(path, gson.toJson(new JsonParser().parse(jsonObject.toString())).getBytes());
    }

    private void saveFriends() throws IOException {
        Path path = Path.of(mainFolder.getAbsolutePath(), friends);
        createFile(path);
        JsonObject jsonObject = new JsonObject();
        JsonArray friends = new JsonArray();
        BedTrap.getFriendManager().getFriends().forEach(friends::add);
        jsonObject.add("Friends", friends);
        Files.write(path, gson.toJson(new JsonParser().parse(jsonObject.toString())).getBytes());
    }

    private void savePrefix() throws IOException {
        Path path = Path.of(mainFolder.getAbsolutePath(), prefix);
        createFile(path);
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("Prefix", new JsonPrimitive(Command.getPrefix()));
        Files.write(path, gson.toJson(new JsonParser().parse(jsonObject.toString())).getBytes());
    }

    private void createFile(Path path) {
        if (Files.exists(path)) new File(path.normalize().toString()).delete();
        try {
            Files.createFile(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
