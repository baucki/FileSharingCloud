package cli.command;

import app.AppConfig;

public class RemoveCommand implements CLICommand{
    @Override
    public String commandName() {
        return "remove";
    }

    @Override
    public void execute(String args) {
        AppConfig.customState.getFileSystemService().remove(args);
    }
}
