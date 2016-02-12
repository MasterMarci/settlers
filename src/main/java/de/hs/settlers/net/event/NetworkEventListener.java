package de.hs.settlers.net.event;

import de.hs.settlers.net.message.ServerMessage;

public abstract class NetworkEventListener<T extends ServerMessage> {
	private Class<T> type;

	public NetworkEventListener(Class<T> type) {
		this.type = type;
	}

	@SuppressWarnings("unchecked")
	public void handle(ServerMessage message) {
		onMessage((T) message);
	}

	public abstract void onMessage(T message);

	public Class<T> getType() {
		return type;
	}
}
