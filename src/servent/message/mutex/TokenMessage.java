package servent.message.mutex;

import app.ServentInfo;
import servent.message.BasicMessage;
import servent.message.MessageType;

public class TokenMessage extends BasicMessage{

    private static final long serialVersionUID = 8653937872880497588L;

    public TokenMessage(int senderPort, int receiverPort) {
        super(MessageType.TOKEN, senderPort, receiverPort);
    }

}
