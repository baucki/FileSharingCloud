package cli.command;

import app.AppConfig;
import app.FileSystemService;
public class AddCommand implements CLICommand{

    @Override
    public String commandName() {
        return "add";
    }

    @Override
    public void execute(String args) {
        AppConfig.customState.getFileSystemService().add(args);
    }
}
