package servent;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import app.AppConfig;
import app.Cancellable;
import mutex.DistributedMutex;
import servent.handler.*;
import servent.handler.mutex.TokenHandler;
import servent.message.Message;
import servent.message.util.MessageUtil;
import servent.message.StatusDeadMessage;

public class SimpleServentListener implements Runnable, Cancellable {

	private volatile boolean working = true;

	private DistributedMutex mutex;
	public SimpleServentListener(DistributedMutex mutex) {
		this.mutex = mutex;
	}

	/*
	 * Thread pool for executing the handlers. Each client will get it's own handler thread.
	 */
	private final ExecutorService threadPool = Executors.newWorkStealingPool();
	
	@Override
	public void run() {
		ServerSocket listenerSocket = null;
		try {
			listenerSocket = new ServerSocket(AppConfig.myServentInfo.getListenerPort(), 100);
			/*
			 * If there is no connection after 1s, wake up and see if we should terminate.
			 */
			listenerSocket.setSoTimeout(1000);
		} catch (IOException e) {
			AppConfig.timestampedErrorPrint("Couldn't open listener socket on: " + AppConfig.myServentInfo.getListenerPort());
			System.exit(0);
		}
		
		
		while (working) {
			try {
				Message clientMessage;
				
				Socket clientSocket = listenerSocket.accept();
				
				//GOT A MESSAGE! <3
				clientMessage = MessageUtil.readMessage(clientSocket);
				
				MessageHandler messageHandler = new NullHandler(clientMessage);
				
				/*
				 * Each message type has it's own handler.
				 * If we can get away with stateless handlers, we will,
				 * because that way is much simpler and less error prone.
				 */
				switch (clientMessage.getMessageType()) {
				case TOKEN:
					messageHandler = new TokenHandler(clientMessage, mutex);
					break;
				case NEW_NODE:
					messageHandler = new NewNodeHandler(clientMessage);
					break;
				case WELCOME:
					messageHandler = new WelcomeHandler(clientMessage);
					break;
				case SORRY:
					messageHandler = new SorryHandler(clientMessage);
					break;
				case UPDATE:
					messageHandler = new UpdateHandler(clientMessage);
					break;
				case NODE_STATUS:
					messageHandler = new NodeStatusHandler(clientMessage);
					break;
				case STATUS_ALIVE:
					messageHandler = new StatusAliveHandler(clientMessage);
					break;
				case STATUS_DEAD:
					messageHandler = new StatusDeadHandler(clientMessage);
					break;
				case KEY:
					messageHandler = new KeyHandler(clientMessage);
					break;
				case PULL_REQUEST:
					messageHandler = new PullRequestHandler(clientMessage);
					break;
				case PULL_RESPONSE:
					messageHandler = new PullResponseHandler(clientMessage);
					break;
				}
				
				threadPool.submit(messageHandler);
			} catch (SocketTimeoutException timeoutEx) {
				//Uncomment the next line to see that we are waking up every second.
//				AppConfig.timedStandardPrint("Waiting...");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void stop() {
		this.working = false;
	}

}
