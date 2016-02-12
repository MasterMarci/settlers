package de.hs.settlers.gui;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import de.hs.settlers.ApplicationHolder;
import de.hs.settlers.SettlersApplication;
import de.hs.settlers.model.Game;
import de.hs.settlers.net.message.text.JoinGameMessage;

public class GameJoiner<T extends Event> extends ApplicationHolder implements EventHandler<T> {
	private Game game;
	private boolean spectate;

	public GameJoiner(Game game, boolean spectate, SettlersApplication app) {
		this.game = game;
		this.spectate = spectate;
		setApplication(app);
	}

	@Override
	public void handle(T e) {
		System.out.println(e + " " + getApplication() + " " + game);
		if (e instanceof MouseEvent) {
			MouseEvent me = (MouseEvent) e;
			if (me.getClickCount() < 2 || !me.getButton().equals(MouseButton.PRIMARY)) {
				return;
			}
		}
		if (e instanceof KeyEvent) {
			if (!((KeyEvent) e).getCode().equals(KeyCode.ENTER)) {
				return;
			}
		}
		e.consume();
		getApplication().getTextCommunicationProtocol().sendMessage(new JoinGameMessage(game.getGameTitle(), spectate));
	}
}