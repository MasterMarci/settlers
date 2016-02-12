package de.hs.settlers.net.message.text;

import de.hs.settlers.net.message.ServerMessage;

public interface ServerTextMessage extends ServerMessage<String> {
	/**
	 * sets the client message this server message answers to
	 * 
	 * @param message
	 */
	public void setAnswerTo(ClientTextMessage message);

	/**
	 * @return the client message this server message answers to (this may be
	 *         null)
	 */
	public ClientTextMessage getAnswerTo();
}
