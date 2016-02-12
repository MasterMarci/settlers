package de.hs.settlers.model;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.hs.settlers.SettlersApplication;

public class AssociationTest {
	@Test
	public void testApplicationToChatRoom() {
		SettlersApplication app = new SettlersApplication();
		ChatRoom room1 = new ChatRoom();
		
		room1.setApplication(app);
		
		assertTrue(app.getChats().contains(room1));
		
		app.getChats().remove(room1);
		
		assertNull(room1.getApplication());
	}
}
