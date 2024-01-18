package servent.handler;

import app.AppConfig;
import servent.message.*;
import servent.message.util.MessageUtil;

public class StatusDeadHandler implements MessageHandler{

    private Message clientMessage;

    public StatusDeadHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() == MessageType.STATUS_DEAD) {

            StatusDeadMessage statusDeadMessage = (StatusDeadMessage) clientMessage;
            // successor said he is alive, check again

            if (clientMessage.getSenderPort() == AppConfig.customState.getSuccessorTable()[0].getListenerPort()) {

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
            AppConfig.timestampedErrorPrint("Welcome handler got a message that is not STATUS_DEAD");
        }
    }
}
