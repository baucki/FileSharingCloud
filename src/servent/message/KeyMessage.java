package servent.message;

public class KeyMessage extends BasicMessage{

    private int key;
    public KeyMessage(int senderPort, int receiverPort, int key) {
        super(MessageType.KEY, senderPort, receiverPort);
        this.key = key;
    }

    public int getKey() {
        return key;
    }
}
