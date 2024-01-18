package servent.message;

public class PullResponseMessage extends BasicMessage{


    public PullResponseMessage(int senderPort, int receiverPort, String file) {
        super(MessageType.PULL_RESPONSE, senderPort, receiverPort, file);
    }
}
