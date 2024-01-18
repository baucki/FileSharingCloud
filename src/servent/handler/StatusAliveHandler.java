package servent.handler;

import app.AppConfig;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.NodeStatusMessage;
import servent.message.StatusAliveMessage;
import servent.message.util.MessageUtil;

public class StatusAliveHandler implements MessageHandler{

    Message clientMessage;

    public StatusAliveHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() == MessageType.STATUS_ALIVE) {
            StatusAliveMessage statusAliveMessage = (StatusAliveMessage) clientMessage;
            // successor said he is alive, check again

            if (clientMessage.getSenderPort() == AppConfig.customState.getSuccessorTable()[0].getListenerPort()) {

                AppConfig.customState.setSsTable(statusAliveMessage.getSuccessorTable());
                AppConfig.customState.setSpTable(statusAliveMessage.getPredecessorTable());

                NodeStatusMessage nodeStatusMessage = new NodeStatusMessage(
                        clientMessage.getReceiverPort(),
                        clientMessage.getSenderPort()
                );
                try {
                    Thread.sleep(3000);
                    MessageUtil.sendMessage(nodeStatusMessage);
                    AppConfig.customState.getTimer().resetTimer();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

        } else {
            AppConfig.timestampedErrorPrint("Welcome handler got a message that is not STATUS_ALIVE");
        }
    }
}
