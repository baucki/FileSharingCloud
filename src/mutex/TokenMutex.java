package mutex;

import app.AppConfig;
import servent.message.Message;
import servent.message.mutex.TokenMessage;
import servent.message.util.MessageUtil;

public class TokenMutex implements DistributedMutex{

    private volatile boolean haveToken = false;
    private volatile boolean wantToken = false;

    @Override
    public void lock() {
        wantToken = true;

        while (!haveToken) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void unlock() {
        haveToken = false;
        wantToken = false;
        sendTokenForward();
    }

    public void receiveToken() {
        if (wantToken) {
            haveToken = true;
        } else {
            sendTokenForward();
        }
    }

    public void sendTokenForward() {
        MessageUtil.sendMessage(new TokenMessage(
                AppConfig.myServentInfo.getListenerPort(),
                AppConfig.customState.getSuccessorTable()[0].getListenerPort())
        );
    }

}
