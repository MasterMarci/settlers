package de.hs.settlers.net.message;

import static org.junit.Assert.assertEquals;
import junit.framework.Assert;

import org.junit.Test;

import de.hs.settlers.net.message.text.ChatReceiveMessage;
import de.hs.settlers.net.message.text.ListGamesMessage;

public class MessageParseTest {
	@Test
	public void testChatReceiveMessage() {
		ChatReceiveMessage message = new ChatReceiveMessage();
		message.setData("MSG FROM user1 TO YOU: \"Hello World\"");

		assertEquals("user1", message.getSender());
		assertEquals("Hello World", message.getMessage());
		assertEquals(ChatReceiveMessage.ReceiveType.YOU, message.getRecipient());
	}
	
	@Test
	public void testListGamesMessage() {
		ListGamesMessage message = new ListGamesMessage();
		message.setData("GAME NAME=w00t EVENTS=0 MAPNAME=map_balanced PLAYERS=0/2 STATUS=JOINING TESTGAME=false");
		
		Assert.assertEquals("w00t", message.getGames().getFirst().get("NAME"));
		Assert.assertEquals("0", message.getGames().getFirst().get("EVENTS"));
		Assert.assertEquals("map_balanced", message.getGames().getFirst().get("MAPNAME"));
		Assert.assertEquals("0/2", message.getGames().getFirst().get("PLAYERS"));
		Assert.assertEquals("JOINING", message.getGames().getFirst().get("STATUS"));
		Assert.assertEquals("false", message.getGames().getFirst().get("TESTGAME"));
	}
}
