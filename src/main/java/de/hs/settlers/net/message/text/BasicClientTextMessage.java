package de.hs.settlers.net.message.text;

import de.hs.settlers.net.message.BasicClientMessage;

public abstract class BasicClientTextMessage extends BasicClientMessage<String> implements ClientTextMessage {

	private ServerTextMessage answer = null;

	@Override
	public void setAnswer(ServerTextMessage answer) {
		this.answer = answer;
	}

	@Override
	public ServerTextMessage getAnswer() {
		return this.answer;
	}
}
