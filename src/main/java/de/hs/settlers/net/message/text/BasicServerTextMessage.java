package de.hs.settlers.net.message.text;


public abstract class BasicServerTextMessage implements ServerTextMessage {
	@Override
	public void setData(String toParse) {
		if (toParse != null) {
			setData(toParse.split("/n"));
		}
	}

	public abstract void setData(String... lines);

	private ClientTextMessage answerTo = null;

	@Override
	public void setAnswerTo(ClientTextMessage message) {
		this.answerTo = message;
	}

	@Override
	public ClientTextMessage getAnswerTo() {
		return this.answerTo;
	}
}
