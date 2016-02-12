package de.hs.settlers.net.message.text;

import de.hs.settlers.model.Recipient;

public class ChatSendMessage extends BidirectionalMessage {

	private String message;
	private Recipient recipient;
	
	public ChatSendMessage() {
		
	}

	public ChatSendMessage(Recipient recipient, String message) {
		this.message = message;
		this.recipient = recipient;
	}

	public String getMessage() {
		return message;
	}

	public Recipient getRecipient() {
		return recipient;
	}

	@Override
	public String getData() {
		StringBuilder sb = new StringBuilder();
		sb.append("MSG ")
				.append(getRecipient().getRecipientString())
				.append(" ")
				.append(getMessage());
		return sb.toString();
	}

	@Override
	public void setData(String... lines) {

		// SettlersApplication.LOGGER.info("[Chat to " + receiveType.toString()
		// + "] <" + sender + ">: " + message);
	}
}
