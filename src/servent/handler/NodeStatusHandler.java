package servent.handler;

import app.AppConfig;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.StatusAliveMessage;
import servent.message.util.MessageUtil;

public class NodeStatusHandler implements MessageHandler{

    private Message clientMessage;

    public NodeStatusHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() == MessageType.NODE_STATUS) {

            // respond with is alive
            if (clientMessage.getSenderPort() == AppConfig.customState.getPredecessorTable()[0].getListenerPort()) {
                StatusAliveMessage statusAliveMessage = new StatusAliveMessage(
                        clientMessage.getReceiverPort(),
                        clientMessage.getSenderPort(),
                        AppConfig.customState.getSuccessorTable(),
                        AppConfig.customState.getPredecessorTable()
                );
                try {
                    Thread.sleep(3000);
                    MessageUtil.sendMessage(statusAliveMessage);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

        } else {
            AppConfig.timestampedErrorPrint("Welcome handler got a message that is not NODE_STATUS");
        }

    }
}
