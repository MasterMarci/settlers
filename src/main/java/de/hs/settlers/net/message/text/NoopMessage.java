package de.hs.settlers.net.message.text;

public class NoopMessage extends BidirectionalMessage {

	@Override
	public String getData() {
		return "NOOP";
	}

	@Override
	public void setData(String... lines) {
		// nothing to parse
	}

}
