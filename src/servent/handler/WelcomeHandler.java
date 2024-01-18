package servent.handler;

import app.AppConfig;
import app.ServentInfo;
import app.Timer;
import servent.message.*;
import servent.message.util.MessageUtil;

import java.util.ArrayList;
import java.util.List;

public class WelcomeHandler implements MessageHandler {

	private Message clientMessage;

	public WelcomeHandler(Message clientMessage) {
		this.clientMessage = clientMessage;
	}

	@Override
	public void run() {
		if (clientMessage.getMessageType() == MessageType.WELCOME) {
			WelcomeMessage welcomeMsg = (WelcomeMessage)clientMessage;
			List<ServentInfo> allNodes = welcomeMsg.getAllNodeInfo();
			AppConfig.customState.init(welcomeMsg);

			// welcomed into system send

			allNodes.add(AppConfig.myServentInfo);

			AppConfig.customState.addNodes(allNodes);

			// Adds node at the end
			// My successors are first 2 nodes in system

			AppConfig.customState.getSuccessorTable()[0] = AppConfig.customState.getAllNodeInfo().get(0);
			AppConfig.customState.getSuccessorTable()[1] =
					(AppConfig.customState.getAllNodeInfo().get(1) != AppConfig.myServentInfo) ?
							AppConfig.customState.getAllNodeInfo().get(1) : AppConfig.customState.getSuccessorTable()[0];

			// My predecessors are 2 nodes behind me

			AppConfig.customState.getPredecessorTable()[0] = AppConfig.customState.getAllNodeInfo().
					get(AppConfig.customState.getAllNodeInfo().size() - 2);
			AppConfig.customState.getPredecessorTable()[1] = (AppConfig.customState.getAllNodeInfo().size() - 3 > 0)?
					AppConfig.customState.getAllNodeInfo().get(AppConfig.customState.getAllNodeInfo().size() - 3) :
					AppConfig.customState.getPredecessorTable()[0];

			for (int i = 0; i < AppConfig.customState.getSuccessorTable().length; i++) {
				UpdateMessage um = new UpdateMessage(AppConfig.myServentInfo.getListenerPort(),
						AppConfig.customState.getSuccessorTable()[i].getListenerPort(),
						AppConfig.customState.getAllNodeInfo());
				MessageUtil.sendMessage(um);

				um = new UpdateMessage(AppConfig.myServentInfo.getListenerPort(),
						AppConfig.customState.getPredecessorTable()[i].getListenerPort(),
						AppConfig.customState.getAllNodeInfo());
				MessageUtil.sendMessage(um);
			}


			try {
				// wait for successor to update
				Thread.sleep(3000);
				NodeStatusMessage nodeStatusMessage = new NodeStatusMessage(AppConfig.myServentInfo.getListenerPort(),
						AppConfig.customState.getSuccessorTable()[0].getListenerPort());
				MessageUtil.sendMessage(nodeStatusMessage);
				AppConfig.customState.getTimer().startTimer();
			}catch (InterruptedException e) {
				throw new RuntimeException(e);
			}

		} else {
				AppConfig.timestampedErrorPrint("Welcome handler got a message that is not WELCOME");
			}
		}
}
