package servent.message;

public class PullRequestMessage extends BasicMessage{

    public PullRequestMessage(int senderPort, int receiverPort, String file) {
        super(MessageType.PULL_REQUEST, senderPort, receiverPort, file);
    }
}
