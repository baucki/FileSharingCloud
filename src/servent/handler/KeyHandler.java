package servent.handler;

import app.AppConfig;
import servent.message.KeyMessage;
import servent.message.Message;
import servent.message.MessageType;

public class KeyHandler implements MessageHandler{
    Message clientMessage;
    public KeyHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() == MessageType.KEY) {

            KeyMessage keyMessage = (KeyMessage) clientMessage;

            if (!AppConfig.customState.getKeys().contains(keyMessage.getKey())) {
                AppConfig.customState.getKeys().add(keyMessage.getKey());
            }

        } else {
            AppConfig.timestampedErrorPrint("Sorry handler got a message that is not KEY");
        }
    }
}
