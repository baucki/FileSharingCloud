package servent.handler;

import java.util.ArrayList;
import java.util.List;

import app.AppConfig;
import app.ServentInfo;
import app.Timer;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.NodeStatusMessage;
import servent.message.UpdateMessage;
import servent.message.util.MessageUtil;

public class UpdateHandler implements MessageHandler {

	private Message clientMessage;

	public UpdateHandler(Message clientMessage) {
		this.clientMessage = clientMessage;
	}

	@Override
	public void run() {
		if (clientMessage.getMessageType() == MessageType.UPDATE) {

			// update successors and predecessors
			UpdateMessage updateMessage = (UpdateMessage) clientMessage;
			List<ServentInfo> allNodesInfoUpdated = updateMessage.getAllNodeInfo();
			AppConfig.customState.addNodes(allNodesInfoUpdated);

			for (int i = 0; i < allNodesInfoUpdated.size(); i++) {
				if (allNodesInfoUpdated.get(i).getListenerPort() == AppConfig.myServentInfo.getListenerPort()) {
					AppConfig.customState.getSuccessorTable()[0] = allNodesInfoUpdated.get((i + 1)%allNodesInfoUpdated.size());
					AppConfig.customState.getSuccessorTable()[1] = allNodesInfoUpdated.get((i + 2)%allNodesInfoUpdated.size());
					AppConfig.customState.getPredecessorTable()[0] = allNodesInfoUpdated.get((i - 1 < 0)? i + allNodesInfoUpdated.size() - 1 : i - 1);
					AppConfig.customState.getPredecessorTable()[1] = allNodesInfoUpdated.get((i - 2 < 0)? i + allNodesInfoUpdated.size() - 2 : i - 2);
					break;
				}
			}

			// ask first successor if he is alive

			try {
				Thread.sleep(3000);
				NodeStatusMessage nodeStatusMessage = new NodeStatusMessage(AppConfig.myServentInfo.getListenerPort(),
						AppConfig.customState.getSuccessorTable()[0].getListenerPort());
				MessageUtil.sendMessage(nodeStatusMessage);
				AppConfig.customState.getTimer().startTimer();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}


		} else {
			AppConfig.timestampedErrorPrint("Update message handler got message that is not UPDATE");
		}
	}

}
