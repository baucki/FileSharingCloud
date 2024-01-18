package cli.command;

import app.AppConfig;

public class StrongGroundsDismissalCommand implements CLICommand{
    @Override
    public String commandName() {
        return "strong_grounds_for_dismissal";
    }

    @Override
    public void execute(String args) {
        AppConfig.timestampedStandardPrint("Strong grounds for dismissal: " + AppConfig.STRONG_GROUNDS_FOR_DISMISSAL);
    }
}
