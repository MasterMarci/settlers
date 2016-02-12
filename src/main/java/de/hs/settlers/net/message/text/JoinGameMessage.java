package de.hs.settlers.net.message.text;

public class JoinGameMessage extends BidirectionalMessage {
	private String gameName;
	private boolean visitor;

	public JoinGameMessage(String gameName, boolean visitor) {
		this.gameName = gameName.replaceAll(" ", "\u00A0");
		this.visitor = visitor;
	}

	public JoinGameMessage() {
	}

	@Override
	public String getData() {
		StringBuilder sb = new StringBuilder();
		sb.append("JOIN GAME ").append(gameName).append(" -visitor ").append(visitor);
		return sb.toString();
	}

	@Override
	public void setData(String... lines) {
	}

}
