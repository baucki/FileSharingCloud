package cli.command;

import app.AppConfig;

public class WeakGroundsDismissalCommand implements CLICommand{
    @Override
    public String commandName() {
        return "weak_grounds_for_dismissal";
    }

    @Override
    public void execute(String args) {
        AppConfig.timestampedStandardPrint("Weak grounds for dismissal: " + AppConfig.WEAK_GROUNDS_FOR_DISMISSAL);
    }
}
