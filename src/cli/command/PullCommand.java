package cli.command;

import app.AppConfig;
public class PullCommand implements CLICommand{
    @Override
    public String commandName() {
        return "pull";
    }

    @Override
    public void execute(String args) {
        AppConfig.customState.getFileSystemService().pull("", args);
    }
}
