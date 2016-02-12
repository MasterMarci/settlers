package de.hs.settlers.net.message;

public interface ClientMessage<PARSETYPE> extends Message<PARSETYPE> {
	/**
	 * @return the data that has to be sent
	 */
	public PARSETYPE getData();

	/**
	 * sets the sent property to true
	 */
	public void setSent();

	/**
	 * @return if this message has already been sent
	 */
	public boolean isSent();
}
