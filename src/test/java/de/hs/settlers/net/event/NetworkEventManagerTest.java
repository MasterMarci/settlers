package de.hs.settlers.net.event;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.hs.settlers.net.event.NetworkErrorListener.UnexpectedError;
import de.hs.settlers.net.message.ServerMessage;
import de.hs.settlers.net.message.text.ChatReceiveMessage;
import de.hs.settlers.net.message.text.ChatSendMessage;

public class NetworkEventManagerTest {
	boolean allMessageReceived, filteredMessaageReceived, unexpectedErrorReceived, expectedErrorReceived;
	NetworkEventManager manager;

	@Before
	public void init() {
		allMessageReceived = false;
		filteredMessaageReceived = false;
		unexpectedErrorReceived = false;
		expectedErrorReceived = false;
		manager = new NetworkEventManager();
	}

	@Test
	public void testNetworkEvent() {
		NetworkEventListener<ServerMessage> allListener = new NetworkEventListener<ServerMessage>(ServerMessage.class) {

			@Override
			public void onMessage(ServerMessage message) {
				allMessageReceived = true;
			}
		};

		manager.registerListener(allListener);

		NetworkEventListener<ChatReceiveMessage> filterListener = new NetworkEventListener<ChatReceiveMessage>(ChatReceiveMessage.class) {
			@Override
			public void onMessage(ChatReceiveMessage message) {
				filteredMessaageReceived = true;
			}
		};

		manager.registerListener(filterListener);

		manager.callMessage(new ChatReceiveMessage());

		assertTrue(allMessageReceived);
		assertTrue(filteredMessaageReceived);
	}

	@Test
	public void testNetworkError() {
		NetworkErrorListener<NetworkErrorListener.UnexpectedError> unexpectedListener = new NetworkErrorListener<NetworkErrorListener.UnexpectedError>(UnexpectedError.class) {
			@Override
			public void onError(de.hs.settlers.net.event.NetworkErrorListener.UnexpectedError message, String error) {
				unexpectedErrorReceived = true;
			}
		};
		manager.registerListener(unexpectedListener);

		NetworkErrorListener<ChatSendMessage> expectedListener = new NetworkErrorListener<ChatSendMessage>(ChatSendMessage.class) {
			@Override
			public void onError(ChatSendMessage message, String error) {
				expectedErrorReceived = true;
			}
		};

		manager.registerListener(expectedListener);

		manager.callError(new ChatSendMessage(), "User not online");
		manager.callError(null, "Timeout");

		assertTrue(expectedErrorReceived);
		assertTrue(unexpectedErrorReceived);
	}
}
