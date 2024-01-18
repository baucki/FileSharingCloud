package cli.command;

import app.AppConfig;
import app.ServentInfo;

public class PredecessorInfo implements CLICommand{
    @Override
    public String commandName() {
        return "predecessor_info";
    }

    @Override
    public void execute(String args) {
        ServentInfo[] predecessorTable = AppConfig.customState.getPredecessorTable();

        int num = 0;
        for (ServentInfo serventInfo : predecessorTable) {
            System.out.println(num + ": " + serventInfo);
            num++;
        }
    }
}
