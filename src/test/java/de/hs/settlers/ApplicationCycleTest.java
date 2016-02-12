package de.hs.settlers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.hs.settlers.net.event.NetworkEventListener;
import de.hs.settlers.net.message.text.CreateGameMessage;
import de.hs.settlers.net.message.text.CreateTestplayerMessage;
import de.hs.settlers.net.message.text.JoinGameMessage;
import de.hs.settlers.net.message.text.LoginMessage;
import de.hs.settlers.state.AbstractState;

public class ApplicationCycleTest {
	
	@BeforeClass
	public static void startApp() {
		new JFXPanel(); // using this hack to init the javafx toolkit
	}
	
	SettlersApplication app;
	String gameName;
	Thread waiter;
	
	@Test
	public void testAppLifecycle() throws Throwable {
		SettlersApplication.args = new String []{"--disable", "gui"}; // don't launch the GUI
		app = new SettlersApplication();
		app.start(null);
		app.currentStateProperty().addListener(new ChangeListener<AbstractState>() {
			@Override
			public void changed(ObservableValue<? extends AbstractState> obj, AbstractState ov, AbstractState nv) {
				if (nv == app.getStates().CONNECTED) {
					// when connected, create a testplayer
					app.getTextCommunicationProtocol().getEventManager().registerListener(new TestPlayerMessageListener());
					app.getTextCommunicationProtocol().sendMessage(new CreateTestplayerMessage());
				}
				if (nv == app.getStates().LOBBY) {
					gameName = "JUnit test TEAM A - " + System.currentTimeMillis();
					app.getTextCommunicationProtocol().getEventManager().registerListener(new CreateGameMessageListener());
					app.getTextCommunicationProtocol().sendMessage(new CreateGameMessage(gameName, 3, null, true));
				}
				
				if (nv == app.getStates().PLAYING) {
					waiter.interrupt(); // test result we want to achieve
				}
			}
		});
		waiter = new Thread() {
			@Override
			public void run() {
				try {
					sleep(30000);
				} catch (InterruptedException e) {
					return;
				}
				Assert.fail();
			}
		};
	}
	
	public class TestPlayerMessageListener extends NetworkEventListener<CreateTestplayerMessage> {

		public TestPlayerMessageListener() {
			super(CreateTestplayerMessage.class);
		}

		@Override
		public void onMessage(CreateTestplayerMessage message) {
			app.getTextCommunicationProtocol().sendMessage(new LoginMessage(message.getNick(), message.getPassword()));
		}
	}
	
	public class CreateGameMessageListener extends NetworkEventListener<CreateGameMessage> {

		public CreateGameMessageListener() {
			super(CreateGameMessage.class);
		}

		@Override
		public void onMessage(CreateGameMessage message) {
			app.getTextCommunicationProtocol().sendMessage(new JoinGameMessage(gameName, false));
		}
	}
}
