package servent.handler;

import app.AppConfig;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.PullResponseMessage;
import servent.message.util.MessageUtil;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PullRequestHandler implements MessageHandler{

    Message clientMessage;

    private static Set<Message> receivedBroadcasts = Collections.newSetFromMap(new ConcurrentHashMap<Message, Boolean>());
    public PullRequestHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() == MessageType.PULL_REQUEST) {
            int serventSenderPort = clientMessage.getSenderPort();

            int serventId = Integer.parseInt(clientMessage.getMessageText().split("/")[0]);

            if (serventId == AppConfig.myServentInfo.getCustomId()) {
                // it was meant for me
                String file = "servent" + clientMessage.getMessageText();
                PullResponseMessage pullResponseMessage = new PullResponseMessage(
                        AppConfig.myServentInfo.getListenerPort(),
                        clientMessage.getRoute().get(0).getListenerPort(),
                        file);
                MessageUtil.sendMessage(pullResponseMessage);
            }

            if (serventSenderPort == AppConfig.myServentInfo.getListenerPort()) {
                // got my own message
                AppConfig.timestampedStandardPrint("Got own message back. No rebroadcast.");
            } else {
                boolean didPut = receivedBroadcasts.add(clientMessage);

                if (didPut) {

                    for (int i = 0; i < AppConfig.customState.getSuccessorTable().length; i++) {
                        MessageUtil.sendMessage(
                                clientMessage.changeReceiver(
                                        AppConfig.customState.getSuccessorTable()[i]
                                                .getListenerPort()).makeMeASender());
                        MessageUtil.sendMessage(
                                clientMessage.changeReceiver(
                                AppConfig.customState.getPredecessorTable()[i]
                                        .getListenerPort()).makeMeASender());
                    }

                }

            }


        }


    }
}
