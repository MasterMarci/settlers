package de.hs.settlers.net.message.text;

import de.hs.settlers.util.ParseUtils;
import de.hs.settlers.util.ParseUtils.ParseResult;

/**
 * The typical reply to the "LOGIN" and "ID" message
 */
public class IdMessage extends BasicServerTextMessage {

	private ParseResult user;
	private ParseResult team;

	@Override
	public void setData(String... lines) {
		if (lines.length != 2) {
			throw new IllegalStateException("Unexpected amount of lines");
		}
		user = ParseUtils.parseKeyValueLine(lines[0]);
		team = ParseUtils.parseKeyValueLine(lines[1]);
	}

	public String getUserName() {
		return user.get("NICK");
	}

	public String getUserEmail() {
		return user.get("EMAIL");
	}

	public String getUserRole() {
		return user.get("ROLE");
	}

	public String getTeamName() {
		return team.get("NAME");
	}

	public String getTeamId() {
		return team.get("ID");
	}
}
