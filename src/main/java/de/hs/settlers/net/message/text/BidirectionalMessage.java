package de.hs.settlers.net.message.text;


public abstract class BidirectionalMessage extends BasicServerTextMessage implements ClientTextMessage {
	private ServerTextMessage answer = null;
	private boolean sent = false;

	@Override
	public void setSent() {
		sent = true;
	}

	@Override
	public boolean isSent() {
		return sent;
	}

	@Override
	public void setAnswer(ServerTextMessage answer) {
		this.answer = answer;
	}

	@Override
	public ServerTextMessage getAnswer() {
		return this.answer;
	}

	@Override
	public Class<? extends ServerTextMessage> getAnswerType() {
		return this.getClass();
	}

}
