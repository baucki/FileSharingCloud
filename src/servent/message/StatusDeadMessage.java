package servent.message;

public class StatusDeadMessage extends BasicMessage {

    private static final long serialVersionUID = 4507821711707704107L;

    public StatusDeadMessage(int senderPort, int receiverPort) {
        super(MessageType.STATUS_DEAD, senderPort, receiverPort);
    }


}
