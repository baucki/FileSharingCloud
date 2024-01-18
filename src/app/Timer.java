package app;

import servent.message.KeyMessage;
import servent.message.UpdateMessage;
import servent.message.util.MessageUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Timer {
    private ScheduledExecutorService executorService;
    private ScheduledFuture<?> scheduledFuture;
    private boolean timerOn;

    public Timer() {
        executorService = Executors.newSingleThreadScheduledExecutor();
        timerOn = true;
    }

    public void startTimer() {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(false);
        }
        scheduledFuture = executorService.schedule(() -> {
            if (timerOn) {
                AppConfig.timestampedStandardPrint("Servent" +
                        AppConfig.customState.getSuccessorTable()[0].getCustomId() + ": just died.");

                // send keys

                for (int i = 0; i < AppConfig.customState.getSsTable().length; i++) {

                    KeyMessage keyMessage = new KeyMessage(
                            AppConfig.myServentInfo.getListenerPort(),
                            AppConfig.customState.getSsTable()[i].getListenerPort(),
                            AppConfig.customState.getSuccessorTable()[0].getCustomId()
                    );
                    MessageUtil.sendMessage(keyMessage);

                    keyMessage = new KeyMessage(
                            AppConfig.myServentInfo.getListenerPort(),
                            AppConfig.customState.getSpTable()[i].getListenerPort(),
                            AppConfig.customState.getSuccessorTable()[0].getCustomId()
                    );
                    MessageUtil.sendMessage(keyMessage);

                }


                // remove node from system

                List<ServentInfo> allNodes = new ArrayList<>();
                List<ServentInfo> currentNodes = AppConfig.customState.getAllNodeInfo();
                for (ServentInfo node : currentNodes) {
                    if (node.getListenerPort() != AppConfig.customState.getSuccessorTable()[0].getListenerPort()) {
                        allNodes.add(node);
                    }
                }
                AppConfig.customState.addNodes(allNodes);

                // inform all other nodes to update

                for (int i = 0; i < AppConfig.customState.getSsTable().length; i++) {
                    UpdateMessage um = new UpdateMessage(AppConfig.myServentInfo.getListenerPort(),
                            AppConfig.customState.getSsTable()[i].getListenerPort(),
                            AppConfig.customState.getAllNodeInfo());
                    MessageUtil.sendMessage(um);

                    um = new UpdateMessage(AppConfig.myServentInfo.getListenerPort(),
                            AppConfig.customState.getSpTable()[i].getListenerPort(),
                            AppConfig.customState.getAllNodeInfo());
                    MessageUtil.sendMessage(um);
                }


            }
        }, 10, TimeUnit.SECONDS);
    }
    public void resetTimer() {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(false);
        }
        startTimer();
    }

    public void turnOffTimer() {
        timerOn = false;
        if (scheduledFuture != null) {
            scheduledFuture.cancel(false);
        }
        executorService.shutdown();
    }

}
