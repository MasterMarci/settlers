package de.hs.settlers.net.event;

import de.hs.settlers.net.message.ClientMessage;

public abstract class NetworkErrorListener<T extends ClientMessage> {
	private Class<T> type;

	public static abstract class UnexpectedError implements ClientMessage {
	}

	/**
	 * Creates a network error listener for the given type.
	 * 
	 * @param type
	 *            the type to filter for. <br>Use {@link UnexpectedError} .class
	 *            when you want to receive unexpected errors.
	 */
	public NetworkErrorListener(Class<T> type) {
		this.type = type;
	}

	@SuppressWarnings("unchecked")
	public void handle(ClientMessage message, String error) {
		onError((T) message, error);
	}

	/**
	 * Called whenever an error occurs
	 * 
	 * @param message
	 *            the client message that is responsible for the error. Is null
	 *            when unexpected
	 * @param error
	 *            the error string from the server
	 */
	public abstract void onError(T message, String error);

	public Class<T> getType() {
		return type;
	}
}
