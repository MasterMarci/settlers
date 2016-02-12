package de.hs.settlers.state;

import de.hs.settlers.ApplicationHolder;
import de.hs.settlers.SettlersApplication;

public class States extends ApplicationHolder {
	public final NotConnectedState NOT_CONNECTED = new NotConnectedState();
	public final ConnectedState CONNECTED = new ConnectedState();
	public final LobbyState LOBBY = new LobbyState();
	public final PlayingState PLAYING = new PlayingState();

	private static boolean initLock = false;

	public States(SettlersApplication app) {
		setApplication(app);
		initStates();
	}

	private void initStates() {
		if (initLock) {
			return;
		}
		initLock = true;
		initState(NOT_CONNECTED, getApplication());
		initState(CONNECTED, getApplication());
		initState(LOBBY, getApplication());
		initState(PLAYING, getApplication());
	}

	private void initState(AbstractState state, SettlersApplication app) {
		state.setApplication(app);
		try {
			state.init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
