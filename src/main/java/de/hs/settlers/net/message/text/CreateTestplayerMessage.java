package de.hs.settlers.net.message.text;

import de.hs.settlers.util.ParseUtils;
import de.hs.settlers.util.ParseUtils.ParseResult;

public class CreateTestplayerMessage extends BidirectionalMessage {

	String nick;
	String password;
	
	@Override
	public String getData() {
		return "CREATE TESTPLAYER";
	}

	@Override
	public void setData(String... lines) {
		ParseResult data = ParseUtils.parseKeyValueLine(lines[0]);
		if (data.getObjectName().equalsIgnoreCase("TEMPORARY USER")) {
			nick = data.get("NICK");
			password = data.get("PASSWORD");
		}
	}
	
	public String getNick() {
		return nick;
	}
	
	public String getPassword() {
		return password;
	}
}
