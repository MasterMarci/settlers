package de.hs.settlers.components;

import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import de.hs.settlers.SettlersApplication;
import de.hs.settlers.gui.DebugViewController;
import de.hs.settlers.state.AbstractState;

public class GuiComponent extends AbstractComponent {

	public GuiComponent(SettlersApplication app) {
		super("gui", app);
	}
	
	@Override
	public void onStateChanged(AbstractState oldState, AbstractState newState) {
		if (newState == getApplication().getStates().NOT_CONNECTED) {
			getApplication().showView(getApplication().loadView("loadingview.fxml"));
		}
		if (newState == getApplication().getStates().CONNECTED) {
			getApplication().showView(getApplication().loadView("loginview.fxml"));
		}
		if (newState == getApplication().getStates().LOBBY) {
			getApplication().showView(getApplication().loadView("lobbyview.fxml"));
			getApplication().getPrimaryStage().setTitle("Settlers of Catan - Lobby");
			getApplication().getMapManager().updateMaps();
			if (!getApplication().getGameManager().isListenersRegistered()) {
				getApplication().getGameManager().updateGames();
			}
		}
		if (newState == getApplication().getStates().PLAYING) {
			getApplication().showView(getApplication().loadView("gameview.fxml"));
		}
	}
	
	@Override
	public void boot() {
		getApplication().preloadView("loadingview.fxml");
		Stage primaryStage = getApplication().getPrimaryStage();
		if (getApplication().isDebug()) {
			DebugViewController debugController = (DebugViewController) getApplication().loadView("debugview.fxml");
			Stage debugStage = new Stage();
			debugStage.setScene(new Scene((Parent) debugController.getRootNode()));
			debugStage.show();
			debugStage.setX(0);
			debugStage.setY(0);
			debugStage.setTitle("Debug");
		}

		Font.loadFont((this.getClass().getResource("/styles/IMMORTAL.ttf")).toExternalForm(), 20);
		
		AnchorPane primaryPane = new AnchorPane();
		getApplication().setPrimaryPane(primaryPane);
		primaryPane.setId("centralPane");
		Scene primaryScene = new Scene(primaryPane);
		primaryScene.getStylesheets().add(getClass().getResource("/styles/main.css").toExternalForm());
		primaryStage.setScene(primaryScene);
		primaryStage.setWidth(900);
		primaryStage.setHeight(700);
		primaryStage.setMinWidth(899);
		primaryStage.setMinHeight(699);

		primaryStage.setTitle("Settlers of Catan");
		Image img = new Image("/images/settlersIcon.png");
		primaryStage.getIcons().add(img);
		primaryStage.show();
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent e) {
				// TODO forward to current state and ask if OK
				e.consume();
				try {
					getApplication().stop();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		
		(new Thread() {
			@Override
			public void run() {
				getApplication().preloadView("loginview.fxml");
				getApplication().preloadView("lobbyview.fxml");
				getApplication().preloadView("creategameview.fxml");
			};
		}).start();
	}
}
