package de.hs.settlers.net.message.text;

import java.util.LinkedList;

import de.hs.settlers.model.Player;
import de.hs.settlers.util.ParseUtils;
import de.hs.settlers.util.ParseUtils.ParseResult;

public class ListPlayersMessage extends BidirectionalMessage {

	LinkedList<Player> players = new LinkedList<>();
	
	@Override
	public String getData() {
		return "LIST USERS";
	}

	@Override
	public void setData(String... lines) {
		for(String line: lines) {
			ParseResult result = ParseUtils.parseKeyValueLine(line);
			Player player = new Player();
			
			player.setName(result.get("NICK"));
			player.setTeam(result.get("TEAM"));
			player.setStatus(result.get("STATE"));
			players.add(player);
		}

	}

	public LinkedList<Player> getPlayers() {
		return players;
	}

}
