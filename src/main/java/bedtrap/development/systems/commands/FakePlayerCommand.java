package bedtrap.development.systems.commands;

import bedtrap.development.ic.Command;
import bedtrap.development.systems.modules.other.FakePlayer.FakePlayer;
import bedtrap.development.systems.utils.game.ChatUtils;

import java.io.ObjectInputFilter;

public class FakePlayerCommand extends Command {
    public FakePlayerCommand() {
        super(new String[]{"fakeplayer", "fp"});
    }

    @Override
    public void onCommand(String name, String[] args) {
        switch (args[0].toLowerCase()) {
            case "play" -> {
                FakePlayer.instance.action = FakePlayer.Action.Play;
                FakePlayer.instance.startPlaying = true;
                ChatUtils.info("FakePlayer", "Playing...");
            }
            case "record" -> {
                FakePlayer.instance.action = FakePlayer.Action.Record;
                FakePlayer.instance.startRecording = true;
                ChatUtils.info("FakePlayer", "Recording...");
            }
            case "stop" -> {
                FakePlayer.instance.action = FakePlayer.Action.Stop;
                ChatUtils.info("FakePlayer", "Recording was stopped...");
            }
            default -> ChatUtils.info("FakePlayer", "Invalid input! Usage: " + getPrefix() + " fakeplayer play/record/stop.");
        }
    }
}
