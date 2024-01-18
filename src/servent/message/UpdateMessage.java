package servent.message;

import app.ServentInfo;

import java.util.List;

public class UpdateMessage extends BasicMessage {

	private static final long serialVersionUID = 3586102505319194978L;

	List<ServentInfo> allNodeInfo;

	public UpdateMessage(int senderPort, int receiverPort, List<ServentInfo> allNodeInfo) {
		super(MessageType.UPDATE, senderPort, receiverPort);
		this.allNodeInfo = allNodeInfo;
	}

	public List<ServentInfo> getAllNodeInfo() {
		return allNodeInfo;
	}
}
