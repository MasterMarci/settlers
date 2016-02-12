package de.hs.settlers.net.message.text;

import java.util.LinkedList;
import de.hs.settlers.util.ParseUtils;
import de.hs.settlers.util.ParseUtils.ParseResult;

public class ListGamesMessage extends BidirectionalMessage {

	LinkedList<ParseResult> games = new LinkedList<>();
	
	@Override
	public String getData() {
		return "LIST GAMES";
	}

	@Override
	public void setData(String... lines) {
		for(String line: lines) {
				if (!line.equals("NO GAMES IN PROGRESS") && line.length() > 0) {
					ParseResult result = ParseUtils.parseKeyValueLine(line);
					games.add(result);
				}
			}

	}
	
	public LinkedList<ParseResult> getGames() {
		return games;
	}

}
