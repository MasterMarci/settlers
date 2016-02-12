package de.hs.settlers.net;

import de.hs.settlers.net.message.ServerMessage;
import de.hs.settlers.net.message.text.ChatReceiveMessage;

public class ChatReceiveMessageFilter implements NonAnswerServerMessageFilter {

	@Override
	public Class<? extends ServerMessage<String>> getType() {
		return ChatReceiveMessage.class;
	}

	@Override
	public boolean matches(String line) {
		return ChatReceiveMessage.MSG_PATTERN.matcher(line).matches();
	}
}
