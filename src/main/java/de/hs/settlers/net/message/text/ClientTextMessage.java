package de.hs.settlers.net.message.text;

import de.hs.settlers.net.message.ClientMessage;

public interface ClientTextMessage extends ClientMessage<String> {
	/**
	 * @return the class type of the expected server answer
	 */
	public Class<? extends ServerTextMessage> getAnswerType();

	/**
	 * sets the answer to this request
	 * 
	 * @param answer
	 */
	public void setAnswer(ServerTextMessage answer);

	/**
	 * @return the answer to this request
	 */
	public ServerTextMessage getAnswer();
}
