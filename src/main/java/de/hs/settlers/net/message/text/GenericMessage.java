package de.hs.settlers.net.message.text;

public class GenericMessage extends BidirectionalMessage {
	private String data;

	public GenericMessage() {
	}

	public GenericMessage(String data) {
		super();
		this.data = data;
	}

	@Override
	public String getData() {
		return data;
	}

	@Override
	public void setData(String... lines) {

	}

}
