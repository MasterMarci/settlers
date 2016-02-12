package de.hs.settlers.net.message;

public interface ServerMessage<PARSETYPE> extends Message<PARSETYPE> {
	/**
	 * Sets the read data. Implement by parsing the data locally
	 */
	public void setData(PARSETYPE toParse);
}
