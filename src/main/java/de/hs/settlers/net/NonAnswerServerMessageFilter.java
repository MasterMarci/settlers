package de.hs.settlers.net;

import de.hs.settlers.net.message.ServerMessage;

public interface NonAnswerServerMessageFilter {
	/**
	 * @return the type of the non answer message
	 */
	public Class<? extends ServerMessage<String>> getType();

	/**
	 * @param line
	 *            the received message
	 * @return if this message matches the filter
	 */
	public boolean matches(String line);
}
