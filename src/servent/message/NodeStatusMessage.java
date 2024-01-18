package servent.message;

public class NodeStatusMessage extends BasicMessage{

    private static final long serialVersionUID = 4507821711707704107L;

    public NodeStatusMessage(int senderPort, int receiverPort) {
        super(MessageType.NODE_STATUS, senderPort, receiverPort);
    }

}
