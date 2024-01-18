package servent.message;

import app.ServentInfo;

import java.util.List;
import java.util.Map;

public class WelcomeMessage extends BasicMessage {

	private static final long serialVersionUID = -8981406250652693908L;

	private List<ServentInfo> allNodeInfo;

	public WelcomeMessage(int senderPort, int receiverPort, List<ServentInfo> allNodeInfo) {
		super(MessageType.WELCOME, senderPort, receiverPort);

		this.allNodeInfo = allNodeInfo;
	}

	public List<ServentInfo> getAllNodeInfo() {
		return allNodeInfo;
	}
}
