package de.hs.settlers.model;

import java.util.LinkedHashSet;
import java.util.LinkedList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import de.hs.settlers.ApplicationHolder;
import de.hs.settlers.SettlersApplication;
import de.hs.settlers.net.event.PollingNetworkEventListener;
import de.hs.settlers.net.message.text.ListGamesMessage;
import de.hs.settlers.util.AssociationUtils;
import de.hs.settlers.util.ParseUtils.ParseResult;

public class GameManager extends ApplicationHolder {

	private ObservableSet<Game> games = FXCollections
			.observableSet(new LinkedHashSet<Game>());

	private ListGamesMessageListener listGamesMessageListener; // = new
																// ListGamesMessageListener(ListGamesMessage.class);

	public PollingNetworkEventListener<ListGamesMessage> getListGamesMessageListener() {
		return listGamesMessageListener;
	}

	private boolean listenersRegistered;

	public boolean isListenersRegistered() {
		return listenersRegistered;
	}

	public Game getGameWithName(String name) {
		for (Game game : games) {
			if (game.getGameTitle().equals(name)) {
				return game;
			}
		}
		return null;
	}

	{
		AssociationUtils.manyInMe(this, games, "gameManager", Game.class);
	}

	public ObservableSet<Game> getGames() {
		return games;
	}

	private final class ListGamesMessageListener extends
			PollingNetworkEventListener<ListGamesMessage> {
		private ListGamesMessageListener(Class<ListGamesMessage> type, SettlersApplication settlersApplication) {
			super(type, ListGamesMessage.class, 1000, settlersApplication);
		}

		@Override
		public void onPollMessage(ListGamesMessage message) {

			LinkedList<Game> data = new LinkedList<>();
			LinkedList<Game> toDelete = new LinkedList<>();

			for (ParseResult p : message.getGames()) {
				if (p.getObjectName().equals("GAME")) {
					Game game = getGameWithName(p.get("NAME"));
					if (game == null) {
						game = new Game();
					}
					data.add(game);
					game.setGameTitle(p.get("NAME"));
					p.get("EVENTS");
					if (getApplication().getMapManager().getMapWithName(p.get("MAPNAME")) != null) {
						if (p.get("MAPNAME") != null
								&& p.get("MAPNAME").length() > 0)
						{
							game.setMap(getApplication().getMapManager().getMapWithName(p.get("MAPNAME")));
						}
					}
					String[] fromTo = p.get("PLAYERS").split("/");
					game.setPlayersJoin(Integer.parseInt(fromTo[0]));
					game.setMaxPlayers(Integer.parseInt(fromTo[1]));
					game.setStatus(p.get("STATUS"));
					game.setTestGame(Boolean.parseBoolean(p
							.get("TESTGAME")));
				}
			}

			for (Game g : games) {
				if (!data.contains(g)) {
					toDelete.add(g);
				}
			}
			for (Game g : toDelete) {
				games.remove(g);
			}

			for (Game g : data) {
				if (!games.contains(g)) {
					games.add(g);
				}
			}

		}
	}

	public void registerListeners() {
		if (!listenersRegistered) {
			listGamesMessageListener = new ListGamesMessageListener(ListGamesMessage.class, getApplication());
			getApplication().getTextCommunicationProtocol().getEventManager().registerListener(listGamesMessageListener);
			listenersRegistered = true;
		}
	}

	public void updateGames() {
		registerListeners();
		getApplication().getTextCommunicationProtocol().sendMessage(new ListGamesMessage());

	}
}
