package servent.message;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import app.AppConfig;
import app.ServentInfo;

/**
 * A default message implementation. This should cover most situations.
 * If you want to add stuff, remember to think about the modificator methods.
 * If you don't override the modificators, you might drop stuff.
 * @author bmilojkovic
 *
 */
public class BasicMessage implements Message {

	private static final long serialVersionUID = -9075856313609777945L;
	private final MessageType type;
	private final int senderPort;
	private final int receiverPort;

	private final List<ServentInfo> routeList;
	private final String messageText;
	
	//This gives us a unique id - incremented in every natural constructor.
	private static AtomicInteger messageCounter = new AtomicInteger(0);
	private final int messageId;
	
	public BasicMessage(MessageType type, int senderPort, int receiverPort) {
		this.type = type;
		this.senderPort = senderPort;
		this.receiverPort = receiverPort;
		routeList = new ArrayList<>();
		this.messageText = "";
		
		this.messageId = messageCounter.getAndIncrement();
	}
	
	public BasicMessage(MessageType type, int senderPort, int receiverPort, String messageText) {
		this.type = type;
		this.senderPort = senderPort;
		this.receiverPort = receiverPort;
		this.routeList = new ArrayList<>();
		this.messageText = messageText;
		
		this.messageId = messageCounter.getAndIncrement();
	}

	public BasicMessage(MessageType type, int senderPort, int receiverPort,
						List<ServentInfo> routeList, String messageText, int messageId) {

		this.type = type;
		this.senderPort = senderPort;
		this.receiverPort = receiverPort;
		this.routeList = routeList;
		this.messageText = messageText;

		this.messageId = messageId;

	}
	
	@Override
	public MessageType getMessageType() {
		return type;
	}
	
	@Override
	public int getReceiverPort() {
		return receiverPort;
	}
	
	@Override
	public String getReceiverIpAddress() {
		return "localhost";
	}
	
	@Override
	public int getSenderPort() {
		return senderPort;
	}

	@Override
	public List<ServentInfo> getRoute() {
		return routeList;
	}
	@Override
	public String getMessageText() {
		return messageText;
	}

	@Override
	public int getMessageId() {
		return messageId;
	}

	@Override
	public Message makeMeASender() {
		ServentInfo newRouteItem = AppConfig.myServentInfo;

		List<ServentInfo> newRouteList = new ArrayList<>(routeList);
		newRouteList.add(newRouteItem);
		Message toReturn = new BasicMessage(getMessageType(), getSenderPort(),
				getReceiverPort(), newRouteList, getMessageText(), getMessageId());

		return toReturn;
	}

	/**
	 * Change the message received based on ID. The receiver has to be our neighbor.
	 * Use this when you want to send a message to multiple neighbors, or when resending.
	 */
	@Override
	public Message changeReceiver(Integer newReceiverPort) {
		Message toReturn = null;
		for (int i = 0; i < AppConfig.customState.getSuccessorTable().length; i++) {
			if (AppConfig.customState.getSuccessorTable()[i].getListenerPort() == newReceiverPort) {
				toReturn = new BasicMessage(getMessageType(), getSenderPort(),
						newReceiverPort, getRoute(), getMessageText(), getMessageId());

			}
			if (AppConfig.customState.getPredecessorTable()[i].getListenerPort() == newReceiverPort) {
				toReturn = new BasicMessage(getMessageType(), getSenderPort(),
						newReceiverPort, getRoute(), getMessageText(), getMessageId());
			}
		}
		return toReturn;
	}

	/**
	 * Comparing messages is based on their unique id and the original sender port.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BasicMessage) {
			BasicMessage other = (BasicMessage)obj;
			
			if (getMessageId() == other.getMessageId() &&
				getSenderPort() == other.getSenderPort()) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Hash needs to mirror equals, especially if we are gonna keep this object
	 * in a set or a map. So, this is based on message id and original sender id also.
	 */
	@Override
	public int hashCode() {
		return Objects.hash(getMessageId(), getSenderPort());
	}
	
	/**
	 * Returns the message in the format: <code>[sender_id|sender_port|message_id|text|type|receiver_port|receiver_id]</code>
	 */
	@Override
	public String toString() {
		return "[" + AppConfig.customState.customHash(getSenderPort()) + "|" + getSenderPort() + "|" + getMessageId() + "|" +
					getMessageText() + "|" + getMessageType() + "|" +
					getReceiverPort() + "|" + AppConfig.customState.customHash(getReceiverPort()) + "]";
	}

}
