package de.hs.settlers.net.message.text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hs.settlers.SettlersApplication;

public class ChatReceiveMessage extends BasicServerTextMessage {

	private String message;
	private String sender;
	private Object recipient;

	public static final Pattern MSG_PATTERN;

	static {
		MSG_PATTERN = Pattern
				.compile("^MSG FROM (\\S*|ME) TO (ALL|TEAM|YOU|\\S*): \"(.*)\"$");
	}

	@Override
	public void setData(String... lines) {
		Matcher matcher = MSG_PATTERN.matcher(lines[0]);
		if (!matcher.matches()) {
			throw new IllegalStateException(
					"Invalid command; the pattern did not match the given string '"
							+ lines[0] + "'.");
		}
		sender = matcher.group(1);
		try {
			recipient = ReceiveType.valueOf(matcher.group(2));
		} catch (Exception e) {
			recipient = matcher.group(2);
		}
		message = matcher.group(3);

		SettlersApplication.CHAT.info("[" + recipient.toString() + "] <" + sender + ">: " + message);
	}

	public String getMessage() {
		return message;
	}

	public String getSender() {
		return sender;
	}

	/**
	 * @return either a {@link String} with the players name or a
	 *         {@link ReceiveType}
	 */
	public Object getRecipient() {
		return recipient;
	}

	public enum ReceiveType {
		YOU, ALL, TEAM;
	}
}
