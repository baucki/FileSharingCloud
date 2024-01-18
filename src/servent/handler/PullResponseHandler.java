package servent.handler;

import app.AppConfig;
import servent.message.Message;
import servent.message.MessageType;

public class PullResponseHandler implements MessageHandler{

    Message clientMessage;

    public PullResponseHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() == MessageType.PULL_RESPONSE) {
            AppConfig.customState.getFileSystemService().pull(clientMessage.getMessageText(), "");
        }
    }
}