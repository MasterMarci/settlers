package de.hs.settlers.gui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import de.hs.settlers.IApplicationHolder;
import de.hs.settlers.SettlersApplication;
import de.hs.settlers.model.Game;

public class GameContextMenu extends ContextMenu implements IApplicationHolder {
	Game game;
	SimpleObjectProperty<SettlersApplication> app = new SimpleObjectProperty<>();

	public GameContextMenu(Game game, SettlersApplication app) {
		super();
		this.game = game;
		setApplication(app);
		MenuItem join = new MenuItem("Join");
		MenuItem spectate = new MenuItem("Spectate");
		getItems().addAll(join, spectate);

		join.setOnAction(new GameJoiner<ActionEvent>(game, false, getApplication()));

		spectate.setOnAction(new GameJoiner<ActionEvent>(game, true, getApplication()));
	}

	@Override
	public SettlersApplication getApplication() {
		return app.get();
	}

	@Override
	public void setApplication(SettlersApplication application) {
		app.set(application);
	}

	@Override
	public ObjectProperty<SettlersApplication> applicationProperty() {
		return app;
	}

}
