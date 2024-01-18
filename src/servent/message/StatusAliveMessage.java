package servent.message;

import app.ServentInfo;

public class StatusAliveMessage extends BasicMessage{

    private ServentInfo[] successorTable;
    private ServentInfo[] predecessorTable;

    public StatusAliveMessage(int senderPort, int receiverPort, ServentInfo[] successorTable, ServentInfo[] predecessorTable) {
        super(MessageType.STATUS_ALIVE, senderPort, receiverPort);
        this.successorTable = successorTable;
        this.predecessorTable = predecessorTable;
    }
    public ServentInfo[] getSuccessorTable() {
        return successorTable;
    }
    public ServentInfo[] getPredecessorTable() {
        return predecessorTable;
    }
}
