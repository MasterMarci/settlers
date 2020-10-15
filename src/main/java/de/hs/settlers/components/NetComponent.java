package de.hs.settlers.components;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import de.hs.settlers.configuration.Configuration;
import de.hs.settlers.SettlersApplication;
import de.hs.settlers.model.Player;
import de.hs.settlers.model.dynamic.DynamicModel;
import de.hs.settlers.net.ChatReceiveMessageFilter;
import de.hs.settlers.net.ClientCommunication;
import de.hs.settlers.net.JsonCommunication;
import de.hs.settlers.net.TextCommunication;
import de.hs.settlers.net.communication.RegisteredMsgHandler;
import de.hs.settlers.net.event.NetworkEventListener;
import de.hs.settlers.net.message.text.IdMessage;
import de.hs.settlers.net.message.text.JoinGameMessage;
import de.hs.settlers.net.message.text.LogoutMessage;
import de.hs.settlers.state.AbstractState;

public class NetComponent extends AbstractComponent {
	private JoinGameListener joinGameListener;

	public NetComponent(SettlersApplication app) {
		super("net", app);
		joinGameListener = new JoinGameListener();
	}
	
	@Override
	public void onStateChanged(AbstractState oldState, final AbstractState newState) {
		if (newState == getApplication().getStates().NOT_CONNECTED) {
			startNotConnected(newState);
		}
		if (newState == getApplication().getStates().CONNECTED) {
			startConnected();
		}
		if (newState == getApplication().getStates().LOBBY) {
			getApplication().getTextCommunicationProtocol().getEventManager().registerListener(joinGameListener);
		}
		if (oldState == getApplication().getStates().LOBBY) {
			getApplication().getTextCommunicationProtocol().getEventManager().unregisterListener(joinGameListener);
		}
		if (newState == getApplication().getStates().PLAYING) {
			startPlaying();
		}
	}

	protected void startPlaying() {
		getApplication().setJsonCommunicationProtocol(new JsonCommunication());
		DynamicModel model = getApplication().getJsonCommunicationProtocol().getModel();
		model.getCollection("SettlersOfCatanGame").getAsListProperties().add("users");
		model.getCollection("Team").getAsListProperties().add("users");
		model.getCollection("Map").getAsListProperties().add("fields");
		model.getCollection("User").getAsListProperties().add("messages");
		getApplication().getClientCommunication().setCommunication(getApplication().getJsonCommunicationProtocol());
	}

	protected void startConnected() {
		getApplication().getTextCommunicationProtocol().getEventManager().registerListener(new NetworkEventListener<IdMessage>(IdMessage.class) {
			@Override
			public void onMessage(IdMessage message) {
				getApplication().setCurrentState(getApplication().getStates().LOBBY);

				Player user = new Player();
				user.setName(message.getUserName());
				user.setId(message.getTeamId());
				user.setOnline(true);
				user.setTeam(message.getTeamName());
				getApplication().setUser(user);
			}
		});
	}

	protected void startNotConnected(final AbstractState newState) {
		(new Thread() {
			@Override
			public void run() {
				// basic communication
				ClientCommunication com = new ClientCommunication();
				com.setApplication(getApplication());
				Socket sock;
				getApplication().setClientCommunication(com);
				getApplication().setTextCommunicationProtocol(new TextCommunication());
				getApplication().getTextCommunicationProtocol().addFilter(new ChatReceiveMessageFilter());
				com.setCommunication(getApplication().getTextCommunicationProtocol());
				try
				{
					sock = new Socket(Configuration.SERVER_ADDRESS.getValue(), Configuration.SERVER_PORT.getValue());
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					}
					com.setReader(new BufferedReader(new InputStreamReader(sock.getInputStream())));
					com.setWriter(new BufferedWriter(new OutputStreamWriter(sock.getOutputStream())));
				} catch (UnknownHostException e)
				{
					newState.displayError("Connection to server failed: " + Configuration.SERVER_ADDRESS.getValue()
							+ " : " + Configuration.SERVER_PORT.getValue() + ".\nCheck connection or server settings.");
					e.printStackTrace();
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// add different message handler
				RegisteredMsgHandler.registerMsgHandlers(getApplication().getTextCommunicationProtocol());
				com.setActive(true);
			};
		}).start();
	}
	
	private class JoinGameListener extends NetworkEventListener<JoinGameMessage> {
		public JoinGameListener() {
			super(JoinGameMessage.class);
		}

		@Override
		public void onMessage(JoinGameMessage message) {
			getApplication().setCurrentState(getApplication().getStates().PLAYING);
		}
	}
	
	@Override
	public void shutdown() {
		getApplication().getTextCommunicationProtocol().sendMessage(new LogoutMessage());

		if(getApplication().getCurrentState() != getApplication().getStates().NOT_CONNECTED)
			getApplication().getTextCommunicationProtocol().setActive(false);
	}
	

}
