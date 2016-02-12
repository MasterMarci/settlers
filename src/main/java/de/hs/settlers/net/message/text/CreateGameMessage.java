package de.hs.settlers.net.message.text;

import de.hs.settlers.model.map.Map;

public class CreateGameMessage extends BidirectionalMessage {
	private String gameName;
	private int maxPlayers;
	private Map map;
	private boolean testgame;

	public CreateGameMessage() {
	}

	public CreateGameMessage(String gameName, int maxPlayers, Map map, boolean testgame) {
		super();
		this.gameName = gameName.replaceAll(" ", "\u00A0");
		this.maxPlayers = maxPlayers;
		this.map = map;
		this.testgame = testgame;
	}

	@Override
	public String getData() {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE GAME ").append(gameName).append(" -maxPlayers ").append(maxPlayers);
		if (map != null) { // This is fine since the server will use
							// map_balanced when the map is omitted
			sb.append(" -map ").append(map.getMapTitle());
		}
		if (testgame) {
			sb.append(" -testgame");
		}
		return sb.toString();
	}

	@Override
	public void setData(String... lines) {
	}

	public String getGameName() {
		return gameName;
	}

}
