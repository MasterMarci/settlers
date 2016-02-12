package de.hs.settlers.net.message;

public abstract class BasicClientMessage<PARSETYPE> implements ClientMessage<PARSETYPE> {
	private boolean sent = false;

	@Override
	public void setSent() {
		sent = true;
	}

	@Override
	public boolean isSent() {
		return sent;
	}
}
